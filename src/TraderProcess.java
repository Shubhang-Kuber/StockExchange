import java.util.*;

/**
 * Trader Process - Simulates a trading process with full OS integration.
 * 
 * Each TraderProcess represents an independent process in the system that:
 * - Executes trades autonomously
 * - Manages its own portfolio
 * - Communicates via IPC (message queue, shared memory)
 * - Accesses virtual memory (triggering page faults)
 * - Coordinates with semaphores
 * - Logs transactions to file system
 * - Maintains Process Control Block (PCB)
 * 
 * PROCESS LIFECYCLE:
 * 1. NEW: Process created with PCB
 * 2. READY: Waiting to be scheduled
 * 3. RUNNING: Executing trades
 * 4. WAITING: Blocked on semaphore or I/O
 * 5. TERMINATED: All trades completed
 * 
 * TRADING BEHAVIOR:
 * Each trader executes 10 trades with the following logic:
 * 1. Acquire trading semaphore (resource lock)
 * 2. Select random stock from available stocks
 * 3. Access portfolio from virtual memory (may cause page fault)
 * 4. Read current stock price from shared memory
 * 5. Decide BUY or SELL randomly
 * 6. Check if trade is valid (sufficient cash/holdings)
 * 7. Create order and send via message queue
 * 8. Update portfolio (cash and holdings)
 * 9. Log transaction to file system
 * 10. Write portfolio back to virtual memory
 * 11. Release trading semaphore
 * 12. Sleep briefly (simulate processing time)
 * 
 * OS INTEGRATION POINTS:
 * 
 * Virtual Memory:
 * - Portfolio stored in virtual pages (one per trader)
 * - accessPage() may trigger page fault
 * - writePage() updates memory with dirty bit
 * 
 * IPC - Message Queue:
 * - send() operation to submit orders
 * - Non-blocking (fails if queue full)
 * 
 * IPC - Shared Memory:
 * - read() stock prices (concurrent reads allowed)
 * - write() price updates (exclusive access)
 * 
 * Synchronization - Semaphore:
 * - acquire() before trading (may block)
 * - release() after trading
 * - Limits concurrent traders (resource pool)
 * 
 * File System:
 * - beginTransaction() for each trade
 * - commitTransaction() with WAL
 * - rollback() on failure
 * 
 * Process Control:
 * - PCB tracks state, priority, metrics
 * - Context switches recorded
 * - Performance data collected
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see ProcessControlBlock
 * @see StockExchange
 */
public class TraderProcess extends Thread {
    private int traderId;
    private ProcessControlBlock pcb;
    private Portfolio portfolio;
    private VirtualMemoryManager vmm;
    private MessageQueue orderQueue;
    private SharedMemory marketData;
    private CustomSemaphore tradingSemaphore;
    private TransactionalFS transactionLog;
    private List<Stock> availableStocks;
    private Random random;
    private int tradesExecuted;
    private boolean running;
    
    public TraderProcess(int traderId, int priority, Portfolio portfolio,
                        VirtualMemoryManager vmm, MessageQueue orderQueue,
                        SharedMemory marketData, CustomSemaphore tradingSemaphore,
                        TransactionalFS transactionLog, List<Stock> availableStocks) {
        this.traderId = traderId;
        this.pcb = new ProcessControlBlock(traderId, priority);
        this.portfolio = portfolio;
        this.vmm = vmm;
        this.orderQueue = orderQueue;
        this.marketData = marketData;
        this.tradingSemaphore = tradingSemaphore;
        this.transactionLog = transactionLog;
        this.availableStocks = availableStocks;
        this.random = new Random(traderId);
        this.tradesExecuted = 0;
        this.running = true;
        
        setName("Trader-" + traderId);
    }
    
    @Override
    public void run() {
        if (StockSimulator.VERBOSE) {
            System.out.println("  [PROCESS] Trader-" + traderId + " created (Priority=" + pcb.getPriority() + ") → NEW state");
        }
        pcb.setState(ProcessControlBlock.State.READY);
        if (StockSimulator.VERBOSE) {
            System.out.println("  [PROCESS] Trader-" + traderId + " → READY (waiting in scheduler queue)");
        }
        
        pcb.setState(ProcessControlBlock.State.RUNNING);
        if (StockSimulator.VERBOSE) {
            System.out.println("  [PROCESS] Trader-" + traderId + " → RUNNING (executing trades)");
        }
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate trading activity
            for (int i = 0; i < 10 && running; i++) {
                executeTrade();
                
                // Simulate some processing time
                Thread.sleep(random.nextInt(100) + 50);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Trader " + traderId + " interrupted");
        } catch (Exception e) {
            System.err.println("Trader " + traderId + " error: " + e.getMessage());
        } finally {
            pcb.setState(ProcessControlBlock.State.TERMINATED);
            pcb.setBurstTime(System.currentTimeMillis() - startTime);
            pcb.calculateTurnaroundTime();
            if (StockSimulator.VERBOSE) {
                System.out.println("  [PROCESS] Trader-" + traderId + " → TERMINATED (completed " + tradesExecuted + " trades)");
            }
        }
    }
    
    private void executeTrade() throws InterruptedException {
        // Acquire trading semaphore (resource lock)
        if (StockSimulator.VERBOSE) {
            System.out.println("    [SYNC] Trader-" + traderId + " waiting for trading semaphore...");
        }
        pcb.setState(ProcessControlBlock.State.WAITING);
        tradingSemaphore.acquire();
        pcb.setState(ProcessControlBlock.State.RUNNING);
        if (StockSimulator.VERBOSE) {
            System.out.println("    [SYNC] Trader-" + traderId + " acquired semaphore → RUNNING");
        }
        
        try {
            // Select random stock
            Stock stock = availableStocks.get(random.nextInt(availableStocks.size()));
            
            // Access portfolio from virtual memory (simulates page access)
            int virtualPage = traderId % 10; // Each trader has virtual pages
            if (StockSimulator.VERBOSE) {
                System.out.println("    [MEMORY] Trader-" + traderId + " accessing virtual page " + virtualPage);
            }
            vmm.accessPage(virtualPage, portfolio); // Trigger page access (may cause page fault)
            
            // Read current stock price from shared memory
            Double currentPrice = (Double) marketData.read("price_" + stock.getSymbol());
            if (currentPrice == null) {
                currentPrice = stock.getCurrentPrice();
                marketData.write("price_" + stock.getSymbol(), currentPrice);
            }
            if (StockSimulator.VERBOSE) {
                System.out.println("    [IPC] Trader-" + traderId + " read " + stock.getSymbol() + " price from shared memory: $" + String.format("%.2f", currentPrice));
            }
            
            // Decide to buy or sell
            Order.OrderType orderType = random.nextBoolean() ? Order.OrderType.BUY : Order.OrderType.SELL;
            int quantity = random.nextInt(10) + 1;
            
            // Check if trade is possible
            if (orderType == Order.OrderType.BUY) {
                double cost = quantity * currentPrice;
                if (portfolio.getCashBalance() >= cost) {
                    executeOrder(stock, orderType, quantity, currentPrice);
                }
            } else { // SELL
                if (portfolio.getHolding(stock.getSymbol()) >= quantity) {
                    executeOrder(stock, orderType, quantity, currentPrice);
                }
            }
            
        } finally {
            tradingSemaphore.release();
            if (StockSimulator.VERBOSE) {
                System.out.println("    [SYNC] Trader-" + traderId + " released semaphore");
            }
        }
    }
    
    private void executeOrder(Stock stock, Order.OrderType type, int quantity, double price) {
        // Begin transaction
        long txnId = transactionLog.beginTransaction();
        if (StockSimulator.VERBOSE) {
            System.out.println("    [FS] Trader-" + traderId + " began transaction #" + txnId);
        }
        
        // Create order
        Order order = new Order(
            (int) (txnId % 100000),
            traderId,
            stock.getSymbol(),
            type,
            quantity,
            price
        );
        
        if (StockSimulator.VERBOSE) {
            System.out.println("    [TRADE] Trader-" + traderId + " attempting " + type + " " + quantity + " " + stock.getSymbol() + " @ $" + String.format("%.2f", price));
        }
        
        // Send order via message queue
        Message orderMsg = new Message(traderId, -1, "ORDER", order);
        boolean sent = orderQueue.send(orderMsg);
        if (StockSimulator.VERBOSE) {
            System.out.println("    [IPC] Trader-" + traderId + " sent order via message queue → " + (sent ? "SUCCESS" : "FAILED"));
        }
        
        if (sent) {
            // Update portfolio
            if (type == Order.OrderType.BUY) {
                portfolio.updateCash(-quantity * price);
                portfolio.updateHolding(stock.getSymbol(), quantity);
                // Update stock price (buying increases demand → price goes up)
                double priceChange = price * 0.01 * quantity; // 1% per share
                double newPrice = stock.getCurrentPrice() + priceChange;
                stock.updatePrice(newPrice);
                marketData.write("price_" + stock.getSymbol(), newPrice);
                if (StockSimulator.VERBOSE) {
                    System.out.println("    [MARKET] " + stock.getSymbol() + " price increased: $" + String.format("%.2f", price) + " → $" + String.format("%.2f", newPrice) + " (buy pressure)");
                }
            } else {
                portfolio.updateCash(quantity * price);
                portfolio.updateHolding(stock.getSymbol(), -quantity);
                // Update stock price (selling increases supply → price goes down)
                double priceChange = price * 0.01 * quantity; // 1% per share
                double newPrice = stock.getCurrentPrice() - priceChange;
                stock.updatePrice(newPrice);
                marketData.write("price_" + stock.getSymbol(), newPrice);
                if (StockSimulator.VERBOSE) {
                    System.out.println("    [MARKET] " + stock.getSymbol() + " price decreased: $" + String.format("%.2f", price) + " → $" + String.format("%.2f", newPrice) + " (sell pressure)");
                }
            }
            
            // Commit transaction to file system
            transactionLog.commitTransaction(txnId, order);
            if (StockSimulator.VERBOSE) {
                System.out.println("    [FS] Trader-" + traderId + " committed transaction #" + txnId + " (WAL flushed)");
            }
            order.setExecuted(true);
            tradesExecuted++;
            
            // Update virtual memory
            int virtualPage = traderId % 10;
            vmm.writePage(virtualPage, portfolio);
            if (StockSimulator.VERBOSE) {
                System.out.println("    [MEMORY] Trader-" + traderId + " wrote portfolio to virtual page " + virtualPage);
            }
            
            // Context switch point - simulate
            pcb.incrementContextSwitch();
        } else {
            // Queue full - rollback
            transactionLog.rollback(txnId);
            if (StockSimulator.VERBOSE) {
                System.out.println("    [FS] Trader-" + traderId + " rolled back transaction #" + txnId + " (queue full)");
            }
        }
    }
    
    public void stopTrading() {
        running = false;
    }
    
    public ProcessControlBlock getPCB() {
        return pcb;
    }
    
    public int getTradesExecuted() {
        return tradesExecuted;
    }
    
    public Portfolio getPortfolio() {
        return portfolio;
    }
    
    @Override
    public String toString() {
        return String.format("TraderProcess[ID=%d, State=%s, Trades=%d, %s]",
            traderId, pcb.getState(), tradesExecuted, pcb);
    }
}

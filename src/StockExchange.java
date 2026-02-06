import java.util.*;

/**
 * Stock Exchange - The OS Kernel Simulator.
 * 
 * This is the central coordinator that acts as an operating system kernel,
 * managing all OS components and orchestrating the trading simulation.
 * 
 * ARCHITECTURE (OS Kernel Components):
 * 
 * 1. Process Management
 *    - CustomScheduler: CPU scheduler with multiple algorithms
 *    - TraderProcess: Simulated processes with PCBs
 *    - PerformanceMonitor: System-wide metrics
 * 
 * 2. Memory Management
 *    - VirtualMemoryManager: Paging and page replacement
 *    - Page fault handling
 *    - Thrashing detection
 * 
 * 3. Inter-Process Communication
 *    - MessageQueue: Message passing
 *    - SharedMemory: Shared memory region
 *    - CustomSemaphore: Synchronization primitive
 * 
 * 4. File System
 *    - TransactionalFS: WAL-based file system
 *    - Transaction logging
 *    - Crash recovery
 * 
 * 5. Hardware Abstraction
 *    - Stock market as I/O device
 *    - Price updates as interrupts
 * 
 * INITIALIZATION:
 * The exchange initializes all OS subsystems:
 * - Creates scheduler with chosen algorithm
 * - Allocates virtual memory manager
 * - Sets up IPC mechanisms
 * - Initializes file system
 * - Creates trader processes with PCBs
 * - Populates stock market data
 * 
 * EXECUTION:
 * 1. Start all trader processes (threads)
 * 2. Monitor system state periodically
 * 3. Update market prices (simulates I/O events)
 * 4. Check for process completion
 * 5. Generate performance report
 * 
 * MARKET SIMULATION:
 * - 5 stocks: AAPL, GOOGL, MSFT, AMZN, TSLA
 * - Prices fluctuate ±2% randomly
 * - Updates written to shared memory
 * - Traders react to price changes
 * 
 * MONITORING:
 * Tracks and reports:
 * - Per-trader statistics (trades, context switches, wait time)
 * - Scheduler performance (algorithm, context switches)
 * - Memory statistics (page faults, hit rate, thrashing)
 * - IPC metrics (message throughput, shared memory R/W)
 * - File system activity (transactions, flushes)
 * - Overall performance (throughput, execution time)
 * - Portfolio values (trading results)
 * 
 * CONFIGURATION:
 * Accepts scheduling algorithm and page replacement policy:
 * - Scheduling: FCFS, SJF, PRIORITY, ROUND_ROBIN
 * - Page Replacement: LRU, FIFO
 * 
 * This allows comparative analysis of different OS configurations.
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see CustomScheduler
 * @see VirtualMemoryManager
 * @see TraderProcess
 */
public class StockExchange {
    // OS Components
    private CustomScheduler scheduler;
    private VirtualMemoryManager vmm;
    private MessageQueue orderQueue;
    private SharedMemory marketData;
    private CustomSemaphore tradingSemaphore;
    private TransactionalFS transactionLog;
    private PerformanceMonitor monitor;
    
    // Trading Components
    private List<Stock> stocks;
    private List<TraderProcess> traders;
    private Map<Integer, Portfolio> portfolios;
    private boolean running;
    
    // Web Visualization
    private DataExporter dataExporter;
    
    public StockExchange(CustomScheduler.Algorithm schedulingAlgo, 
                        VirtualMemoryManager.ReplacementPolicy pageReplacement,
                        int numTraders) {
        // Initialize OS components
        this.scheduler = new CustomScheduler(schedulingAlgo, 100); // 100ms quantum for RR
        this.vmm = new VirtualMemoryManager(20, 100, pageReplacement); // 20 physical frames, 100 virtual pages
        this.orderQueue = new MessageQueue(1000);
        this.marketData = new SharedMemory();
        this.tradingSemaphore = new CustomSemaphore("TradingLock", 5); // Max 5 concurrent traders
        this.transactionLog = new TransactionalFS("logs/transactions.log");
        this.monitor = new PerformanceMonitor();
        
        // Initialize web visualization
        // Determine data export path (works from both src/ and root directory)
        String dataPath = new java.io.File("web/data").exists() ? "web/data" : "../web/data";
        this.dataExporter = new DataExporter(dataPath);
        this.dataExporter.clearData();
        
        // Initialize trading components
        this.stocks = new ArrayList<>();
        this.traders = new ArrayList<>();
        this.portfolios = new HashMap<>();
        this.running = false;
        
        initializeStocks();
        initializeTraders(numTraders);
    }
    
    private void initializeStocks() {
        stocks.add(new Stock("AAPL", "Apple Inc.", 150.0));
        stocks.add(new Stock("GOOGL", "Alphabet Inc.", 2800.0));
        stocks.add(new Stock("MSFT", "Microsoft Corp.", 300.0));
        stocks.add(new Stock("AMZN", "Amazon.com Inc.", 3300.0));
        stocks.add(new Stock("TSLA", "Tesla Inc.", 700.0));
        
        // Initialize stock prices in shared memory
        for (Stock stock : stocks) {
            marketData.write("price_" + stock.getSymbol(), stock.getCurrentPrice());
        }
    }
    
    private void initializeTraders(int numTraders) {
        Random random = new Random();
        
        for (int i = 0; i < numTraders; i++) {
            // Create portfolio with initial cash
            Portfolio portfolio = new Portfolio(i, 10000.0 + random.nextInt(5000));
            portfolios.put(i, portfolio);
            
            // Create trader process with random priority
            int priority = random.nextInt(10) + 1; // Priority 1-10
            TraderProcess trader = new TraderProcess(
                i, priority, portfolio, vmm, orderQueue,
                marketData, tradingSemaphore, transactionLog, stocks
            );
            
            traders.add(trader);
        }
    }
    
    public void start() {
        running = true;
        monitor.start();
        
        System.out.println("=== Stock Exchange Starting ===");
        System.out.println("Scheduler: " + scheduler.getAlgorithm());
        System.out.println("Virtual Memory: " + vmm);
        System.out.println("Traders: " + traders.size());
        System.out.println("Stocks: " + stocks.size());
        
        if (StockSimulator.VERBOSE) {
            System.out.println("\n[SCHEDULER] Initial Ready Queue:");
            for (TraderProcess trader : traders) {
                System.out.println("  - Trader-" + trader.getPCB().getProcessId() + " (Priority=" + trader.getPCB().getPriority() + ", State=NEW)");
            }
        }
        System.out.println("================================\n");
        
        // Start all trader processes
        if (StockSimulator.VERBOSE) {
            System.out.println("[KERNEL] Starting all trader processes...\n");
        }
        for (TraderProcess trader : traders) {
            trader.start();
            monitor.recordProcessStart();
            if (StockSimulator.VERBOSE) {
                try {
                    Thread.sleep(50); // Small delay to see process creation
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        if (StockSimulator.VERBOSE) {
            System.out.println("\n[KERNEL] All processes created. Beginning trading simulation...\n");
            System.out.println("=".repeat(70) + "\n");
        }
        
        // Monitor trading activity
        monitorTrading();
    }
    
    private void monitorTrading() {
        long startTime = System.currentTimeMillis();
        long lastUpdate = startTime;
        
        while (running) {
            try {
                Thread.sleep(500);
                
                // Export real-time data for visualization
                exportDataForVisualization();
                
                // Check if all traders are done
                boolean allDone = true;
                for (TraderProcess trader : traders) {
                    if (trader.getPCB().getState() != ProcessControlBlock.State.TERMINATED) {
                        allDone = false;
                        break;
                    }
                }
                
                if (allDone) {
                    running = false;
                    break;
                }
                
                // Periodic status update
                if (StockSimulator.VERBOSE) {
                    long now = System.currentTimeMillis();
                    if (now - lastUpdate > 3000) {
                        printStatus();
                        lastUpdate = now;
                    }
                }
                
                // Simulate market price updates
                updateMarketPrices();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        stop();
    }
    
    private void updateMarketPrices() {
        Random random = new Random();
        for (Stock stock : stocks) {
            // Random price fluctuation ±2%
            double change = (random.nextDouble() - 0.5) * 0.04;
            double newPrice = stock.getCurrentPrice() * (1 + change);
            stock.updatePrice(newPrice);
            marketData.write("price_" + stock.getSymbol(), newPrice);
        }
    }
    
    public void stop() {
        running = false;
        monitor.stop();
        
        // Wait for all traders to finish
        for (TraderProcess trader : traders) {
            try {
                trader.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Flush transaction log
        transactionLog.fsync();
        
        // Print final statistics
        printFinalReport();
    }
    
    private void printStatus() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("--- PERIODIC STATUS UPDATE ---");
        System.out.println("=".repeat(70));
        
        System.out.println("\n[SCHEDULER] Ready Queue State:");
        System.out.println("  Algorithm: " + scheduler.getAlgorithm());
        for (TraderProcess trader : traders) {
            ProcessControlBlock.State state = trader.getPCB().getState();
            System.out.println("  - Trader-" + trader.getPCB().getProcessId() + 
                " | Priority=" + trader.getPCB().getPriority() + 
                " | State=" + state + 
                " | Trades=" + trader.getTradesExecuted() +
                " | ContextSwitches=" + trader.getPCB().getContextSwitchCount());
        }
        
        System.out.println("\n[MEMORY] " + vmm);
        System.out.println("[IPC] " + orderQueue);
        System.out.println("[IPC] " + marketData);
        System.out.println("[SYNC] " + tradingSemaphore);
        System.out.println("[FS] " + transactionLog);
        
        System.out.println("\n[MARKET] Current Stock Prices:");
        for (Stock stock : stocks) {
            System.out.println("  " + stock.getSymbol() + ": $" + String.format("%.2f", stock.getCurrentPrice()));
        }
        
        int activeTrades = 0;
        for (TraderProcess trader : traders) {
            if (trader.getPCB().getState() == ProcessControlBlock.State.RUNNING || 
                trader.getPCB().getState() == ProcessControlBlock.State.WAITING) {
                activeTrades++;
            }
        }
        System.out.println("\nActive Traders: " + activeTrades + "/" + traders.size());
        System.out.println("=".repeat(70) + "\n");
    }
    
    private void exportDataForVisualization() {
        // System status
        int runningCount = 0;
        int completedCount = 0;
        int totalTrades = 0;
        
        for (TraderProcess trader : traders) {
            ProcessControlBlock.State state = trader.getPCB().getState();
            if (state == ProcessControlBlock.State.RUNNING || 
                state == ProcessControlBlock.State.READY || 
                state == ProcessControlBlock.State.WAITING) {
                runningCount++;
            } else if (state == ProcessControlBlock.State.TERMINATED) {
                completedCount++;
            }
            totalTrades += trader.getTradesExecuted();
        }
        
        dataExporter.exportSystemStatus(
            scheduler.getAlgorithm().toString(),
            traders.size(),
            runningCount,
            completedCount,
            monitor.getElapsedTime(),
            totalTrades
        );
        
        // Process statistics
        List<Map<String, Object>> processData = new ArrayList<>();
        for (TraderProcess trader : traders) {
            ProcessControlBlock pcb = trader.getPCB();
            Map<String, Object> p = new HashMap<>();
            p.put("id", pcb.getProcessId());
            p.put("name", "Trader-" + pcb.getProcessId());
            p.put("state", pcb.getState().toString());
            p.put("priority", pcb.getPriority());
            p.put("trades", trader.getTradesExecuted());
            p.put("contextSwitches", pcb.getContextSwitchCount());
            p.put("waitTime", pcb.getWaitingTime());
            p.put("cpuTime", pcb.getCPUTime());
            processData.add(p);
        }
        dataExporter.exportProcessStats(processData);
        
        // Memory statistics
        dataExporter.exportMemoryStats(
            vmm.getPageFaults(),
            vmm.getPageHits(),
            vmm.getPageFaultRate() * 100,
            vmm.isThrashing(),
            vmm.getUsedFrames(),
            vmm.getTotalFrames()
        );
        
        // Stock prices
        Map<String, Double> stockPrices = new HashMap<>();
        for (Stock stock : stocks) {
            stockPrices.put(stock.getSymbol(), stock.getCurrentPrice());
        }
        dataExporter.exportStockPrices(stockPrices);
        
        // Scheduler statistics
        long totalContextSwitches = 0;
        long totalWaitTime = 0;
        long totalTurnaroundTime = 0;
        int completedTasks = 0;
        
        for (TraderProcess trader : traders) {
            ProcessControlBlock pcb = trader.getPCB();
            totalContextSwitches += pcb.getContextSwitchCount();
            totalWaitTime += pcb.getWaitingTime();
            if (pcb.getState() == ProcessControlBlock.State.TERMINATED) {
                completedTasks++;
                totalTurnaroundTime += pcb.getTurnaroundTime();
            }
        }
        
        double avgWaitTime = traders.size() > 0 ? (double) totalWaitTime / traders.size() : 0;
        double avgTurnaroundTime = completedTasks > 0 ? (double) totalTurnaroundTime / completedTasks : 0;
        
        dataExporter.exportSchedulerStats(
            scheduler.getAlgorithm().toString(),
            traders.size(),
            completedTasks,
            (int) totalContextSwitches,
            avgWaitTime,
            avgTurnaroundTime
        );
        
        // IPC statistics
        dataExporter.exportIPCStats(
            orderQueue.getMessagesSent(),
            orderQueue.getMessagesReceived(),
            marketData.getReadCount(),
            marketData.getWriteCount(),
            tradingSemaphore.getAcquireCount(),
            tradingSemaphore.getReleaseCount()
        );
    }
    
    private void printFinalReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("FINAL PERFORMANCE REPORT");
        System.out.println("=".repeat(60));
        
        // Trading Statistics
        int totalTrades = 0;
        long totalContextSwitches = 0;
        long totalWaitTime = 0;
        
        System.out.println("\n--- Trader Statistics ---");
        for (TraderProcess trader : traders) {
            ProcessControlBlock pcb = trader.getPCB();
            totalTrades += trader.getTradesExecuted();
            totalContextSwitches += pcb.getContextSwitchCount();
            totalWaitTime += pcb.getWaitingTime();
            
            System.out.printf("Trader %d: %d trades, %d context switches, %dms wait time\n",
                pcb.getProcessId(), trader.getTradesExecuted(), 
                pcb.getContextSwitchCount(), pcb.getWaitingTime());
        }
        
        // Calculate average wait time
        double avgWaitTime = traders.size() > 0 ? (double) totalWaitTime / traders.size() : 0.0;
        
        // OS Component Statistics
        System.out.println("\n--- OS Component Statistics ---");
        System.out.println("Scheduler: " + scheduler);
        System.out.println("  Algorithm: " + scheduler.getAlgorithm());
        System.out.println("  Total Context Switches: " + scheduler.getTotalContextSwitches());
        
        System.out.println("\nVirtual Memory: " + vmm);
        System.out.println("  Page Faults: " + vmm.getPageFaults());
        System.out.println("  Page Hits: " + vmm.getPageHits());
        System.out.println("  Fault Rate: " + String.format("%.2f%%", vmm.getPageFaultRate() * 100));
        System.out.println("  Thrashing: " + (vmm.isThrashing() ? "YES" : "NO"));
        
        System.out.println("\nMessage Queue: " + orderQueue);
        System.out.println("Shared Memory: " + marketData);
        System.out.println("Semaphore: " + tradingSemaphore);
        System.out.println("File System: " + transactionLog);
        
        // Performance Metrics
        System.out.println("\n--- Performance Metrics ---");
        long totalTime = monitor.getElapsedTime();
        double throughput = totalTrades / (totalTime / 1000.0);
        double avgContextSwitchTime = totalTime / (double) totalContextSwitches;
        
        System.out.printf("Total Execution Time: %dms\n", totalTime);
        System.out.printf("Total Trades: %d\n", totalTrades);
        System.out.printf("Throughput: %.2f trades/sec\n", throughput);
        System.out.printf("Avg Context Switch Time: %.2fms\n", avgContextSwitchTime);
        System.out.printf("Avg Wait Time: %.2fms\n", avgWaitTime);
        System.out.printf("Total Context Switches: %d\n", totalContextSwitches);
        
        // Portfolio Values
        System.out.println("\n--- Final Portfolio Values ---");
        Map<String, Double> stockPrices = new HashMap<>();
        for (Stock stock : stocks) {
            stockPrices.put(stock.getSymbol(), stock.getCurrentPrice());
        }
        
        for (Map.Entry<Integer, Portfolio> entry : portfolios.entrySet()) {
            Portfolio p = entry.getValue();
            double totalValue = p.getTotalValue(stockPrices);
            System.out.printf("Trader %d: $%.2f (Cash: $%.2f)\n",
                entry.getKey(), totalValue, p.getCashBalance());
        }
        
        System.out.println("\n" + "=".repeat(60));
    }
    
    public CustomScheduler getScheduler() { return scheduler; }
    public VirtualMemoryManager getVMM() { return vmm; }
    public MessageQueue getOrderQueue() { return orderQueue; }
    public SharedMemory getMarketData() { return marketData; }
    public List<Stock> getStocks() { return stocks; }
    public List<TraderProcess> getTraders() { return traders; }
}

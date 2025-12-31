import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe portfolio management for trader processes.
 * 
 * Each trader has a portfolio that tracks:
 * - Cash balance (for buying stocks)
 * - Stock holdings (symbol → quantity mapping)
 * 
 * THREAD SAFETY:
 * All methods are synchronized to prevent race conditions when multiple
 * threads access the same portfolio. This is critical because:
 * 1. Traders execute concurrently
 * 2. Portfolio updates must be atomic (cash and holdings together)
 * 3. Portfolio queries must see consistent state
 * 
 * OPERATIONS:
 * - getCashBalance(): Returns current cash
 * - updateCash(amount): Add/subtract cash (negative for purchases)
 * - getHolding(symbol): Get quantity of a stock
 * - updateHolding(symbol, quantity): Add/subtract stock quantity
 * - getTotalValue(prices): Calculate portfolio worth (cash + stock values)
 * 
 * VIRTUAL MEMORY INTEGRATION:
 * Portfolios are stored in virtual memory pages managed by VirtualMemoryManager.
 * Access patterns trigger page faults for realistic memory management simulation.
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see TraderProcess
 * @see VirtualMemoryManager
 */
public class Portfolio {
    private int traderId;
    private double cashBalance;
    private Map<String, Integer> holdings; // symbol -> quantity
    
    public Portfolio(int traderId, double initialCash) {
        this.traderId = traderId;
        this.cashBalance = initialCash;
        this.holdings = new HashMap<>();
    }
    
    public int getTraderId() {
        return traderId;
    }
    
    public synchronized double getCashBalance() {
        return cashBalance;
    }
    
    public synchronized void updateCash(double amount) {
        this.cashBalance += amount;
    }
    
    public synchronized int getHolding(String symbol) {
        return holdings.getOrDefault(symbol, 0);
    }
    
    public synchronized void updateHolding(String symbol, int quantity) {
        int current = holdings.getOrDefault(symbol, 0);
        holdings.put(symbol, current + quantity);
    }
    
    public synchronized double getTotalValue(Map<String, Double> stockPrices) {
        double totalValue = cashBalance;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            Double price = stockPrices.get(symbol);
            if (price != null) {
                totalValue += quantity * price;
            }
        }
        return totalValue;
    }
    
    @Override
    public synchronized String toString() {
        return String.format("Portfolio[Trader-%d] Cash: $%.2f, Holdings: %s", 
            traderId, cashBalance, holdings);
    }
}

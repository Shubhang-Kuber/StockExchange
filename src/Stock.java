/**
 * Represents a tradable stock in the stock exchange simulation.
 * 
 * This class maintains stock information including symbol, name, and current price.
 * Thread-safe price updates are supported through synchronized methods to handle
 * concurrent access from multiple trader processes.
 * 
 * FEATURES:
 * - Unique stock symbol (e.g., "AAPL", "GOOGL")
 * - Company name
 * - Thread-safe volatile price updates
 * - Real-time price access for traders
 * 
 * THREAD SAFETY:
 * - currentPrice is volatile for visibility across threads
 * - updatePrice() is synchronized to prevent race conditions
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 */
public class Stock {
    private String symbol;
    private String name;
    private volatile double currentPrice;
    
    public Stock(String symbol, String name, double initialPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = initialPrice;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public double getCurrentPrice() {
        return currentPrice;
    }
    
    public synchronized void updatePrice(double newPrice) {
        this.currentPrice = newPrice;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s): $%.2f", name, symbol, currentPrice);
    }
}

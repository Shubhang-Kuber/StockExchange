/**
 * Represents a buy or sell order in the trading system.
 * 
 * Orders are created by trader processes and submitted through the message queue
 * to the stock exchange for execution. Each order contains all necessary information
 * for trade execution including trader ID, stock symbol, order type, quantity, and price.
 * 
 * ATTRIBUTES:
 * - orderId: Unique identifier for the order
 * - traderId: ID of the trader who created the order
 * - stockSymbol: Symbol of the stock being traded
 * - type: BUY or SELL
 * - quantity: Number of shares
 * - price: Price per share
 * - timestamp: Order creation time (for FIFO processing)
 * - executed: Whether the order has been completed
 * 
 * USAGE:
 * Used in conjunction with MessageQueue for IPC and TransactionalFS for logging.
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see MessageQueue
 * @see TransactionalFS
 */
public class Order {
    public enum OrderType { BUY, SELL }
    
    private int orderId;
    private int traderId;
    private String stockSymbol;
    private OrderType type;
    private int quantity;
    private double price;
    private long timestamp;
    private boolean executed;
    
    public Order(int orderId, int traderId, String stockSymbol, OrderType type, int quantity, double price) {
        this.orderId = orderId;
        this.traderId = traderId;
        this.stockSymbol = stockSymbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = System.currentTimeMillis();
        this.executed = false;
    }
    
    public int getOrderId() { return orderId; }
    public int getTraderId() { return traderId; }
    public String getStockSymbol() { return stockSymbol; }
    public OrderType getType() { return type; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }
    public boolean isExecuted() { return executed; }
    
    public void setExecuted(boolean executed) {
        this.executed = executed;
    }
    
    @Override
    public String toString() {
        return String.format("Order[%d] Trader-%d %s %d %s @ $%.2f", 
            orderId, traderId, type, quantity, stockSymbol, price);
    }
}

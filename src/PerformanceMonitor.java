import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance Monitor - System-wide performance tracking.
 * 
 * Tracks high-level system metrics across the entire simulation.
 * This is similar to system monitoring tools like:
 * - Unix: top, vmstat, iostat
 * - Windows: Task Manager, Performance Monitor
 * - Linux: /proc/stat, perf
 * 
 * TRACKED METRICS:
 * 
 * 1. Execution Time
 *    - Start time: When simulation begins
 *    - End time: When simulation completes
 *    - Elapsed time: Total duration
 * 
 * 2. Process Metrics
 *    - Processes started: Total processes created
 *    - Processes completed: Processes that reached TERMINATED state
 *    - Active processes: started - completed
 * 
 * 3. Transaction Throughput
 *    - Total transactions: Cumulative count
 *    - Throughput: Transactions per second
 * 
 * THREAD SAFETY:
 * Uses AtomicLong for lock-free concurrent updates from multiple
 * trader processes without synchronization overhead.
 * 
 * USAGE:
 * 1. Create monitor at system start
 * 2. Call start() to begin timing
 * 3. Processes call recordProcessStart() on creation
 * 4. Processes call recordProcessCompletion() on termination
 * 5. Transactions call recordTransaction() on commit
 * 6. Call stop() at simulation end
 * 7. Query metrics for reporting
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see StockExchange (uses monitor for reporting)
 */
public class PerformanceMonitor {
    private long startTime;
    private long endTime;
    private AtomicLong processesStarted;
    private AtomicLong processesCompleted;
    private AtomicLong totalTransactions;
    private boolean running;
    
    public PerformanceMonitor() {
        this.startTime = 0;
        this.endTime = 0;
        this.processesStarted = new AtomicLong(0);
        this.processesCompleted = new AtomicLong(0);
        this.totalTransactions = new AtomicLong(0);
        this.running = false;
    }
    
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }
    
    public void stop() {
        this.endTime = System.currentTimeMillis();
        this.running = false;
    }
    
    public void recordProcessStart() {
        processesStarted.incrementAndGet();
    }
    
    public void recordProcessCompletion() {
        processesCompleted.incrementAndGet();
    }
    
    public void recordTransaction() {
        totalTransactions.incrementAndGet();
    }
    
    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        } else {
            return endTime - startTime;
        }
    }
    
    public long getProcessesStarted() {
        return processesStarted.get();
    }
    
    public long getProcessesCompleted() {
        return processesCompleted.get();
    }
    
    public long getTotalTransactions() {
        return totalTransactions.get();
    }
    
    public double getThroughput() {
        long elapsed = getElapsedTime();
        if (elapsed > 0) {
            return (double) totalTransactions.get() / (elapsed / 1000.0);
        }
        return 0.0;
    }
    
    @Override
    public String toString() {
        return String.format("PerformanceMonitor[Elapsed=%dms, Processes=%d/%d, Transactions=%d, Throughput=%.2f/s]",
            getElapsedTime(), processesCompleted.get(), processesStarted.get(),
            totalTransactions.get(), getThroughput());
    }
}

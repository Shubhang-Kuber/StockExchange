import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Custom Semaphore wrapper for process synchronization.
 * 
 * Semaphores are synchronization primitives used to control access to shared
 * resources. This class wraps Java's Semaphore with additional performance
 * tracking and statistics.
 * 
 * SEMAPHORE CONCEPTS:
 * - Counter: Tracks available resource permits
 * - P() operation (acquire): Decrement counter, block if zero
 * - V() operation (release): Increment counter, wake waiting process
 * - Fair: FIFO ordering of waiting processes
 * 
 * TYPES:
 * 1. Binary Semaphore (permits=1): Acts like a mutex lock
 * 2. Counting Semaphore (permits>1): Controls access to resource pool
 * 
 * USE CASES:
 * - Mutual Exclusion: Protect critical sections
 * - Resource Counting: Limit concurrent access (e.g., connection pool)
 * - Signaling: Coordinate between processes
 * 
 * IN THIS SIMULATOR:
 * Used as a counting semaphore to limit concurrent traders accessing
 * the exchange (e.g., max 5 traders executing trades simultaneously).
 * This simulates resource constraints like:
 * - Database connection limits
 * - Thread pool size
 * - I/O device access
 * 
 * FAIRNESS:
 * This implementation uses a fair semaphore (FIFO), meaning processes
 * acquire permits in the order they request them. This prevents starvation
 * but may reduce throughput compared to unfair semaphores.
 * 
 * CLASSIC PROBLEMS SOLVED BY SEMAPHORES:
 * - Producer-Consumer (bounded buffer)
 * - Readers-Writers
 * - Dining Philosophers
 * 
 * PERFORMANCE TRACKING:
 * - Acquire Count: Total successful acquisitions
 * - Release Count: Total releases
 * - Wait Time: Cumulative time spent waiting
 * - Average Wait Time: Mean wait per acquisition
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see TraderProcess (uses semaphore for resource coordination)
 */
public class CustomSemaphore {
    private Semaphore semaphore;
    private String name;
    private long acquireCount;
    private long releaseCount;
    private long waitTime; // Total wait time in milliseconds
    
    public CustomSemaphore(String name, int permits) {
        this.name = name;
        this.semaphore = new Semaphore(permits, true); // Fair semaphore
        this.acquireCount = 0;
        this.releaseCount = 0;
        this.waitTime = 0;
    }
    
    public void acquire() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        semaphore.acquire();
        long endTime = System.currentTimeMillis();
        
        synchronized (this) {
            acquireCount++;
            waitTime += (endTime - startTime);
        }
    }
    
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        boolean acquired = semaphore.tryAcquire(timeout, unit);
        long endTime = System.currentTimeMillis();
        
        if (acquired) {
            synchronized (this) {
                acquireCount++;
                waitTime += (endTime - startTime);
            }
        }
        return acquired;
    }
    
    public void release() {
        semaphore.release();
        synchronized (this) {
            releaseCount++;
        }
    }
    
    public int availablePermits() {
        return semaphore.availablePermits();
    }
    
    public synchronized long getAcquireCount() { return acquireCount; }
    public synchronized long getReleaseCount() { return releaseCount; }
    public synchronized long getWaitTime() { return waitTime; }
    public synchronized double getAverageWaitTime() {
        return acquireCount > 0 ? (double) waitTime / acquireCount : 0.0;
    }
    
    @Override
    public synchronized String toString() {
        return String.format("Semaphore[%s, Available=%d, Acquires=%d, Releases=%d, AvgWait=%.2fms]",
            name, semaphore.availablePermits(), acquireCount, releaseCount, getAverageWaitTime());
    }
}

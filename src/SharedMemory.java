import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Shared Memory for Inter-Process Communication (IPC).
 * 
 * Implements shared memory IPC where multiple processes can access the same
 * memory region. This is typically faster than message passing but requires
 * careful synchronization.
 * 
 * SHARED MEMORY CHARACTERISTICS:
 * - Direct memory access (no copying)
 * - Requires explicit synchronization (locks, semaphores)
 * - Very fast for large data transfers
 * - Common in high-performance computing
 * 
 * READ/WRITE LOCK OPTIMIZATION:
 * Uses ReadWriteLock for better concurrency:
 * - Multiple readers can access simultaneously (read lock)
 * - Writers get exclusive access (write lock)
 * - Ideal for read-heavy workloads (like stock prices)
 * 
 * FEATURES:
 * - Key-value store interface (like shared hash table)
 * - Thread-safe with reader-writer locking
 * - Performance tracking (read/write counts)
 * - Supports any Object type as value
 * 
 * USE CASE IN SIMULATOR:
 * Stock prices are written to shared memory by the exchange and read by traders:
 *   Exchange (writer) → [Shared Memory] → Traders (readers)
 * 
 * This simulates:
 * - Unix: shmget(), shmat(), shmdt()
 * - POSIX: shm_open(), mmap()
 * - Windows: CreateFileMapping(), MapViewOfFile()
 * 
 * COMPARISON WITH MESSAGE QUEUE:
 * Advantages:
 * - Zero-copy access (very fast)
 * - Low latency for reads
 * - Suitable for large data structures
 * 
 * Disadvantages:
 * - Requires synchronization primitives
 * - Potential for race conditions if misused
 * - More complex programming model
 * 
 * SYNCHRONIZATION PATTERN:
 * This implementation uses locks internally, but in real systems,
 * applications must coordinate access with semaphores or other mechanisms.
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see MessageQueue (alternative IPC mechanism)
 * @see CustomSemaphore (synchronization primitive)
 */
public class SharedMemory {
    private Map<String, Object> memory;
    private ReadWriteLock rwLock;
    private long reads;
    private long writes;
    
    public SharedMemory() {
        this.memory = new HashMap<>();
        this.rwLock = new ReentrantReadWriteLock();
        this.reads = 0;
        this.writes = 0;
    }
    
    public Object read(String key) {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            reads++;
            return memory.get(key);
        } finally {
            readLock.unlock();
        }
    }
    
    public void write(String key, Object value) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            memory.put(key, value);
            writes++;
        } finally {
            writeLock.unlock();
        }
    }
    
    public boolean contains(String key) {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return memory.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }
    
    public void remove(String key) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            memory.remove(key);
        } finally {
            writeLock.unlock();
        }
    }
    
    public long getReads() { return reads; }
    public long getWrites() { return writes; }
    
    // Aliases for consistency with other components
    public long getReadCount() { return reads; }
    public long getWriteCount() { return writes; }
    
    @Override
    public String toString() {
        return String.format("SharedMemory[Entries=%d, Reads=%d, Writes=%d]",
            memory.size(), reads, writes);
    }
}

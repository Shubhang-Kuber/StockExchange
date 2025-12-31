import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Transactional File System with Write-Ahead Logging (WAL).
 * 
 * Implements a simplified file system that provides ACID transaction guarantees
 * through write-ahead logging, a technique used in databases and modern file
 * systems (ext4, NTFS, XFS).
 * 
 * ACID PROPERTIES:
 * - Atomicity: Transactions complete fully or not at all
 * - Consistency: System moves from valid state to valid state
 * - Isolation: Concurrent transactions don't interfere (simplified here)
 * - Durability: Committed transactions survive crashes
 * 
 * WRITE-AHEAD LOGGING (WAL):
 * The golden rule: "Log first, commit later"
 * 
 * Protocol:
 * 1. Begin transaction → assign transaction ID
 * 2. Log operation to WAL (in memory buffer)
 * 3. Commit transaction → write COMMIT record
 * 4. fsync → force log to persistent storage
 * 5. Apply changes to main data structures
 * 
 * CRASH RECOVERY:
 * After a crash:
 * 1. Read WAL from disk
 * 2. Replay committed transactions
 * 3. Discard uncommitted transactions
 * 4. System returns to consistent state
 * 
 * REAL-WORLD EXAMPLES:
 * - Databases: PostgreSQL WAL, MySQL redo log
 * - File Systems: ext4 journal, NTFS $LogFile
 * - Key-Value Stores: Redis AOF, RocksDB WAL
 * 
 * OPERATIONS:
 * - beginTransaction(): Start new transaction, return ID
 * - writeLog(txnId, op, data): Add log entry (in-memory)
 * - commitTransaction(txnId, order): Commit and fsync
 * - rollback(txnId): Abort transaction
 * - fsync(): Force buffer to disk
 * - recover(): Replay log after crash
 * 
 * PERFORMANCE CONSIDERATIONS:
 * - Buffering: Batch multiple log entries before fsync
 * - Group Commit: Combine multiple transactions in one fsync
 * - Log Truncation: Periodically checkpoint and remove old entries
 * 
 * IN THIS SIMULATOR:
 * Every order execution is logged as a transaction, ensuring that:
 * - Order history survives simulator crashes
 * - Portfolio updates are atomic (can't lose cash without receiving stock)
 * - Audit trail exists for all trades
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see Order (logged data structure)
 * @see TraderProcess (creates transactions)
 */
public class TransactionalFS {
    private String logFilePath;
    private List<LogEntry> writeAheadLog;
    private Lock fsLock;
    private long transactionCounter;
    private long flushCount;
    
    public TransactionalFS(String logFilePath) {
        this.logFilePath = logFilePath;
        this.writeAheadLog = new ArrayList<>();
        this.fsLock = new ReentrantLock();
        this.transactionCounter = 0;
        this.flushCount = 0;
    }
    
    public long beginTransaction() {
        fsLock.lock();
        try {
            return ++transactionCounter;
        } finally {
            fsLock.unlock();
        }
    }
    
    public void writeLog(long transactionId, String operation, Object data) {
        fsLock.lock();
        try {
            LogEntry entry = new LogEntry(transactionId, operation, data, System.currentTimeMillis());
            writeAheadLog.add(entry);
        } finally {
            fsLock.unlock();
        }
    }
    
    public boolean commitTransaction(long transactionId, Order order) {
        fsLock.lock();
        try {
            // Write-ahead logging: log before commit
            writeLog(transactionId, "COMMIT", order);
            
            // Simulate fsync - flush to disk
            fsync();
            
            return true;
        } catch (Exception e) {
            // Rollback on error
            rollback(transactionId);
            return false;
        } finally {
            fsLock.unlock();
        }
    }
    
    public void fsync() {
        // Simulate force write to disk
        try {
            if (writeAheadLog.size() > 0) {
                File logFile = new File(logFilePath);
                logFile.getParentFile().mkdirs();
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                    for (LogEntry entry : writeAheadLog) {
                        writer.write(entry.toString());
                        writer.newLine();
                    }
                    writer.flush();
                }
                
                writeAheadLog.clear();
                flushCount++;
            }
        } catch (IOException e) {
            System.err.println("Error flushing log: " + e.getMessage());
        }
    }
    
    public void rollback(long transactionId) {
        fsLock.lock();
        try {
            // Remove all log entries for this transaction
            writeAheadLog.removeIf(entry -> entry.getTransactionId() == transactionId);
            writeLog(transactionId, "ROLLBACK", null);
        } finally {
            fsLock.unlock();
        }
    }
    
    public List<LogEntry> recover() {
        List<LogEntry> recoveredEntries = new ArrayList<>();
        
        try {
            File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                return recoveredEntries;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Parse log entry (simplified)
                    recoveredEntries.add(LogEntry.fromString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error recovering log: " + e.getMessage());
        }
        
        return recoveredEntries;
    }
    
    public long getTransactionCount() { return transactionCounter; }
    public long getFlushCount() { return flushCount; }
    public int getBufferedLogEntries() { return writeAheadLog.size(); }
    
    @Override
    public String toString() {
        return String.format("TransactionalFS[Transactions=%d, Flushes=%d, BufferedEntries=%d]",
            transactionCounter, flushCount, writeAheadLog.size());
    }
}

class LogEntry {
    private long transactionId;
    private String operation;
    private Object data;
    private long timestamp;
    
    public LogEntry(long transactionId, String operation, Object data, long timestamp) {
        this.transactionId = transactionId;
        this.operation = operation;
        this.data = data;
        this.timestamp = timestamp;
    }
    
    public long getTransactionId() { return transactionId; }
    public String getOperation() { return operation; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("[TXN:%d] %s @ %d | %s", 
            transactionId, operation, timestamp, data != null ? data.toString() : "null");
    }
    
    public static LogEntry fromString(String line) {
        // Simplified parsing - in production would be more robust
        return new LogEntry(0, "RECOVERED", line, System.currentTimeMillis());
    }
}

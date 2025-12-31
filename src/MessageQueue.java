import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Message Queue for Inter-Process Communication (IPC).
 * 
 * Implements message-passing IPC where processes communicate by sending and
 * receiving messages through a shared queue. This is one of two main IPC
 * paradigms (the other being shared memory).
 * 
 * MESSAGE PASSING CHARACTERISTICS:
 * - Explicit communication via send/receive operations
 * - No shared address space (safer, no race conditions on data)
 * - Kernel manages message buffering
 * - Good for distributed systems
 * 
 * FEATURES:
 * - Bounded capacity (1000 messages) to prevent unbounded memory usage
 * - Non-blocking operations (returns false if queue full)
 * - FIFO ordering (messages received in send order)
 * - Thread-safe with lock protection
 * - Performance tracking (messages sent/received)
 * 
 * USE CASE IN SIMULATOR:
 * Traders send Order messages to the exchange through this queue:
 *   Trader Process → [Message Queue] → Exchange
 * 
 * This simulates system calls like:
 * - Unix: msgsnd(), msgrcv()
 * - POSIX: mq_send(), mq_receive()
 * 
 * COMPARISON WITH SHARED MEMORY:
 * Advantages:
 * - Simpler programming model
 * - No synchronization needed for message data
 * - Natural for request/response patterns
 * 
 * Disadvantages:
 * - Copying overhead (message must be copied twice)
 * - Kernel involvement for each operation
 * - Limited to small-medium data sizes
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see SharedMemory (alternative IPC mechanism)
 * @see TraderProcess (message producer)
 * @see StockExchange (message consumer)
 */
public class MessageQueue {
    private Queue<Message> queue;
    private int capacity;
    private Lock queueLock;
    private long messagesSent;
    private long messagesReceived;
    
    public MessageQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
        this.queueLock = new ReentrantLock();
        this.messagesSent = 0;
        this.messagesReceived = 0;
    }
    
    public boolean send(Message message) {
        queueLock.lock();
        try {
            if (queue.size() >= capacity) {
                return false; // Queue full
            }
            queue.offer(message);
            messagesSent++;
            return true;
        } finally {
            queueLock.unlock();
        }
    }
    
    public Message receive() {
        queueLock.lock();
        try {
            Message msg = queue.poll();
            if (msg != null) {
                messagesReceived++;
            }
            return msg;
        } finally {
            queueLock.unlock();
        }
    }
    
    public int size() {
        queueLock.lock();
        try {
            return queue.size();
        } finally {
            queueLock.unlock();
        }
    }
    
    public long getMessagesSent() { return messagesSent; }
    public long getMessagesReceived() { return messagesReceived; }
    
    @Override
    public String toString() {
        return String.format("MessageQueue[Size=%d/%d, Sent=%d, Received=%d]",
            queue.size(), capacity, messagesSent, messagesReceived);
    }
}

class Message {
    private int senderId;
    private int receiverId;
    private String type;
    private Object payload;
    private long timestamp;
    
    public Message(int senderId, int receiverId, String type, Object payload) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getType() { return type; }
    public Object getPayload() { return payload; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("Message[%d→%d, Type=%s, Time=%d]",
            senderId, receiverId, type, timestamp);
    }
}

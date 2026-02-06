import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Virtual Memory Manager with Demand Paging and Page Replacement.
 * 
 * Simulates a virtual memory system where processes have a large virtual address
 * space mapped to a smaller physical memory through paging. Implements page
 * replacement algorithms when physical memory is full.
 * 
 * ARCHITECTURE:
 * - Virtual Pages: 100 (large virtual address space)
 * - Physical Frames: 20 (limited physical memory)
 * - Oversubscription Ratio: 5:1 (realistic memory pressure)
 * 
 * PAGE REPLACEMENT ALGORITHMS:
 * 
 * 1. LRU (Least Recently Used)
 *    - Evicts page that hasn't been used for longest time
 *    - Uses timestamp tracking for access history
 *    - Better performance for programs with locality
 *    - Approximates optimal replacement (Belady's algorithm)
 * 
 * 2. FIFO (First-In-First-Out)
 *    - Evicts oldest page in memory
 *    - Simple queue-based implementation
 *    - Can suffer from Belady's anomaly
 *    - Lower overhead than LRU
 * 
 * KEY CONCEPTS DEMONSTRATED:
 * - Page Tables: Virtual-to-physical address translation
 * - Page Faults: Handle missing pages by loading from "disk"
 * - Page Replacement: Evict victim when no free frames
 * - Thrashing Detection: Monitor fault rate (>50% indicates thrashing)
 * - Working Set: Implicit through page access patterns
 * 
 * PERFORMANCE METRICS:
 * - Page Faults: Count of pages not found in memory
 * - Page Hits: Count of successful page accesses
 * - Fault Rate: pageFaults / (pageFaults + pageHits)
 * - Thrashing Status: Boolean indicating severe memory pressure
 * 
 * THREAD SAFETY:
 * All memory operations are protected by locks to prevent race conditions
 * during concurrent page table updates and frame allocation.
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see Portfolio (stored in virtual memory pages)
 * @see TraderProcess (accesses virtual memory)
 */
public class VirtualMemoryManager {
    public enum ReplacementPolicy { LRU, FIFO }
    
    private int physicalFrames;
    private int virtualPages;
    private ReplacementPolicy policy;
    private PageTable pageTable;
    private Map<Integer, PhysicalFrame> frameMap;
    private Queue<Integer> fifoQueue; // For FIFO replacement
    private LinkedHashMap<Integer, Long> lruMap; // For LRU replacement
    private long pageFaults;
    private long pageHits;
    private Lock memoryLock;
    
    public VirtualMemoryManager(int physicalFrames, int virtualPages, ReplacementPolicy policy) {
        this.physicalFrames = physicalFrames;
        this.virtualPages = virtualPages;
        this.policy = policy;
        this.pageTable = new PageTable(virtualPages);
        this.frameMap = new HashMap<>();
        this.fifoQueue = new LinkedList<>();
        this.lruMap = new LinkedHashMap<>(physicalFrames, 0.75f, true);
        this.pageFaults = 0;
        this.pageHits = 0;
        this.memoryLock = new ReentrantLock();
    }
    
    public Object accessPage(int virtualPageNumber, Object defaultValue) {
        memoryLock.lock();
        try {
            PageTableEntry entry = pageTable.getEntry(virtualPageNumber);
            
            if (entry != null && entry.isValid()) {
                // Page hit
                pageHits++;
                int frameNumber = entry.getFrameNumber();
                PhysicalFrame frame = frameMap.get(frameNumber);
                
                if (StockSimulator.VERBOSE) {
                    System.out.println("      [VM] Page HIT: Virtual page " + virtualPageNumber + " → Physical frame " + frameNumber);
                }
                
                // Update LRU tracking
                if (policy == ReplacementPolicy.LRU) {
                    lruMap.put(frameNumber, System.currentTimeMillis());
                }
                
                return frame != null ? frame.getData() : defaultValue;
            } else {
                // Page fault
                pageFaults++;
                if (StockSimulator.VERBOSE) {
                    System.out.println("      [VM] Page FAULT: Virtual page " + virtualPageNumber + " not in memory! Loading...");
                }
                return handlePageFault(virtualPageNumber, defaultValue);
            }
        } finally {
            memoryLock.unlock();
        }
    }
    
    private Object handlePageFault(int virtualPageNumber, Object data) {
        int frameNumber;
        
        // Check if we have free frames
        if (frameMap.size() < physicalFrames) {
            // Allocate new frame
            frameNumber = frameMap.size();
            if (StockSimulator.VERBOSE) {
                System.out.println("      [VM] Allocating new physical frame " + frameNumber + " (free frames available)");
            }
        } else {
            // Need to replace a page
            if (StockSimulator.VERBOSE) {
                System.out.println("      [VM] Physical memory FULL! Using " + policy + " replacement policy...");
            }
            frameNumber = selectVictimFrame();
            if (StockSimulator.VERBOSE) {
                System.out.println("      [VM] Evicting frame " + frameNumber + " (victim selected by " + policy + ")");
            }
            evictFrame(frameNumber);
        }
        
        // Load page into frame
        PhysicalFrame frame = new PhysicalFrame(frameNumber, data);
        frameMap.put(frameNumber, frame);
        
        // Update page table
        PageTableEntry entry = pageTable.getEntry(virtualPageNumber);
        if (entry == null) {
            entry = new PageTableEntry(virtualPageNumber);
            pageTable.setEntry(virtualPageNumber, entry);
        }
        entry.setFrameNumber(frameNumber);
        entry.setValid(true);
        
        // Update replacement policy structures
        if (policy == ReplacementPolicy.FIFO) {
            fifoQueue.offer(frameNumber);
        } else if (policy == ReplacementPolicy.LRU) {
            lruMap.put(frameNumber, System.currentTimeMillis());
        }
        
        return data;
    }
    
    private int selectVictimFrame() {
        if (policy == ReplacementPolicy.FIFO) {
            return fifoQueue.poll();
        } else { // LRU
            // Find least recently used frame
            int lruFrame = -1;
            long oldestTime = Long.MAX_VALUE;
            for (Map.Entry<Integer, Long> entry : lruMap.entrySet()) {
                if (entry.getValue() < oldestTime) {
                    oldestTime = entry.getValue();
                    lruFrame = entry.getKey();
                }
            }
            lruMap.remove(lruFrame);
            return lruFrame;
        }
    }
    
    private void evictFrame(int frameNumber) {
        // Find and invalidate the page table entry for this frame
        for (int i = 0; i < virtualPages; i++) {
            PageTableEntry entry = pageTable.getEntry(i);
            if (entry != null && entry.isValid() && entry.getFrameNumber() == frameNumber) {
                entry.setValid(false);
                break;
            }
        }
        frameMap.remove(frameNumber);
    }
    
    public void writePage(int virtualPageNumber, Object data) {
        memoryLock.lock();
        try {
            PageTableEntry entry = pageTable.getEntry(virtualPageNumber);
            
            if (entry != null && entry.isValid()) {
                int frameNumber = entry.getFrameNumber();
                PhysicalFrame frame = frameMap.get(frameNumber);
                if (frame != null) {
                    frame.setData(data);
                    entry.setDirty(true);
                }
            } else {
                // Page fault - load page first
                handlePageFault(virtualPageNumber, data);
            }
        } finally {
            memoryLock.unlock();
        }
    }
    
    public long getPageFaults() { return pageFaults; }
    public long getPageHits() { return pageHits; }
    public double getPageFaultRate() {
        long total = pageFaults + pageHits;
        return total > 0 ? (double) pageFaults / total : 0.0;
    }
    
    public boolean isThrashing() {
        // Simple thrashing detection: page fault rate > 50%
        return getPageFaultRate() > 0.5;
    }
    
    public int getUsedFrames() {
        return frameMap.size();
    }
    
    public int getTotalFrames() {
        return physicalFrames;
    }
    
    @Override
    public String toString() {
        return String.format("VMM[Policy=%s, Frames=%d/%d, PageFaults=%d, Hits=%d, FaultRate=%.2f%%]",
            policy, frameMap.size(), physicalFrames, pageFaults, pageHits, getPageFaultRate() * 100);
    }
}

class PageTable {
    private Map<Integer, PageTableEntry> entries;
    
    public PageTable(int size) {
        this.entries = new HashMap<>();
    }
    
    public PageTableEntry getEntry(int pageNumber) {
        return entries.get(pageNumber);
    }
    
    public void setEntry(int pageNumber, PageTableEntry entry) {
        entries.put(pageNumber, entry);
    }
}

class PageTableEntry {
    private int virtualPageNumber;
    private int frameNumber;
    private boolean valid;
    private boolean dirty;
    private boolean referenced;
    
    public PageTableEntry(int virtualPageNumber) {
        this.virtualPageNumber = virtualPageNumber;
        this.frameNumber = -1;
        this.valid = false;
        this.dirty = false;
        this.referenced = false;
    }
    
    public int getVirtualPageNumber() { return virtualPageNumber; }
    public int getFrameNumber() { return frameNumber; }
    public boolean isValid() { return valid; }
    public boolean isDirty() { return dirty; }
    public boolean isReferenced() { return referenced; }
    
    public void setFrameNumber(int frameNumber) { this.frameNumber = frameNumber; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }
    public void setReferenced(boolean referenced) { this.referenced = referenced; }
}

class PhysicalFrame {
    private int frameNumber;
    private Object data;
    
    public PhysicalFrame(int frameNumber, Object data) {
        this.frameNumber = frameNumber;
        this.data = data;
    }
    
    public int getFrameNumber() { return frameNumber; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}

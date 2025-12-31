import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Custom CPU Scheduler implementing multiple scheduling algorithms.
 * 
 * This scheduler demonstrates fundamental OS scheduling concepts by implementing
 * four classic algorithms. It manages ready and waiting queues, performs context
 * switching, and tracks performance metrics.
 * 
 * SUPPORTED ALGORITHMS:
 * 
 * 1. FCFS (First-Come-First-Served)
 *    - Non-preemptive
 *    - Orders by arrival time
 *    - Simple but can cause convoy effect
 * 
 * 2. SJF (Shortest Job First)
 *    - Non-preemptive
 *    - Orders by estimated burst time
 *    - Optimal average waiting time but requires burst time estimates
 * 
 * 3. PRIORITY
 *    - Preemptive
 *    - Orders by priority (higher number = higher priority)
 *    - Implements aging to prevent starvation
 *    - Priority increases by 1 for every 1 second of waiting
 * 
 * 4. ROUND_ROBIN
 *    - Preemptive with time quantum (default 100ms)
 *    - Fair time-sharing
 *    - Good response time but higher context switch overhead
 * 
 * KEY FEATURES:
 * - Context switching with state preservation
 * - Ready queue (tasks waiting for CPU)
 * - Waiting queue (tasks blocked on I/O)
 * - Priority aging to prevent indefinite postponement
 * - Performance tracking (context switches, queue sizes)
 * 
 * THREAD SAFETY:
 * All operations are protected by a ReentrantLock to ensure thread-safe
 * access to scheduler state from multiple concurrent processes.
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see Task
 * @see ProcessControlBlock
 */
public class CustomScheduler {
    public enum Algorithm { FCFS, SJF, PRIORITY, ROUND_ROBIN }
    
    private PriorityQueue<Task> readyQueue;
    private List<Task> waitingQueue;
    private Task currentTask;
    private Algorithm algorithm;
    private int timeQuantum; // For Round Robin
    private Lock schedulerLock;
    private long totalContextSwitches;
    
    public CustomScheduler(Algorithm algorithm, int timeQuantum) {
        this.algorithm = algorithm;
        this.timeQuantum = timeQuantum;
        this.readyQueue = new PriorityQueue<>(getComparator(algorithm));
        this.waitingQueue = new ArrayList<>();
        this.schedulerLock = new ReentrantLock();
        this.totalContextSwitches = 0;
    }
    
    private Comparator<Task> getComparator(Algorithm algo) {
        switch (algo) {
            case FCFS:
                return Comparator.comparingLong(Task::getArrivalTime);
            case SJF:
                return Comparator.comparingLong(Task::getBurstTime);
            case PRIORITY:
                return Comparator.comparingInt(Task::getPriority).reversed(); // Higher priority first
            case ROUND_ROBIN:
                return Comparator.comparingLong(Task::getArrivalTime);
            default:
                return Comparator.comparingLong(Task::getArrivalTime);
        }
    }
    
    public void addTask(Task task) {
        schedulerLock.lock();
        try {
            task.getPCB().setState(ProcessControlBlock.State.READY);
            readyQueue.offer(task);
        } finally {
            schedulerLock.unlock();
        }
    }
    
    public Task schedule() {
        schedulerLock.lock();
        try {
            // Implement aging for priority scheduling to prevent starvation
            if (algorithm == Algorithm.PRIORITY) {
                ageTasks();
            }
            
            // Context switch if needed
            if (currentTask != null) {
                if (algorithm == Algorithm.ROUND_ROBIN) {
                    // Check if quantum expired
                    long runTime = System.currentTimeMillis() - currentTask.getStartTime();
                    if (runTime >= timeQuantum && !readyQueue.isEmpty()) {
                        contextSwitch(currentTask);
                        currentTask = null;
                    }
                }
            }
            
            // Get next task
            if (currentTask == null && !readyQueue.isEmpty()) {
                currentTask = readyQueue.poll();
                currentTask.getPCB().setState(ProcessControlBlock.State.RUNNING);
                currentTask.setStartTime(System.currentTimeMillis());
                totalContextSwitches++;
                currentTask.getPCB().incrementContextSwitch();
            }
            
            return currentTask;
        } finally {
            schedulerLock.unlock();
        }
    }
    
    private void contextSwitch(Task task) {
        if (task != null && task.getPCB().getState() == ProcessControlBlock.State.RUNNING) {
            task.getPCB().setState(ProcessControlBlock.State.READY);
            readyQueue.offer(task);
            totalContextSwitches++;
        }
    }
    
    private void ageTasks() {
        // Increase priority of waiting tasks to prevent starvation
        for (Task task : readyQueue) {
            long waitTime = System.currentTimeMillis() - task.getArrivalTime();
            if (waitTime > 1000) { // If waiting > 1 second
                int currentPriority = task.getPriority();
                task.setPriority(Math.min(10, currentPriority + 1)); // Max priority 10
            }
        }
        // Rebuild queue with new priorities
        List<Task> tasks = new ArrayList<>(readyQueue);
        readyQueue.clear();
        readyQueue.addAll(tasks);
    }
    
    public void moveToWaiting(Task task) {
        schedulerLock.lock();
        try {
            if (currentTask == task) {
                currentTask = null;
            }
            task.getPCB().setState(ProcessControlBlock.State.WAITING);
            waitingQueue.add(task);
        } finally {
            schedulerLock.unlock();
        }
    }
    
    public void moveToReady(Task task) {
        schedulerLock.lock();
        try {
            waitingQueue.remove(task);
            task.getPCB().setState(ProcessControlBlock.State.READY);
            readyQueue.offer(task);
        } finally {
            schedulerLock.unlock();
        }
    }
    
    public void terminateTask(Task task) {
        schedulerLock.lock();
        try {
            task.getPCB().setState(ProcessControlBlock.State.TERMINATED);
            task.getPCB().calculateTurnaroundTime();
            if (currentTask == task) {
                currentTask = null;
            }
        } finally {
            schedulerLock.unlock();
        }
    }
    
    public long getTotalContextSwitches() {
        return totalContextSwitches;
    }
    
    public int getReadyQueueSize() {
        return readyQueue.size();
    }
    
    public int getWaitingQueueSize() {
        return waitingQueue.size();
    }
    
    public Algorithm getAlgorithm() {
        return algorithm;
    }
    
    @Override
    public String toString() {
        return String.format("Scheduler[Algo=%s, ReadyQueue=%d, WaitingQueue=%d, ContextSwitches=%d]",
            algorithm, readyQueue.size(), waitingQueue.size(), totalContextSwitches);
    }
}

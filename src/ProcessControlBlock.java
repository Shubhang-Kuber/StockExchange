import java.util.HashMap;
import java.util.Map;

/**
 * Process Control Block (PCB) - Core OS data structure for process management.
 * 
 * The PCB stores all information needed to manage a process throughout its lifecycle.
 * This is analogous to the task_struct in Linux or EPROCESS in Windows.
 * 
 * PROCESS STATES:
 * NEW → READY → RUNNING → WAITING → TERMINATED
 * 
 * State transitions occur during:
 * - NEW: Process creation
 * - READY: Waiting in scheduler's ready queue
 * - RUNNING: Currently executing on CPU
 * - WAITING: Blocked on I/O or resource
 * - TERMINATED: Execution completed
 * 
 * TRACKED METRICS:
 * - processId: Unique process identifier (like PID)
 * - state: Current process state
 * - priority: Scheduling priority (1-10, higher = more important)
 * - burstTime: Total CPU time used
 * - arrivalTime: When process was created
 * - waitingTime: Time spent in READY state
 * - responseTime: Time to first execution
 * - turnaroundTime: Total time from arrival to termination
 * - contextSwitchCount: Number of context switches
 * 
 * CONTEXT STORAGE:
 * - context: HashMap simulating CPU registers (PC, SP, registers, etc.)
 * - saveContext()/loadContext() simulate register save/restore during context switch
 * 
 * INTEGRATION:
 * Used by CustomScheduler for scheduling decisions and by TraderProcess for
 * state management. Performance metrics are reported at simulation end.
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see CustomScheduler
 * @see TraderProcess
 */
public class ProcessControlBlock {
    public enum State { NEW, READY, RUNNING, WAITING, TERMINATED }
    
    private int processId;
    private State state;
    private int priority;
    private long burstTime;
    private long arrivalTime;
    private long waitingTime;
    private long responseTime;
    private long turnaroundTime;
    private long contextSwitchCount;
    
    // Context information (simulated registers)
    private Map<String, Object> context;
    
    public ProcessControlBlock(int processId, int priority) {
        this.processId = processId;
        this.state = State.NEW;
        this.priority = priority;
        this.burstTime = 0;
        this.arrivalTime = System.currentTimeMillis();
        this.waitingTime = 0;
        this.responseTime = -1;
        this.turnaroundTime = 0;
        this.contextSwitchCount = 0;
        this.context = new HashMap<>();
    }
    
    public int getProcessId() { return processId; }
    public State getState() { return state; }
    public int getPriority() { return priority; }
    public long getBurstTime() { return burstTime; }
    public long getArrivalTime() { return arrivalTime; }
    public long getWaitingTime() { return waitingTime; }
    public long getResponseTime() { return responseTime; }
    public long getTurnaroundTime() { return turnaroundTime; }
    public long getContextSwitchCount() { return contextSwitchCount; }
    public long getCPUTime() { return burstTime; } // CPU time is same as burst time
    
    public void setState(State state) {
        this.state = state;
        if (state == State.RUNNING && responseTime == -1) {
            responseTime = System.currentTimeMillis() - arrivalTime;
        }
    }
    
    public void setPriority(int priority) { this.priority = priority; }
    public void setBurstTime(long burstTime) { this.burstTime = burstTime; }
    public void addWaitingTime(long time) { this.waitingTime += time; }
    public void incrementContextSwitch() { this.contextSwitchCount++; }
    
    public void saveContext(String key, Object value) {
        context.put(key, value);
    }
    
    public Object loadContext(String key) {
        return context.get(key);
    }
    
    public void calculateTurnaroundTime() {
        this.turnaroundTime = System.currentTimeMillis() - arrivalTime;
    }
    
    @Override
    public String toString() {
        return String.format("PCB[PID=%d, State=%s, Priority=%d, Burst=%dms, Wait=%dms, CS=%d]",
            processId, state, priority, burstTime, waitingTime, contextSwitchCount);
    }
}

/**
 * Represents a schedulable task in the CustomScheduler.
 * 
 * A Task is an abstraction that wraps a Runnable with scheduling metadata.
 * This allows the scheduler to make decisions based on:
 * - Priority (for Priority scheduling)
 * - Burst time (for SJF scheduling)
 * - Arrival time (for FCFS and Round Robin)
 * 
 * COMPONENTS:
 * - taskId: Unique identifier
 * - priority: Scheduling priority (1-10)
 * - burstTime: Estimated execution time
 * - runnable: The actual code to execute
 * - pcb: Associated Process Control Block
 * - arrivalTime: When task entered system
 * - startTime: When task began execution
 * 
 * LIFECYCLE:
 * 1. Task created with priority and burst time estimate
 * 2. Task added to scheduler's ready queue
 * 3. Scheduler selects task based on algorithm
 * 4. Task executes via execute() method
 * 5. Task completes or is preempted
 * 
 * USAGE:
 * Tasks are created by StockExchange for each trader operation and
 * managed by CustomScheduler throughout their execution.
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see CustomScheduler
 * @see ProcessControlBlock
 */
public class Task {
    private int taskId;
    private ProcessControlBlock pcb;
    private Runnable runnable;
    private long arrivalTime;
    private long startTime;
    private long burstTime;
    private int priority;
    
    public Task(int taskId, int priority, long burstTime, Runnable runnable) {
        this.taskId = taskId;
        this.priority = priority;
        this.burstTime = burstTime;
        this.runnable = runnable;
        this.arrivalTime = System.currentTimeMillis();
        this.startTime = 0;
        this.pcb = new ProcessControlBlock(taskId, priority);
    }
    
    public int getTaskId() { return taskId; }
    public ProcessControlBlock getPCB() { return pcb; }
    public Runnable getRunnable() { return runnable; }
    public long getArrivalTime() { return arrivalTime; }
    public long getStartTime() { return startTime; }
    public long getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public void setPriority(int priority) { 
        this.priority = priority;
        this.pcb.setPriority(priority);
    }
    
    public void execute() {
        if (runnable != null) {
            runnable.run();
        }
    }
    
    @Override
    public String toString() {
        return String.format("Task[ID=%d, Priority=%d, Burst=%dms, State=%s]",
            taskId, priority, burstTime, pcb.getState());
    }
}

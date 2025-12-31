# Quick Reference Guide

## 🚀 Getting Started

### Compile
```bash
cd src
javac *.java
```

### Run
```bash
# Normal mode (simplified output)
java StockSimulator

# Verbose mode (detailed OS operation visibility)
java StockSimulator -v
```

### Output Location
```
../logs/transactions.log - Transaction history
Console - Performance reports
```

**Verbose Mode** shows detailed:
- Process state transitions (NEW→READY→RUNNING→WAITING→TERMINATED)
- Scheduler ready queue with priorities
- Memory page faults/hits with frame numbers
- Semaphore synchronization operations
- IPC message queue and shared memory accesses
- File system transactions (WAL operations)
- Market price updates with supply/demand dynamics

## 📂 File Overview

| File | Purpose | Key Concepts |
|------|---------|--------------|
| **StockSimulator.java** | Main entry point | Runs 3 simulations with different configs |
| **StockExchange.java** | OS kernel simulator | Coordinates all components |
| **CustomScheduler.java** | CPU scheduling | FCFS, SJF, Priority, Round Robin |
| **VirtualMemoryManager.java** | Memory management | Paging, LRU, FIFO, page faults |
| **TraderProcess.java** | Process simulation | PCB, state transitions, trading logic |
| **ProcessControlBlock.java** | Process metadata | States, metrics, context |
| **Task.java** | Schedulable unit | Wraps runnable with scheduling info |
| **MessageQueue.java** | IPC mechanism | Message passing, bounded queue |
| **SharedMemory.java** | IPC mechanism | Shared data, reader-writer locks |
| **CustomSemaphore.java** | Synchronization | Resource coordination, fairness |
| **TransactionalFS.java** | File system | WAL, ACID, crash recovery |
| **Stock.java** | Data structure | Stock representation |
| **Order.java** | Data structure | Buy/sell orders |
| **Portfolio.java** | Data structure | Trader holdings |
| **PerformanceMonitor.java** | Metrics | System-wide performance tracking |

## 🔧 Configuration Parameters

### In StockSimulator.java
```java
int numTraders = 5;                    // Number of trader processes
```

### In StockExchange.java (constructor)
```java
new CustomScheduler(algo, 100);        // 100ms time quantum for RR
new VirtualMemoryManager(20, 100, ...); // 20 physical frames, 100 virtual pages
new MessageQueue(1000);                // 1000 message capacity
new CustomSemaphore("...", 5);         // Max 5 concurrent traders
```

### Scheduling Algorithms
```java
CustomScheduler.Algorithm.FCFS          // First-Come-First-Served
CustomScheduler.Algorithm.SJF           // Shortest Job First
CustomScheduler.Algorithm.PRIORITY      // Priority with aging
CustomScheduler.Algorithm.ROUND_ROBIN   // Time-sliced (100ms quantum)
```

### Page Replacement Policies
```java
VirtualMemoryManager.ReplacementPolicy.LRU   // Least Recently Used
VirtualMemoryManager.ReplacementPolicy.FIFO  // First-In-First-Out
```

## 📊 Understanding Output

### Process Statistics
```
Trader 0: 10 trades, 12 context switches, 145ms wait time
```
- **trades**: Orders executed successfully
- **context switches**: Times process was preempted/resumed
- **wait time**: Time spent in READY state

### Memory Statistics
```
PageFaults=45, Hits=312, FaultRate=12.61%, Thrashing=NO
```
- **Page Faults**: Times requested page wasn't in memory
- **Hits**: Successful page accesses
- **Fault Rate**: Faults / (Faults + Hits)
- **Thrashing**: YES if fault rate > 50%

### Performance Metrics
```
Throughput: 9.23 trades/sec
Avg Context Switch Time: 80.90ms
```
- **Throughput**: System efficiency (higher = better)
- **Context Switch Time**: Scheduling overhead (lower = better)

## 🎯 OS Concepts Mapping

### Process Management
- **Files**: ProcessControlBlock.java, TraderProcess.java, Task.java
- **Concepts**: States (NEW→READY→RUNNING→WAITING→TERMINATED), PCB, context switching

### CPU Scheduling
- **Files**: CustomScheduler.java
- **Concepts**: FCFS, SJF, Priority, RR, preemption, aging, time quantum

### Memory Management
- **Files**: VirtualMemoryManager.java
- **Concepts**: Paging, page tables, page faults, LRU, FIFO, thrashing

### Process Synchronization
- **Files**: CustomSemaphore.java, SharedMemory.java
- **Concepts**: Semaphores, mutual exclusion, reader-writer locks, deadlock prevention

### IPC
- **Files**: MessageQueue.java, SharedMemory.java
- **Concepts**: Message passing vs shared memory, bounded buffer, producer-consumer

### File Systems
- **Files**: TransactionalFS.java
- **Concepts**: WAL, ACID, transactions, durability, crash recovery

## 🔬 Experiments to Try

### 1. Compare Scheduling Algorithms
**Question**: Which algorithm gives best throughput?

**Method**: Run all configurations, compare "Throughput" metric

**Expected**: Priority may win for short jobs, RR for fairness

### 2. Analyze Page Replacement
**Question**: Does LRU really beat FIFO?

**Method**: Compare "Fault Rate" between configs

**Expected**: LRU should have lower fault rate (better locality)

### 3. Measure Context Switch Overhead
**Question**: How much does preemption cost?

**Method**: Compare FCFS (non-preemptive) vs RR (preemptive)

**Expected**: RR has more context switches, higher overhead

### 4. Detect Thrashing
**Question**: What causes thrashing?

**Method**: 
1. Reduce physical frames to 5 in VirtualMemoryManager constructor
2. Run simulation
3. Observe "Thrashing: YES" and degraded performance

### 5. IPC Performance
**Question**: How many messages can the system handle?

**Method**: Check "Messages Sent/Received" in output

**Expected**: Should be ~50 messages (10 trades × 5 traders)

## 🐛 Common Issues

### Compilation Errors
```bash
# If you get "class not found"
cd src
javac *.java

# If you get "cannot find symbol"
# Check that all files are in src directory
ls src/*.java
```

### Runtime Errors
```bash
# OutOfMemoryError
java -Xmx512m StockSimulator

# FileNotFoundException
# logs directory will be created automatically
```

### Performance Issues
```bash
# If simulation hangs
# Increase semaphore permits or reduce traders

# If too many page faults
# Increase physical frames in VirtualMemoryManager
```

## 📖 Learning Path

### Week 1: Understand Base System
- Read README.md fully
- Study Stock, Order, Portfolio classes
- Run simulation once, read output carefully

### Week 2: Process Management
- Study ProcessControlBlock.java (states, metrics)
- Study TraderProcess.java (lifecycle)
- Trace one trader's execution path

### Week 3: Scheduling & Memory
- Study CustomScheduler.java (all 4 algorithms)
- Study VirtualMemoryManager.java (paging, replacement)
- Compare FCFS vs RR performance

### Week 4: Apply to Report
- Document your experiments
- Explain trade-offs between algorithms
- Include performance graphs (manually from data)

---

# Understanding the Stock Trading Simulator - Detailed Explanation

## 📘 Core Concepts Explained

### 1. What are Pages in Virtual Memory?

**Pages** are fixed-size blocks of virtual memory, like dividing a book into chapters:

```
Virtual Memory (100 pages)     Physical Memory (20 frames)
┌──────────────┐              ┌──────────────┐
│ Page 0 (T0)  │──────────────→│ Frame 0      │
│ Page 1 (T1)  │──────────────→│ Frame 1      │
│ Page 2 (T2)  │──────────────→│ Frame 2      │
│ Page 3 (T3)  │──────────────→│ Frame 3      │
│ Page 4 (T4)  │──────────────→│ Frame 4      │
│ Page 5       │              │ Frame 5      │
│ ...          │              │ ...          │
│ Page 99      │              │ Frame 19     │
└──────────────┘              └──────────────┘
```

**In our simulator:**
- Each trader's portfolio = 1 virtual page
- 5 traders = 5 pages needed
- Physical memory can hold 20 frames
- When trader accesses portfolio → page table lookup:
  - **Page Hit**: Page is in physical memory (fast!)
  - **Page Fault**: Page not in memory (must load from "disk")

### 2. Why are Page Metrics Similar Across Algorithms?

You noticed that page faults and hits are roughly the same (5 faults, 45 hits = 10% fault rate) regardless of scheduling algorithm. **This is correct!** Here's why:

```
Scheduling Algorithm (FCFS vs Priority vs RR)
        ↓
Determines WHICH trader runs WHEN
        ↓
But traders still access their OWN portfolios in the SAME pattern
        ↓
Same virtual memory access pattern
        ↓
Same page faults and hits
```

**Key Insight:**
- **CPU Scheduling** controls *when* processes run
- **Virtual Memory** controls *where* data is stored
- These are **independent subsystems**!

**When would page metrics differ?**
- If physical memory was very small (e.g., 3 frames for 5 traders)
- If we ran different workloads in each simulation
- If page replacement policy caused different eviction patterns

**Current scenario:**
- 20 frames for 5 traders = plenty of space
- All 5 pages fit in memory after initial faults
- No page replacement needed → LRU vs FIFO doesn't matter yet!

### 3. Why is the Message Queue Needed?

**Without Message Queue (Direct Shared Memory):**
```
Trader 1 ──┐
           ├──→ [Exchange Data] ← RACE CONDITIONS!
Trader 2 ──┘
```
- Both traders modify exchange data simultaneously
- Portfolio updates can be lost
- Order totals become incorrect
- **Classic race condition!**

**With Message Queue (Message Passing):**
```
Trader 1 → [Message Queue] → Exchange
Trader 2 → [Message Queue] → Exchange
```
- Traders send orders as messages
- Exchange processes orders **one at a time**
- No direct memory sharing = no race conditions
- **Thread-safe by design!**

**Real-world analogy:**
- **Without queue**: Everyone shouting orders at once in a chaotic room
- **With queue**: Taking a number and waiting in line at a bank

**This demonstrates:**
- **IPC (Inter-Process Communication)** - how processes talk to each other
- **Message Passing** vs **Shared Memory** trade-offs
- **Producer-Consumer pattern** - traders produce orders, exchange consumes them

---

## 🔍 What the Verbose Mode Shows (Use `-v` flag)

### Process State Transitions (Process Lifecycle)
```
[PROCESS] Trader-0 created (Priority=8) → NEW state
[PROCESS] Trader-0 → READY (waiting in scheduler queue)
[PROCESS] Trader-0 → RUNNING (executing trades)
[PROCESS] Trader-0 → TERMINATED (completed 6 trades)
```

**State diagram:**
```
NEW → READY → RUNNING → WAITING → READY → ... → TERMINATED
```

### Scheduler Ready Queue with Priorities
```
[SCHEDULER] Initial Ready Queue:
  - Trader-0 (Priority=8, State=NEW)
  - Trader-1 (Priority=9, State=NEW)
  - Trader-2 (Priority=9, State=NEW)
  - Trader-3 (Priority=3, State=NEW)   ← Lowest priority
  - Trader-4 (Priority=10, State=NEW)  ← Highest priority
```

**Priority Scheduling:** Trader-4 (priority 10) runs before Trader-3 (priority 3)
**Round Robin:** All traders get equal time slices regardless of priority
**FCFS:** Order of arrival determines execution order

### Memory Access Patterns (Page Faults and Hits)
```
[MEMORY] Trader-0 accessing virtual page 0
  [VM] Page FAULT: Virtual page 0 not in memory! Loading...
  [VM] Allocating new physical frame 0 (free frames available)

[MEMORY] Trader-0 accessing virtual page 0
  [VM] Page HIT: Virtual page 0 → Physical frame 0
```

**First access** = Page Fault (cold miss)
**Subsequent accesses** = Page Hit (data already loaded)

### Process Synchronization (Semaphore)
```
[SYNC] Trader-0 waiting for trading semaphore...     ← WAITING state
[SYNC] Trader-0 acquired semaphore → RUNNING         ← Got resource
[SYNC] Trader-0 released semaphore                   ← Freed resource
```

**Semaphore limits concurrent traders to 5** (like a resource pool)

### IPC - Message Queue
```
[IPC] Trader-0 sent order via message queue → SUCCESS
```
**Orders are sent as messages** (not shared memory writes)

### IPC - Shared Memory
```
[IPC] Trader-0 read AAPL price from shared memory: $150.00
```
**Stock prices stored in shared memory** for fast reads by all traders

### File System Transactions (WAL)
```
[FS] Trader-0 began transaction #1
[TRADE] Trader-0 attempting BUY 10 AAPL @ $150.00
[FS] Trader-0 committed transaction #1 (WAL flushed)
```

**Write-Ahead Logging ensures durability:**
1. Log the operation first
2. Commit the transaction
3. Flush to disk (fsync)
4. Now it's permanent!

### Market Dynamics (Supply & Demand)
```
[MARKET] AAPL price increased: $150.00 → $165.00 (buy pressure)
[MARKET] AAPL price decreased: $165.00 → $161.70 (sell pressure)
```

**Now prices respond to trading activity:**
- **BUY orders** → Price increases (demand ↑)
- **SELL orders** → Price decreases (supply ↑)
- **1% change per share traded** (realistic market impact)

---

## 📊 Comparing Scheduling Algorithms

### Priority Scheduling (with Aging)
```
Ready Queue: [T4(10), T1(9), T2(9), T0(8), T3(3)]
              ↑ runs first        runs last ↑
```
**Characteristics:**
- Higher priority processes run first
- Aging prevents starvation (priority increases if waiting too long)
- Good for real-time systems
- **Risk:** Priority inversion (low priority holds resource needed by high priority)

### Round Robin (Time Quantum = 100ms)
```
Ready Queue: [T0, T1, T2, T3, T4] → [T1, T2, T3, T4, T0] → ...
              ↑ runs 100ms          ↑ runs 100ms
```
**Characteristics:**
- Fair time-sharing (everyone gets equal CPU time)
- Good response time for interactive tasks
- More context switches (overhead)
- **Best for:** Multitasking systems (like desktop OS)

### FCFS (First-Come-First-Served)
```
Arrival: T0, T1, T2, T3, T4
Execution: T0 → T1 → T2 → T3 → T4 (same order)
```
**Characteristics:**
- Simplest algorithm (no priorities, no preemption)
- Low context switch overhead
- **Problem:** Convoy effect (long process blocks short ones)
- **Best for:** Batch processing systems

---

## 🎯 Performance Analysis

### Throughput Comparison
From the simulation output:
- **Priority + LRU:** 15.76 trades/sec
- **Round Robin + FIFO:** 16.53 trades/sec ← **Winner!**
- **FCFS + LRU:** 16.48 trades/sec

**Why RR wins:**
- Fair time-sharing prevents any trader from hogging CPU
- All traders make progress simultaneously
- Better parallelism utilization

### Context Switch Analysis
All algorithms: ~24-25 context switches
- **Priority:** May have fewer switches (runs high-priority to completion)
- **Round Robin:** More switches due to time quantum expiration
- **FCFS:** Fewest switches (no preemption)

### Memory Performance
All algorithms: 10% page fault rate
- **Why same?** Memory access pattern independent of scheduling
- **5 page faults:** One per trader (first access)
- **45 page hits:** All subsequent accesses

---

## 🔬 Experimental Ideas

### 1. Force Page Replacement
Change `physicalFrames` from 20 to 3:
```java
this.vmm = new VirtualMemoryManager(3, 100, pageReplacement);
```
**Expected:** LRU will outperform FIFO (better locality)

### 2. Create Priority Inversion
Make low-priority trader hold semaphore longer:
```java
if (traderId == 3) Thread.sleep(5000); // Block for 5 seconds
```
**Expected:** High-priority traders starve while waiting

### 3. Overload Message Queue
Reduce capacity from 1000 to 10:
```java
this.orderQueue = new MessageQueue(10);
```
**Expected:** Orders rejected, more rollbacks

### 4. Stress Test Scheduler
Increase traders from 5 to 50:
```java
int numTraders = 50;
```
**Expected:** 
- Priority scheduling shows clear advantages
- More page faults (working set exceeds physical memory)
- Possible thrashing if memory too small

---

## 💡 Key Takeaways

1. **Virtual Memory is Independent of Scheduling:**
   - Page faults depend on memory access patterns, not scheduling algorithm
   - Scheduling affects *when* processes run; paging affects *where* data is stored

2. **Message Queue Prevents Race Conditions:**
   - Message passing = explicit communication (safer)
   - Shared memory = implicit communication (faster but needs synchronization)

3. **Different Algorithms Serve Different Goals:**
   - **Priority:** Real-time systems (meet deadlines)
   - **Round Robin:** Interactive systems (fairness)
   - **FCFS:** Batch systems (simplicity)

4. **Stock Prices Now Reflect Market Dynamics:**
   - Buy pressure → price ↑
   - Sell pressure → price ↓
   - Realistic market simulation!

5. **Process States Matter:**
   - NEW → READY → RUNNING → WAITING → TERMINATED
   - Context switches happen at state transitions
   - Semaphores cause WAITING state

---

## 📚 Further Reading

- **Operating System Concepts** (Silberschatz): Chapters 5 (Scheduling), 9 (Virtual Memory), 6 (Synchronization)
- **Modern Operating Systems** (Tanenbaum): Chapter 3 (Memory Management)
- **The Art of Multiprocessor Programming** (Herlihy): Chapter 8 (Monitors and Blocking Synchronization)

---

## 🎓 Additional Learning Resources

### Week 1: Understand Base System
- Read README.md fully
- Study Stock, Order, Portfolio classes
- Run simulation once, read output carefully

### Week 2: Process Management
- Study ProcessControlBlock.java (states, metrics)
- Study TraderProcess.java (lifecycle)

### Week 4: IPC & File Systems
- Study MessageQueue.java and SharedMemory.java (compare approaches)
- Study CustomSemaphore.java (synchronization)
- Study TransactionalFS.java (WAL protocol)

### Week 5: Integration & Experimentation
- Study StockExchange.java (how everything connects)
- Run experiments from section above
- Modify parameters and observe effects

## 🎓 Key Takeaways

1. **Scheduling matters**: Different algorithms have different strengths
2. **Memory is limited**: Virtual memory extends apparent size
3. **Page replacement impacts performance**: LRU generally better than FIFO
4. **IPC has tradeoffs**: Message passing (safe) vs shared memory (fast)
5. **Synchronization is critical**: Race conditions cause bugs
6. **Transactions ensure consistency**: WAL provides durability
7. **Context switching has cost**: Balance fairness vs overhead
8. **Monitoring is essential**: Can't optimize what you don't measure

## 📞 Support

This is an educational project. For questions:
1. Review README.md
2. Read code comments (every file has detailed documentation)
3. Check this Quick Reference
4. Experiment with parameters
5. Trace execution with print statements

## 🎯 Next Steps

After mastering this project, consider:
- Implement SJF preemptive (SRTF)
- Add Clock page replacement algorithm
- Implement Banker's algorithm for deadlock avoidance
- Add multi-level feedback queue scheduler
- Implement memory compaction
- Add network sockets for distributed trading
- Create GUI visualization of process states

---

**Happy Learning! 🚀**

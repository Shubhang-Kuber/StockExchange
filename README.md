# Stock Trading Simulator - Advanced OS Concepts

A comprehensive stock trading simulator demonstrating graduate-level Operating Systems concepts through a realistic market trading system. This project implements CPU scheduling, virtual memory management, inter-process communication, transactional file systems, and process control mechanisms.

## 🎯 Project Overview

This simulator models a stock exchange where multiple trader processes execute trades concurrently while demonstrating core OS concepts:

- **CPU Scheduling**: Four scheduling algorithms (FCFS, SJF, Priority, Round Robin)
- **Virtual Memory**: Paging with LRU and FIFO page replacement
- **IPC**: Message queues, shared memory, and semaphores
- **File Systems**: Write-ahead logging with transactional guarantees
- **Process Management**: Complete process lifecycle with PCB state tracking
- **Performance Analysis**: Comprehensive metrics and comparative analysis

## 📁 Project Structure

```
StockExchange/
├── README.md                      # Project documentation
├── .gitignore                     # Git ignore rules
│
├── src/                           # Source code
│   ├── StockSimulator.java       # Main simulation runner
│   ├── StockExchange.java        # OS kernel simulation
│   │
│   ├── Core Data Structures
│   │   ├── Stock.java            # Stock representation
│   │   ├── Order.java            # Trade order objects
│   │   └── Portfolio.java        # Trader portfolio management
│   │
│   ├── OS Components
│   │   ├── CustomScheduler.java     # CPU scheduling algorithms
│   │   ├── Task.java                # Schedulable task abstraction
│   │   ├── ProcessControlBlock.java # Process control blocks
│   │   ├── VirtualMemoryManager.java # Virtual memory & paging
│   │   ├── MessageQueue.java        # Message passing IPC
│   │   ├── SharedMemory.java        # Shared memory IPC
│   │   ├── CustomSemaphore.java     # Semaphore synchronization
│   │   └── TransactionalFS.java     # File system with WAL
│   │
│   └── Process Management
│       ├── TraderProcess.java       # Trader process implementation
│       └── PerformanceMonitor.java  # Performance tracking
│
├── docs/
│   └── QUICK_REFERENCE.md         # Quick reference guide
│
└── logs/                          # Runtime logs (gitignored)
    └── transactions.log           # Transaction log (generated)
```

## 🔧 Technical Implementation

### 1. CPU Scheduling Algorithms

**File**: `CustomScheduler.java`

Implements four scheduling algorithms with full state management:

- **FCFS (First-Come-First-Served)**: Non-preemptive, processes in arrival order
- **SJF (Shortest Job First)**: Non-preemptive, shortest burst time first
- **Priority Scheduling**: Preemptive with aging to prevent starvation
- **Round Robin**: Preemptive with configurable time quantum (100ms)

**Key Features**:
- Priority aging mechanism prevents indefinite postponement
- Context switching with PCB state preservation
- Ready queue and waiting queue management
- Performance metrics: context switches, turnaround time, waiting time

### 2. Virtual Memory Management

**File**: `VirtualMemoryManager.java`

Simulates demand paging with page replacement:

- **Page Table**: Maps virtual pages to physical frames
- **Page Faults**: Handled automatically with page loading
- **LRU Replacement**: Least Recently Used algorithm with timestamp tracking
- **FIFO Replacement**: First-In-First-Out queue-based replacement
- **Thrashing Detection**: Monitors page fault rate (>50% indicates thrashing)

**Configuration**:
- Physical Frames: 20
- Virtual Pages: 100 (5:1 oversubscription ratio)
- Page fault tracking and hit rate calculation

### 3. Inter-Process Communication

#### Message Queue (`MessageQueue.java`)
- **Capacity**: 1000 messages
- **Operations**: Non-blocking send/receive
- **Use Case**: Order submission from traders to exchange
- **Thread-Safe**: Lock-based synchronization

#### Shared Memory (`SharedMemory.java`)
- **Read/Write Locks**: Concurrent reads, exclusive writes
- **Use Case**: Real-time stock price distribution
- **Performance**: Low-latency data sharing

#### Semaphores (`CustomSemaphore.java`)
- **Fair Semaphore**: FIFO ordering
- **Permits**: Configurable (default 5 concurrent traders)
- **Metrics**: Wait time tracking, acquire/release counts

### 4. Transactional File System

**File**: `TransactionalFS.java`

Implements ACID properties with write-ahead logging:

- **Write-Ahead Logging (WAL)**: Log before commit
- **fsync**: Force flush to persistent storage
- **Transactions**: Begin, commit, rollback operations
- **Crash Recovery**: Replay log entries after crash
- **Durability**: All committed transactions survive crashes

**Log Format**: `[TXN:id] operation @ timestamp | data`

### 5. Process Control Blocks

**File**: `ProcessControlBlock.java`

Complete process lifecycle management:

**States**: NEW → READY → RUNNING → WAITING → TERMINATED

**Tracked Metrics**:
- Process ID and priority
- Burst time and arrival time
- Waiting time and response time
- Turnaround time
- Context switch count
- Context data (simulated registers)

### 6. Stock Exchange Kernel

**File**: `StockExchange.java`

Coordinates all OS components:

- Initializes 5 stocks (AAPL, GOOGL, MSFT, AMZN, TSLA)
- Creates configurable number of trader processes
- Manages real-time market price updates (±2% fluctuation)
- Monitors trading activity
- Generates comprehensive performance reports

## 🚀 Compilation and Execution

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Terminal/Command prompt

### Compile
```bash
cd src
javac *.java
```

### Run
```bash
# Normal mode (simplified output - recommended for reports)
java StockSimulator

# Verbose mode (detailed OS operation visibility - for understanding)
java StockSimulator -v
```

### Clean
```bash
rm -f *.class
```

**Normal mode** provides clean performance reports perfect for analysis and lab reports.

**Verbose mode** (`-v` flag) shows detailed:
- Process state transitions (NEW→READY→RUNNING→WAITING→TERMINATED)
- Scheduler ready queue with priorities
- Memory page faults/hits with frame numbers and explanations
- Semaphore synchronization operations
- IPC operations (message queue sends, shared memory reads/writes)
- File system transactions with Write-Ahead Logging
- Market price updates driven by supply and demand

Use normal mode for running experiments and collecting metrics. Use verbose mode to understand exactly how OS concepts are working under the hood.

## 📊 Simulation Configurations

The simulator runs three configurations automatically:

### Configuration 1: Priority + LRU
- **Scheduler**: Priority scheduling with aging
- **Memory**: LRU page replacement
- **Focus**: Demonstrates priority inversion prevention

### Configuration 2: Round Robin + FIFO
- **Scheduler**: Round Robin (100ms quantum)
- **Memory**: FIFO page replacement
- **Focus**: Time-sharing fairness

### Configuration 3: FCFS + LRU
- **Scheduler**: First-Come-First-Served
- **Memory**: LRU page replacement
- **Focus**: Baseline comparison

## 📈 Performance Metrics

Each simulation tracks and reports:

### Process Metrics
- **Trades Executed**: Total orders processed per trader
- **Context Switches**: Number of PCB context switches
- **Wait Time**: Time spent in ready queue
- **Turnaround Time**: Total time from arrival to termination
- **Response Time**: Time to first execution

### Memory Metrics
- **Page Faults**: Count of page not in memory
- **Page Hits**: Count of successful page accesses
- **Fault Rate**: Percentage of page faults
- **Thrashing Status**: Yes/No based on fault rate

### IPC Metrics
- **Messages Sent/Received**: Message queue throughput
- **Shared Memory R/W**: Read and write operation counts
- **Semaphore Wait Time**: Average time waiting for resource
- **Semaphore Acquires/Releases**: Total synchronization operations

### File System Metrics
- **Transactions**: Total committed transactions
- **Flushes**: Number of fsync operations
- **Buffered Entries**: Pending log entries

### Overall Performance
- **Throughput**: Trades per second
- **Total Execution Time**: Milliseconds from start to completion
- **Context Switch Overhead**: Average time per context switch
- **Portfolio Values**: Final wealth of each trader

## 🎓 OS Concepts Demonstrated

### 1. CPU Scheduling
- Multiple scheduling algorithms
- Context switching overhead
- Priority inversion and aging
- Preemptive vs non-preemptive scheduling
- Time quantum effects (Round Robin)

### 2. Memory Management
- Virtual memory abstraction
- Page tables and address translation
- Page replacement algorithms comparison
- Page fault handling
- Thrashing detection and analysis
- Working set size effects

### 3. Process Synchronization
- Race condition prevention
- Critical section management
- Deadlock-free design
- Semaphore-based coordination
- Lock contention analysis

### 4. Inter-Process Communication
- Message passing vs shared memory
- Producer-consumer patterns
- Reader-writer locks
- IPC performance comparison

### 5. File Systems
- Write-ahead logging protocol
- Transaction atomicity
- Crash recovery mechanisms
- Durability guarantees
- Log-structured design

### 6. Process Management
- Process state diagrams
- Process creation and termination
- Context switching mechanics
- Process control blocks
- Process scheduling integration

## 📝 Sample Output

```
╔════════════════════════════════════════════════════════╗
║   Stock Trading Simulator - OS Concepts Demo           ║
╚════════════════════════════════════════════════════════╝

============================================================
SIMULATION 1: Priority Scheduling + LRU Page Replacement
============================================================

=== Stock Exchange Starting ===
Scheduler: PRIORITY
Virtual Memory: VMM[Policy=LRU, Frames=0/20, ...]
Traders: 5
Stocks: 5
================================

--- Trading Status ---
Scheduler[Algo=PRIORITY, ReadyQueue=2, WaitingQueue=0, ContextSwitches=45]
VMM[Policy=LRU, Frames=15/20, PageFaults=23, Hits=187, FaultRate=10.95%]
...

============================================================
FINAL PERFORMANCE REPORT
============================================================

--- Trader Statistics ---
Trader 0: 10 trades, 12 context switches, 145ms wait time
Trader 1: 10 trades, 15 context switches, 203ms wait time
...

--- OS Component Statistics ---
Scheduler: PRIORITY
  Total Context Switches: 67
Virtual Memory: VMM[...]
  Page Faults: 45
  Page Hits: 312
  Fault Rate: 12.61%
  Thrashing: NO
...

--- Performance Metrics ---
Total Execution Time: 5420ms
Total Trades: 50
Throughput: 9.23 trades/sec
Avg Context Switch Time: 80.90ms
```

## 🔬 Experimental Analysis

### Key Questions to Explore

1. **Which scheduling algorithm provides best throughput?**
   - Compare FCFS vs Round Robin vs Priority

2. **How does page replacement affect performance?**
   - LRU vs FIFO fault rates and thrashing

3. **What is the overhead of context switching?**
   - Measure time per context switch

4. **How do IPC mechanisms compare?**
   - Message queue latency vs shared memory

5. **What causes thrashing?**
   - Analyze working set size vs physical frames

## 🛠️ Configuration Options

Modify these parameters in `StockSimulator.java`:

```java
int numTraders = 5;              // Number of concurrent traders
int physicalFrames = 20;         // Physical memory frames
int virtualPages = 100;          // Virtual memory pages
int timeQuantum = 100;           // RR quantum (ms)
int maxConcurrentTraders = 5;    // Semaphore permits
int messageQueueCapacity = 1000; // Max pending orders
```

## 🐛 Troubleshooting

### Issue: "java.io.FileNotFoundException"
**Solution**: Ensure logs directory exists or code will create it automatically

### Issue: High page fault rate (>50%)
**Solution**: Increase `physicalFrames` or decrease `numTraders`

### Issue: OutOfMemoryError
**Solution**: Reduce `virtualPages` or increase JVM heap: `java -Xmx512m StockSimulator`

## 📚 Learning Objectives

After studying this project, you should understand:

1. ✅ How CPU schedulers make decisions and trade-offs
2. ✅ Virtual memory mechanics and page replacement algorithms
3. ✅ IPC mechanisms and when to use each
4. ✅ Transaction logging and crash recovery
5. ✅ Process lifecycle and state transitions
6. ✅ Performance measurement and analysis
7. ✅ Thread synchronization and race conditions
8. ✅ Real-world OS design patterns

## 🎯 Extension Ideas

1. **Add SJF Scheduler**: Implement shortest-job-first preemptive scheduling
2. **Clock Page Replacement**: Add second-chance algorithm
3. **Priority Inheritance**: Implement to prevent priority inversion
4. **Deadlock Detection**: Add Banker's algorithm
5. **Multi-level Queue**: Separate interactive and batch processes
6. **Memory Compaction**: Implement defragmentation
7. **Network IPC**: Add socket-based communication
8. **Distributed Trading**: Multi-exchange coordination

## 📖 References

### Operating Systems Textbooks
- **Operating System Concepts** (Silberschatz, Galvin, Gagne)
  - Chapters: 5 (CPU Scheduling), 9 (Virtual Memory), 6 (Synchronization)
  
- **Modern Operating Systems** (Tanenbaum, Bos)
  - Chapters: 2 (Processes), 3 (Memory), 6 (Deadlocks)

### Research Papers
- *The Working Set Model for Program Behavior* - Peter Denning (1968)
- *An Optimality Theory of Concurrency Control* - Kung & Papadimitriou (1979)

### Online Resources
- [OSDev Wiki](https://wiki.osdev.org/) - OS development reference
- [Linux Kernel Documentation](https://www.kernel.org/doc/html/latest/)

## 👥 Authors

- Suraj
- Shubhang

OS Lab Project

## License

This project is for educational purposes as part of an Operating Systems course lab assignment.

## 🙏 Acknowledgments

- Inspired by real-world stock trading systems
- OS concepts from undergraduate/graduate OS courses
- Java concurrency utilities for thread-safe implementations

---

**Note**: This is a simulation for educational purposes. Not suitable for actual trading or production use.

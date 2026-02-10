# 📖 QUICK START GUIDE - Web Dashboard

## 🎯 3 Steps to Run

### STEP 1️⃣: Open Terminal
Navigate to the source directory:
```bash
cd c:\Shubhang Kuber\Engineering 2024-2028\2nd Year All Docs\3rd Semester\OS\StockExchange\src
```

### STEP 2️⃣: Compile Java
```bash
javac *.java
```
✅ You should see no errors

### STEP 3️⃣: Run Simulator with Web
```bash
java StockSimulator -web -algo FCFS
```

You should see:
```
╔════════════════════════════════════════════════════════╗
║   Stock Trading Simulator - OS Concepts Demo           ║
╚════════════════════════════════════════════════════════╝
✓ Web Server started on http://localhost:8080
```

---

## 🌐 Open Dashboard (STEP 4️⃣)

Open your web browser and go to:
```
http://localhost:8080
```

You should see the dashboard loading...

---

## 📊 Dashboard Layout

```
┌─────────────────────────────────────────────────────────┐
│  🏛️ Stock Exchange OS Simulator                         │
│  Real-Time Visualization of Operating System Concepts  │
│                                                         │
│  [FCFS] [SJF] [PRIORITY] [ROUND ROBIN]                 │
│                                                         │
│  Status: ● Running  |  Scheduler: FCFS  |  Time: 45s   │
└─────────────────────────────────────────────────────────┘

┌──────────────────────────────────┐ ┌──────────────────────────────────┐
│ 📊 System Overview               │ │ ⚙️ CPU Scheduler                 │
│ • Processes: 12                  │ │ • Algorithm: FCFS                │
│ • Running: 3                     │ │ • Avg Wait: 325.5 ms             │
│ • Completed: 9                   │ │ • Avg Turnaround: 475.3 ms       │
│ • Context Switches: 145          │ │ [Line Chart]                     │
└──────────────────────────────────┘ └──────────────────────────────────┘

┌──────────────────────────────────┐ ┌──────────────────────────────────┐
│ 💾 Virtual Memory                │ │ 📈 Stock Prices                  │
│ • Page Faults: 234               │ │ [Line Chart - 5 stocks]          │
│ • Page Hits: 1230                │ │ • AAPL: $150.25                  │
│ • Fault Rate: 16.02%             │ │ • GOOGL: $145.80                 │
│ • Thrashing: NO ✓                │ │ • MSFT: $380.50                  │
└──────────────────────────────────┘ └──────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ 🔄 Process States & Statistics                          │
│ ┌─────────────────────────────────────────────────────┐ │
│ │ PID │ State  │ Priority │ Burst │ Wait │ Turnaround │ │
│ ├─────────────────────────────────────────────────────┤ │
│ │  1  │ RUNNING│    5     │ 100ms │ 250ms│   350ms    │ │
│ │  2  │ READY  │    3     │  0ms  │ 500ms│    0ms     │ │
│ │  3  │ WAITING│    7     │ 50ms  │ 120ms│   170ms    │ │
│ └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘

┌──────────────────────────────────┐ ┌──────────────────────────────────┐
│ 📨 Message Queue                 │ │ 🔗 Shared Memory & Semaphore     │
│ • Sent: 567                      │ │ • Mem Reads: 1204                │
│ • Received: 567                  │ │ • Mem Writes: 456                │
│ [Bar Chart]                      │ │ • Sem Acquires: 892              │
│                                  │ │ • Sem Releases: 891              │
└──────────────────────────────────┘ └──────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ 📋 Recent Trade History                                 │
│ 2024-02-04 10:30:45 - AAPL: BUY 100 @ $150.25          │
│ 2024-02-04 10:31:12 - GOOGL: SELL 50 @ $145.80         │
│ 2024-02-04 10:32:33 - MSFT: BUY 75 @ $380.50           │
│ 2024-02-04 10:33:01 - AMZN: SELL 30 @ $175.30          │
└─────────────────────────────────────────────────────────┘
```

---

## 🎛️ Algorithm Buttons

Click to see the run command for each algorithm:

### FCFS (First-Come-First-Served)
```bash
java StockSimulator -web -algo FCFS
```

### SJF (Shortest Job First)
```bash
java StockSimulator -web -algo SJF
```

### PRIORITY
```bash
java StockSimulator -web -algo PRIORITY
```

### ROUND ROBIN
```bash
java StockSimulator -web -algo ROUND_ROBIN
```

---

## 🎨 Color Codes

### Process States
| Badge | Color | Meaning |
|-------|-------|---------|
| NEW | Gray | Just created |
| READY | Green | Waiting for CPU |
| RUNNING | Bright Green | Currently executing |
| WAITING | Yellow | Blocked (I/O) |
| TERMINATED | Blue | Finished |

### Indicators
| Symbol | Meaning |
|--------|---------|
| ● Green (pulsing) | System Running |
| ● Red | System Offline |
| ✓ Green | No Thrashing |
| ⚠️ Red | Thrashing Detected |

---

## 📊 Understanding the Charts

### Scheduler Chart (Line)
- **Red line:** Average wait time (ms)
- **Blue line:** Average turnaround time (ms)
- Shows metrics over last 15 updates
- Lower is better

### Memory Chart (Doughnut)
- **Green portion:** Page hits (good)
- **Red portion:** Page faults (bad)
- Higher green ratio = better performance

### Stock Chart (Line)
- One line per stock (different colors)
- Shows last 20 price updates
- Multiple stocks tracked simultaneously

### IPC Chart (Bar)
- **Left bar:** Messages sent
- **Right bar:** Messages received
- Should be roughly equal

---

## 🔍 What Each Metric Means

### System Overview
- **Total Processes:** Number of trader processes created
- **Running:** Currently executing on CPU
- **Completed:** Finished and terminated
- **Context Switches:** Number of CPU switches between processes

### Scheduler
- **Algorithm:** Which scheduling algorithm is active
- **Avg Wait Time:** Average time processes wait in ready queue
- **Avg Turnaround:** Average total time from arrival to completion

### Memory
- **Page Faults:** Times a page wasn't in memory (cache miss)
- **Page Hits:** Times a page was in memory (cache hit)
- **Fault Rate:** Percentage of accesses that were faults
- **Thrashing:** High page faults causing system slowdown

### IPC
- **Messages Sent:** Orders submitted by traders
- **Messages Received:** Orders received by exchange
- **Shared Memory Ops:** Data read/written to shared memory
- **Semaphore Ops:** Synchronization operations

---

## 🎓 Learning Objectives

### Observe FCFS Algorithm
- First process to arrive gets CPU first
- No preemption (runs to completion)
- Compare wait times vs other algorithms
- Note context switch count

### Observe SJF Algorithm
- Process with shortest burst time goes first
- Lower average wait time
- Compare metrics to FCFS
- See improved efficiency

### Observe Priority Scheduling
- Higher priority processes run first
- Lower priority processes may starve
- Aging prevents indefinite postponement
- Compare fairness vs efficiency

### Observe Round Robin
- Each process gets time quantum (time slice)
- Fair distribution of CPU time
- Higher context switches
- Better response time

---

## ⚡ Real-Time Features

### Auto-Refresh
- Dashboard updates every 500ms automatically
- No manual refresh needed
- Smooth transitions between updates

### Live Metrics
- All numbers update in real-time
- Charts animate as data changes
- Tables refresh with new process data

### Connection Status
- Green pulsing indicator = Connected ✅
- Red indicator = Disconnected ❌
- Automatically detects connection loss

### Timestamp
- Shows last update time
- Helps verify updates are happening
- Formatted as HH:MM:SS

---

## 🔧 Troubleshooting

### Problem: "Cannot connect to localhost:8080"
**Solutions:**
1. Make sure Java process is running (terminal visible)
2. Wait 3-5 seconds after starting simulator
3. Check if another app is using port 8080
4. Try refreshing browser (F5)

### Problem: "Dashboard shows Offline"
**Solutions:**
1. Check Java process is still running
2. Check terminal for errors
3. Try: `netstat -an | findstr 8080`
4. Restart the simulator

### Problem: "Empty charts or tables"
**Solutions:**
1. Wait 2-3 seconds for data to load
2. Refresh browser (F5)
3. Check browser console (F12) for errors
4. Check Java console for error messages

### Problem: "Port 8080 already in use"
**Solutions:**
1. Find what's using the port: `netstat -an | findstr 8080`
2. Kill the process: `taskkill /PID [process-id]`
3. Or restart your computer
4. Or change port in code

---

## 📋 Checklist Before Use

- [ ] Java JDK 8+ installed
- [ ] Web browser (Chrome/Firefox/Safari/Edge)
- [ ] Port 8080 available
- [ ] Files in correct location
- [ ] Java files compiled (no errors)
- [ ] Terminal/Command Prompt open
- [ ] Simulator started with `-web` flag
- [ ] Browser navigated to http://localhost:8080

---

## 🎯 Next Steps

1. **Start Simulator** - Run the java command
2. **Open Dashboard** - Go to http://localhost:8080
3. **Observe Metrics** - See OS concepts in action
4. **Try Algorithms** - Run different scheduling algorithms
5. **Compare Results** - Note differences in performance
6. **Analyze Data** - Understand why metrics differ
7. **Experiment** - Try different configurations

---

## 🎉 You're Ready!

The dashboard is fully functional and error-free.

**Start the simulator and enjoy the visualization!** 🚀

---

## 📞 Command Reference

```bash
# Navigate to source
cd src

# Compile all files
javac *.java

# Run with web (default FCFS)
java StockSimulator -web

# Run with specific algorithm
java StockSimulator -web -algo FCFS
java StockSimulator -web -algo SJF
java StockSimulator -web -algo PRIORITY
java StockSimulator -web -algo ROUND_ROBIN

# Run with verbose output
java StockSimulator -web -v -algo FCFS

# Stop (in terminal)
Ctrl+C
```

---

**Happy Simulating! 📊✨**

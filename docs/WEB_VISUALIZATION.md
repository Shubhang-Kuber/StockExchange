# 🌐 Web Visualization Guide

## Quick Start - Running the Visualization

### Step 1: Compile the Code
Open a terminal in the `src` directory and compile all Java files:

```bash
cd src
javac *.java
```

### Step 2: Run with Web Visualization
Run the simulator with the `-web` flag to enable the web dashboard:

```bash
java StockSimulator -web
```

**Optional flags:**
- `-v` : Enable verbose console output
- `-web` : Enable web visualization dashboard
- `-v -web` : Enable both verbose output and web dashboard

### Step 3: Open the Dashboard
Once the program starts, you'll see:

```
╔════════════════════════════════════════════════════════╗
║   Web Dashboard Started Successfully!                  ║
╠════════════════════════════════════════════════════════╣
║   Open your browser and go to:                         ║
║                                                        ║
║   → http://localhost:8080                              ║
║                                                        ║
║   The dashboard will update automatically in real-time ║
╚════════════════════════════════════════════════════════╝
```

**Open your browser and navigate to:** http://localhost:8080

### Step 4: Watch the Visualization
The dashboard will automatically:
- ✅ Update every 500ms
- ✅ Show real-time stock prices
- ✅ Display process states and CPU scheduling
- ✅ Visualize memory management (page faults/hits)
- ✅ Track IPC statistics
- ✅ Show recent trade history

### Step 5: Stop the Server
Press **Ctrl+C** in the terminal to stop both the simulation and web server.

---

## 📊 Dashboard Features

### System Overview
- **Total Processes**: Number of trader processes
- **Running Processes**: Currently active traders
- **Completed Processes**: Finished traders
- **Total Context Switches**: CPU scheduler context switches

### CPU Scheduler
- Shows scheduling algorithm in use (FCFS, SJF, PRIORITY, ROUND_ROBIN)
- Average wait time and turnaround time
- Visual representation of completed vs pending tasks

### Virtual Memory
- **Page Faults**: Number of times requested page wasn't in memory
- **Page Hits**: Successful page accesses
- **Fault Rate**: Percentage of page faults
- **Thrashing Detection**: Warns if fault rate is too high
- Doughnut chart showing hit/fault ratio

### Stock Market Prices
- Real-time line chart showing price changes
- Tracks 5 stocks: AAPL, GOOGL, MSFT, AMZN, TSLA
- Historical trend over last 20 data points
- Each stock has a different color

### Process States & Statistics
- Complete table of all trader processes
- Shows:
  - Process ID and name
  - Current state (NEW, READY, RUNNING, WAITING, TERMINATED)
  - Priority level
  - Number of trades executed
  - Context switches
  - Wait time and CPU time
- Color-coded state badges

### IPC Statistics
- **Message Queue**: Messages sent/received between processes
- **Shared Memory**: Read/write operations on shared memory
- **Semaphore**: Acquire/release counts for synchronization

### Trade History
- Shows last 15 trades in real-time
- Color-coded: BUY orders in green, SELL orders in red
- Scrollable log with timestamps

---

## 🎯 What OS Concepts Are Visualized?

### 1. CPU Scheduling
- **What to Watch**: Process state transitions, context switches
- **In Dashboard**: Process table, scheduler stats
- **OS Concept**: See how different algorithms (FCFS, Priority, RR) affect performance

### 2. Virtual Memory Management
- **What to Watch**: Page faults vs hits ratio
- **In Dashboard**: Memory card with doughnut chart
- **OS Concept**: Paging, page replacement (LRU/FIFO), thrashing detection

### 3. Process Management
- **What to Watch**: Process states changing (READY → RUNNING → WAITING → TERMINATED)
- **In Dashboard**: Process table with color-coded states
- **OS Concept**: Process Control Blocks (PCB), process lifecycle

### 4. Inter-Process Communication
- **What to Watch**: Message queue activity, shared memory R/W
- **In Dashboard**: IPC statistics cards
- **OS Concept**: Message passing, shared memory, semaphores

### 5. Market Simulation (I/O Operations)
- **What to Watch**: Stock price changes, trading activity
- **In Dashboard**: Stock price chart, trade history
- **OS Concept**: I/O device simulation, event-driven processing

---

## 🔧 Troubleshooting

### Dashboard doesn't load
**Problem**: Browser shows "Can't connect to localhost:8080"

**Solutions**:
1. Make sure you ran with `-web` flag: `java StockSimulator -web`
2. Check if port 8080 is already in use
3. Look for "Web Dashboard Started Successfully!" message in terminal
4. Try refreshing the browser (Ctrl+F5)

### No data showing
**Problem**: Dashboard loads but shows "Waiting for data..."

**Solutions**:
1. Wait a few seconds - data files are generated as simulation runs
2. Check that `web/data/` directory exists
3. Verify simulation is actually running (check terminal output)

### Compilation errors
**Problem**: `javac *.java` fails

**Solutions**:
1. Make sure you're in the `src` directory
2. Check Java version: `java -version` (should be Java 8 or higher)
3. Ensure all `.java` files are present, including:
   - DataExporter.java
   - WebServer.java
   - (all other existing files)

### Port already in use
**Problem**: Error message says port 8080 is already in use

**Solutions**:
1. Stop any other programs using port 8080
2. Or modify `WebServer` in StockSimulator.java to use different port:
   ```java
   webServer = new WebServer(8081, "web");  // Use 8081 instead
   ```
   Then access at http://localhost:8081

---

## 💡 Tips for Best Experience

### For Presentations
1. Run with: `java StockSimulator -web` (without verbose)
2. Open browser in fullscreen (F11)
3. Point out specific OS concepts as they visualize
4. Use the process table to explain state transitions
5. Show how memory management responds to load

### For Development/Debugging
1. Run with: `java StockSimulator -v -web`
2. Keep terminal and browser side-by-side
3. Watch console logs and dashboard simultaneously
4. Verbose mode helps understand what's happening behind the scenes

### For Demonstrations
1. Start simulation first, then open browser
2. Dashboard works on any device on same network
3. Mobile-responsive design works on tablets/phones
4. Can open multiple browser windows to show different views

---

## 📱 Accessing from Other Devices

The dashboard can be accessed from other devices on the same network:

1. Find your computer's IP address:
   - **Windows**: `ipconfig` → Look for IPv4 Address
   - **Mac/Linux**: `ifconfig` → Look for inet address

2. On another device, open browser to:
   ```
   http://YOUR_IP_ADDRESS:8080
   ```
   Example: `http://192.168.1.100:8080`

3. Perfect for presenting on a second screen or tablet!

---

## 🎨 Dashboard Updates

The dashboard automatically refreshes every **500 milliseconds**, showing:
- ✅ Live stock price changes
- ✅ Real-time process state updates
- ✅ Current memory statistics
- ✅ Latest trade executions
- ✅ IPC activity counters

**No manual refresh needed!**

---

## 📂 File Structure

```
StockExchange/
├── src/
│   ├── StockSimulator.java      # Run this with -web flag
│   ├── StockExchange.java        # Modified to export data
│   ├── DataExporter.java         # NEW: Exports JSON data
│   ├── WebServer.java            # NEW: Serves web dashboard
│   └── (other Java files)
│
├── web/
│   ├── index.html                # Main dashboard page
│   ├── styles.css                # Dashboard styling
│   ├── dashboard.js              # Real-time data fetching
│   └── data/                     # Generated JSON files (gitignored)
│       ├── system_status.json
│       ├── processes.json
│       ├── memory.json
│       ├── stocks.json
│       ├── scheduler.json
│       ├── ipc.json
│       └── trade_history.txt
│
└── docs/
    └── WEB_VISUALIZATION.md      # This file
```

---

## 🚀 Commands Cheat Sheet

| Command | Description |
|---------|-------------|
| `cd src` | Navigate to source directory |
| `javac *.java` | Compile all Java files |
| `java StockSimulator` | Run simulator (console only) |
| `java StockSimulator -web` | Run with web dashboard |
| `java StockSimulator -v` | Run with verbose console output |
| `java StockSimulator -v -web` | Run with both verbose and web |
| **Ctrl+C** | Stop simulation and server |

---

## 📊 Browser Compatibility

Works best with modern browsers:
- ✅ Chrome/Edge (Recommended)
- ✅ Firefox
- ✅ Safari
- ⚠️ Internet Explorer not supported

---

## 🎓 Educational Value

This visualization helps understand:

1. **How CPU schedulers work** - Watch processes compete for CPU time
2. **Virtual memory in action** - See page faults and replacement policies
3. **Process lifecycle** - Observe state transitions in real-time
4. **IPC mechanisms** - Monitor communication between processes
5. **Performance metrics** - Compare different scheduling algorithms

Perfect for:
- ✅ Lab demonstrations
- ✅ Project presentations
- ✅ Understanding OS concepts visually
- ✅ Debugging and analysis
- ✅ Performance comparisons

---

## ❓ Need Help?

If you encounter issues:
1. Check terminal output for error messages
2. Verify all files compiled successfully
3. Ensure web/ directory structure is intact
4. Try closing and reopening the browser
5. Restart the simulation with `-web` flag

**Enjoy your real-time OS visualization!** 🎉

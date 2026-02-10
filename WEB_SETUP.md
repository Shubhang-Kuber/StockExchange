# 🌐 Stock Exchange OS Simulator - Web Dashboard Setup Guide

## ✅ Complete Integration Ready

Your Stock Exchange OS Simulator now includes a **real-time web dashboard** for live visualization of all OS concepts!

---

## 📋 What's Included

### Backend (Java API Server)
- **WebServerAPI.java** - REST API server with 7 endpoints
- Runs on `http://localhost:8080`
- Provides JSON data for all metrics

### Frontend (Web Dashboard)
- **index.html** - Dashboard UI with Chart.js visualizations
- **dashboard.js** - Real-time data fetching and chart updates
- **styles.css** - Modern dark-themed responsive design
- Updates every 500ms automatically

---

## 🚀 Quick Start

### Step 1: Compile Java Files
```bash
cd src/
javac *.java
```

### Step 2: Run Simulator with Web Mode
```bash
java StockSimulator -web -algo FCFS
```

### Step 3: Open Browser
Navigate to: **http://localhost:8080**

---

## 🎯 Features

### Dashboard Sections

1. **Algorithm Selection**
   - Choose from FCFS, SJF, PRIORITY, ROUND_ROBIN
   - Auto-updates run command

2. **System Overview**
   - Total/Running/Completed processes
   - Context switches count
   - Elapsed time and total trades

3. **CPU Scheduler Panel**
   - Current algorithm display
   - Average wait time (ms)
   - Average turnaround time (ms)
   - Line chart showing metrics over time

4. **Virtual Memory Panel**
   - Page faults and hits
   - Fault rate percentage
   - Thrashing detection
   - Doughnut chart visualization

5. **Stock Market Prices**
   - Real-time line chart
   - Multiple stocks plotted
   - 20-point history maintained

6. **Process States Table**
   - PID, State, Priority, Burst Time
   - Wait Time and Turnaround Time
   - Color-coded state badges

7. **IPC Statistics**
   - Message Queue (sent/received)
   - Shared Memory (reads/writes)
   - Semaphore (acquires/releases)
   - Bar chart for message counts

8. **Trade History**
   - Recent 20 trades displayed
   - Timestamp and trade details

---

## 📡 API Endpoints

All endpoints return JSON data:

### GET `/api/stats`
```json
{
  "status": "running",
  "schedulerAlgo": "FCFS",
  "elapsedTime": 45,
  "totalTrades": 234,
  "timestamp": 1707043200000
}
```

### GET `/api/stocks`
```json
[
  {"symbol": "AAPL", "name": "Apple", "price": 150.25},
  {"symbol": "GOOGL", "name": "Google", "price": 145.80}
]
```

### GET `/api/processes`
```json
[
  {
    "pid": 1,
    "state": "RUNNING",
    "priority": 5,
    "burstTime": 100,
    "waitTime": 250,
    "turnaroundTime": 350
  }
]
```

### GET `/api/scheduler`
```json
{
  "algorithm": "FCFS",
  "contextSwitches": 145,
  "avgWaitTime": 325.5,
  "avgTurnaroundTime": 475.3
}
```

### GET `/api/memory`
```json
{
  "pageFaults": 234,
  "pageHits": 1230,
  "faultRate": 16.02,
  "thrashing": false
}
```

### GET `/api/ipc`
```json
{
  "messagesSent": 567,
  "messagesReceived": 567,
  "sharedMemReads": 1204,
  "sharedMemWrites": 456,
  "semaphoreAcquires": 892,
  "semaphoreReleases": 891
}
```

### GET `/api/trades`
```json
[
  "2024-02-04 10:30:45 - AAPL: BUY 100 shares @ $150.25",
  "2024-02-04 10:31:12 - GOOGL: SELL 50 shares @ $145.80"
]
```

---

## 🎨 UI Features

### Real-Time Updates
- Dashboard auto-refreshes every 500ms
- Smooth chart animations
- Live metric counters

### Color Coding
- **Green (✓)** - Success/Running
- **Red (✗)** - Errors/Faults/Offline
- **Blue** - Info/Primary
- **Yellow** - Warning/Pending

### Responsive Design
- Works on desktop, tablet, mobile
- Dark theme optimized for long viewing
- Professional gradient backgrounds

### Chart Visualizations
- **Line Charts** - Scheduler metrics over time
- **Doughnut Chart** - Page hits vs faults ratio
- **Bar Charts** - Message counts
- Interactive legends and hover details

---

## 🔧 Troubleshooting

### "Connection Refused" Error
```
Problem: Can't connect to http://localhost:8080
Solution: 
1. Make sure java process is running
2. Check firewall isn't blocking port 8080
3. Try: netstat -an | findstr 8080 (Windows)
```

### "File Not Found" Error
```
Problem: 404 errors when loading page
Solution:
1. Ensure web/ folder exists in project root
2. Check index.html, styles.css, dashboard.js are present
3. Run from correct directory
```

### No Data Displayed
```
Problem: Dashboard shows "Offline" or empty tables
Solution:
1. Start simulator with: java StockSimulator -web -algo FCFS
2. Wait 2-3 seconds for data to populate
3. Refresh browser (F5)
4. Check browser console for errors (F12)
```

### Port Already in Use
```
Problem: Port 8080 is already in use
Solution:
1. Change port in WebServerAPI.java (line 30)
2. Recompile: javac WebServerAPI.java
3. Update browser URL accordingly
```

---

## 📊 Running with Different Algorithms

### FCFS (First-Come-First-Served)
```bash
java StockSimulator -web -algo FCFS
```

### SJF (Shortest Job First)
```bash
java StockSimulator -web -algo SJF
```

### Priority Scheduling
```bash
java StockSimulator -web -algo PRIORITY
```

### Round Robin
```bash
java StockSimulator -web -algo ROUND_ROBIN
```

---

## 🛠️ System Requirements

- **Java**: JDK 8 or higher
- **Browser**: Chrome, Firefox, Safari, Edge (ES6+ compatible)
- **Port**: 8080 (configurable)
- **Network**: localhost only (no external network needed)

---

## 📝 Tested Configurations

✅ Windows 10/11 + Java 11+ + Chrome
✅ Linux + Java 8+ + Firefox
✅ macOS + Java 11+ + Safari

---

## 💡 Tips & Tricks

1. **Monitor Thrashing**: Watch Virtual Memory panel for thrashing indicator
2. **Compare Algorithms**: Run different algorithms and note metrics
3. **Track Trades**: Use Trade History to verify order execution
4. **Process States**: Check color-coded badges in Process Table
5. **Real-Time Charts**: Charts smooth scroll as new data arrives

---

## 📧 Support

For issues or questions:
1. Check browser console (F12)
2. Check server logs in terminal
3. Verify all files are present
4. Ensure port 8080 is available
5. Try restarting the simulator

---

## ✨ Project Structure

```
StockExchange/
├── src/
│   ├── *.java (all source files)
│   └── WebServerAPI.java (REST API server)
├── web/
│   ├── index.html (dashboard UI)
│   ├── dashboard.js (real-time updates)
│   ├── styles.css (styling)
│   └── data/ (JSON data files)
├── README.md
├── INSTRUCTIONS_TO_RUN.md
├── ALGORITHM_SELECTION_GUIDE.md
└── VISUALIZATION_QUICKSTART.md
```

---

## 🎓 Learning Outcomes

By using this dashboard, you'll understand:

1. **CPU Scheduling**: Real-time comparison of algorithms
2. **Virtual Memory**: Page replacement and thrashing detection
3. **IPC**: Message queues, shared memory, semaphores in action
4. **Process Management**: State transitions and lifecycle
5. **System Performance**: Metrics-based algorithm comparison

---

## 🚀 Next Steps

1. ✅ Start the simulator with web mode
2. ✅ Open dashboard at http://localhost:8080
3. ✅ Select different scheduling algorithms
4. ✅ Observe metrics and make note
5. ✅ Compare performance across algorithms
6. ✅ Analyze the system documentation

Happy Simulating! 🎉

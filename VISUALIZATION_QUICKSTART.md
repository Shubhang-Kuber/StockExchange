# 🚀 Quick Start: Run the Web Visualization

## Simple 3-Step Guide

### 1️⃣ Compile
```bash
cd src
javac *.java
```

### 2️⃣ Run with Web Dashboard
```bash
java StockSimulator -web
```

### 3️⃣ Open Browser
Go to: **http://localhost:8080**

---

## What You'll See

Your browser will show a **real-time dashboard** with:

- 📊 **System Overview** - Process counts, context switches, elapsed time
- ⚙️ **CPU Scheduler** - Algorithm in use, wait times, task completion
- 💾 **Virtual Memory** - Page faults, hits, thrashing detection
- 📈 **Stock Prices** - Real-time price changes for 5 stocks
- 🔄 **Process States** - Live process state transitions and statistics
- 📨 **IPC Stats** - Message queue, shared memory, semaphore activity
- 📋 **Trade History** - Recent buy/sell orders in real-time

**Dashboard updates automatically every 500ms!**

---

## Command Options

| Command | What It Does |
|---------|-------------|
| `java StockSimulator` | Run simulation (console output only) |
| `java StockSimulator -web` | **Run with web visualization** ⭐ |
| `java StockSimulator -v` | Verbose console output |
| `java StockSimulator -v -web` | Verbose + web visualization |

---

## Stopping the Server

Press **Ctrl+C** in the terminal to stop both the simulation and web server.

---

## Troubleshooting

**Dashboard not loading?**
- Make sure you see "Web Dashboard Started Successfully!" in terminal
- Check you're accessing `http://localhost:8080` (not https)
- Wait a few seconds for data to generate
- Try refreshing browser (Ctrl+F5)

**Port already in use?**
- Another program is using port 8080
- Close other applications or change port in StockSimulator.java

**No data showing?**
- Wait a few seconds - simulation needs to start
- Check terminal for any error messages

---

## 📖 Full Documentation

For complete details, see:
- [Full Web Visualization Guide](docs/WEB_VISUALIZATION.md)
- [Project README](README.md)
- [Quick Reference](docs/QUICK_REFERENCE.md)

---

## 🎯 OS Concepts Visualized

- ✅ CPU Scheduling (FCFS, SJF, Priority, Round Robin)
- ✅ Virtual Memory Management (Paging, Page Replacement)
- ✅ Process Management (PCB, State Transitions)
- ✅ Inter-Process Communication (Message Queue, Shared Memory, Semaphores)
- ✅ Performance Metrics (Context Switches, Throughput, Wait Times)

**Perfect for lab demonstrations and project presentations!** 🎓

---

**Enjoy your real-time OS visualization!** 🎉

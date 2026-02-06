# 🎯 Algorithm Selection Guide

## ✅ New Feature: Choose Your Scheduling Algorithm!

You can now select which CPU scheduling algorithm to run from the web interface!

---

## 🚀 How to Use Algorithm Selection

### **Step 1: Recompile (First Time Only)**
```bash
cd "c:\Shubhang Kuber\Engineering 2024-2028\2nd Year All Docs\3rd Semester\OS\StockExchange\src"
javac *.java
```

### **Step 2: Choose Your Algorithm**

Run the simulator with your chosen algorithm:

#### **FCFS (First-Come-First-Served)**
```bash
java StockSimulator -web -algo FCFS
```

#### **SJF (Shortest Job First)**
```bash
java StockSimulator -web -algo SJF
```

#### **PRIORITY Scheduling**
```bash
java StockSimulator -web -algo PRIORITY
```

#### **ROUND_ROBIN (Time-Sliced)**
```bash
java StockSimulator -web -algo ROUND_ROBIN
```

### **Step 3: Open Browser**
Go to: **http://localhost:8080**

---

## 🎨 What You'll See

The web dashboard now has **Algorithm Selection Buttons** at the top:

```
┌─────────────────────────────────────────────┐
│ Choose CPU Scheduling Algorithm:           │
│                                             │
│  [FCFS]  [SJF]  [PRIORITY]  [ROUND ROBIN] │
│                                             │
│ ℹ️ Run: java StockSimulator -web -algo FCFS│
└─────────────────────────────────────────────┘
```

- **Active algorithm** is highlighted with a green glow
- Click any button to see the command to run
- The dashboard automatically detects which algorithm is running

---

## 🔄 Switching Algorithms

To change the algorithm:

1. **Stop** current simulation: Press **Ctrl+C** in terminal
2. **Click** the algorithm button you want in the browser
3. **Copy** the command shown below the buttons
4. **Run** the new command in terminal
5. **Refresh** browser (or it updates automatically)

---

## 📊 Examples

### Example 1: Run FCFS
```bash
# From root directory
cd "c:\Shubhang Kuber\Engineering 2024-2028\2nd Year All Docs\3rd Semester\OS\StockExchange"
java -cp src StockSimulator -web -algo FCFS
```

### Example 2: Run Round Robin
```bash
# From src directory
cd src
java StockSimulator -web -algo ROUND_ROBIN
```

### Example 3: Run with Verbose Output
```bash
java StockSimulator -web -algo PRIORITY -v
```

---

## 🎯 Algorithm Comparison

You can run different algorithms one at a time and compare:

| Algorithm | Best For | Characteristics |
|-----------|----------|-----------------|
| **FCFS** | Simple fairness | Non-preemptive, FIFO order |
| **SJF** | Minimize wait time | Shortest burst first |
| **PRIORITY** | Important tasks first | Aging prevents starvation |
| **ROUND_ROBIN** | Time-sharing | 100ms time quantum |

---

## 🎨 Visual Features

### Button States:
- **Default**: Gray border, hover for blue glow
- **Active**: Green border with glow (currently running algorithm)
- **Hover**: Lift effect with shadow

### Auto-Detection:
The dashboard automatically highlights the currently running algorithm by reading the JSON data.

---

## 💡 Tips

1. **Default Behavior**: Running `java StockSimulator -web` without `-algo` runs all 3 simulations
2. **Single Algorithm**: Add `-algo <NAME>` to run just one
3. **Page Replacement**: Add `-page FIFO` or `-page LRU` (default is LRU)
4. **Full Example**: `java StockSimulator -web -algo FCFS -page FIFO -v`

---

## 🔧 Advanced Options

```bash
# Run FCFS with FIFO page replacement
java StockSimulator -web -algo FCFS -page FIFO

# Run Priority with LRU and verbose output
java StockSimulator -web -algo PRIORITY -page LRU -v

# Run Round Robin with FIFO
java StockSimulator -web -algo ROUND_ROBIN -page FIFO
```

---

## 📖 Command Reference

```
java StockSimulator [options]

Options:
  -web              Enable web visualization
  -algo <ALGORITHM> Choose scheduling algorithm
                    Options: FCFS, SJF, PRIORITY, ROUND_ROBIN
  -page <POLICY>    Choose page replacement policy
                    Options: LRU, FIFO (default: LRU)
  -v                Enable verbose console output

Examples:
  java StockSimulator -web -algo FCFS
  java StockSimulator -web -algo PRIORITY -v
  java StockSimulator -web -algo ROUND_ROBIN -page FIFO
```

---

## 🎓 For Your Presentation

1. **Show the selector** - Point out the algorithm buttons
2. **Demonstrate switching** - Stop and restart with different algorithm
3. **Compare performance** - Run FCFS vs Round Robin and compare metrics
4. **Explain differences** - Use the dashboard to show how each works

---

## ✅ Summary

**What Changed:**
- ✅ Added algorithm selection buttons in web UI
- ✅ Added `-algo` command-line parameter
- ✅ Dashboard auto-detects and highlights active algorithm
- ✅ Shows command to run for each algorithm
- ✅ Professional button styling with hover effects

**How to Use:**
1. Recompile: `javac *.java`
2. Run: `java StockSimulator -web -algo FCFS`
3. Open: http://localhost:8080
4. Switch: Stop (Ctrl+C), run new algo, refresh

**Benefits:**
- Easy algorithm comparison
- Interactive selection
- Clear visual feedback
- Professional presentation tool

---

**Enjoy exploring different scheduling algorithms!** 🎉

/**
 * Stock Trading Simulator with Advanced OS Concepts
 * 
 * MAIN ENTRY POINT for the stock trading simulator that demonstrates graduate-level
 * Operating Systems concepts through a realistic market trading system.
 * 
 * This simulator demonstrates:
 * 1. Custom CPU Scheduling (FCFS, SJF, Priority, Round Robin)
 *    - Multiple scheduling algorithms with context switching
 *    - Priority aging to prevent starvation
 *    - Time quantum management for Round Robin
 * 
 * 2. Virtual Memory Management with Page Replacement (LRU/FIFO)
 *    - Demand paging with page fault handling
 *    - LRU and FIFO page replacement algorithms
 *    - Thrashing detection and analysis
 * 
 * 3. Inter-Process Communication (Message Queues, Shared Memory, Semaphores)
 *    - Message passing for order submission
 *    - Shared memory for market data distribution
 *    - Semaphores for resource coordination
 * 
 * 4. Transactional File System with Write-Ahead Logging
 *    - ACID transaction guarantees
 *    - Crash recovery with WAL
 *    - fsync durability simulation
 * 
 * 5. Process Control Blocks and State Management
 *    - Complete process lifecycle (NEW→READY→RUNNING→WAITING→TERMINATED)
 *    - Context switching with state preservation
 *    - Performance metrics tracking
 * 
 * 6. Performance Monitoring and Analysis
 *    - Throughput measurement
 *    - Context switch overhead analysis
 *    - Comparative performance across configurations
 * 
 * USAGE:
 *   javac *.java
 *   java StockSimulator
 * 
 * OUTPUT:
 *   - Console: Detailed simulation reports
 *   - File: logs/transactions.log (transaction history)
 * 
 * @author Suraj, Shubhang
 * @version 1.0
 * @see StockExchange
 * @see CustomScheduler
 * @see VirtualMemoryManager
 */
import java.io.File;

public class StockSimulator {
    // Global verbose flag - set to false for simplified output
    public static boolean VERBOSE = false;
    
    // Web server for visualization
    private static WebServer webServer = null;
    
    public static void main(String[] args) {
        // Check for flags
        boolean enableWeb = false;
        String selectedAlgo = null;
        String selectedPageReplacement = "LRU";
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-v")) {
                VERBOSE = true;
            } else if (args[i].equals("-web")) {
                enableWeb = true;
            } else if (args[i].equals("-algo") && i + 1 < args.length) {
                selectedAlgo = args[++i];
            } else if (args[i].equals("-page") && i + 1 < args.length) {
                selectedPageReplacement = args[++i];
            }
        }
        
        if (VERBOSE) {
            System.out.println(">>> VERBOSE MODE ENABLED <<<\n");
        }
        
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Stock Trading Simulator - OS Concepts Demo           ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        if (enableWeb) {
            // Start web server for visualization
            // Determine web directory path - check if index.html exists
            String webPath = "web";
            File webDir = new File(webPath);
            File indexFile = new File(webDir, "index.html");
            
            if (!indexFile.exists()) {
                // Try parent directory
                webPath = "../web";
                webDir = new File(webPath);
                indexFile = new File(webDir, "index.html");
                
                if (!indexFile.exists()) {
                    System.err.println("ERROR: Could not find web/index.html");
                    System.err.println("Tried: web/index.html and ../web/index.html");
                    System.exit(1);
                }
            }
            
            webServer = new WebServer(8080, webPath);
            webServer.start();
            System.out.println("Run with 'java StockSimulator -v -web' for verbose + web mode\n");
        } else {
            System.out.println("Run with 'java StockSimulator -web' to enable web visualization");
            if (!VERBOSE) {
                System.out.println("Run with 'java StockSimulator -v' for detailed verbose output");
            }
            System.out.println();
        }
        
        // Configuration
        int numTraders = 5;
        
        // If specific algorithm selected, run only that one
        if (selectedAlgo != null) {
            CustomScheduler.Algorithm algo = parseAlgorithm(selectedAlgo);
            VirtualMemoryManager.ReplacementPolicy pagePolicy = parsePageReplacement(selectedPageReplacement);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("SIMULATION: " + algo + " Scheduling + " + pagePolicy + " Page Replacement");
            System.out.println("=".repeat(60));
            runSimulation(algo, pagePolicy, numTraders);
            
            if (!enableWeb) {
                printComparisonSummary();
            }
            return;
        }
        
        // Otherwise run all three simulations (default behavior)
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SIMULATION 1: Priority Scheduling + LRU Page Replacement");
        System.out.println("=".repeat(60));
        runSimulation(CustomScheduler.Algorithm.PRIORITY, 
                     VirtualMemoryManager.ReplacementPolicy.LRU, 
                     numTraders);
        
        System.out.println("\n\n" + "=".repeat(60));
        System.out.println("SIMULATION 2: Round Robin Scheduling + FIFO Page Replacement");
        System.out.println("=".repeat(60));
        runSimulation(CustomScheduler.Algorithm.ROUND_ROBIN, 
                     VirtualMemoryManager.ReplacementPolicy.FIFO, 
                     numTraders);
        
        System.out.println("\n\n" + "=".repeat(60));
        System.out.println("SIMULATION 3: FCFS Scheduling + LRU Page Replacement");
        System.out.println("=".repeat(60));
        runSimulation(CustomScheduler.Algorithm.FCFS, 
                     VirtualMemoryManager.ReplacementPolicy.LRU, 
                     numTraders);
        
        // Print comparison summary
        printComparisonSummary();
    }
    
    private static CustomScheduler.Algorithm parseAlgorithm(String algo) {
        switch (algo.toUpperCase()) {
            case "FCFS": return CustomScheduler.Algorithm.FCFS;
            case "SJF": return CustomScheduler.Algorithm.SJF;
            case "PRIORITY": return CustomScheduler.Algorithm.PRIORITY;
            case "ROUND_ROBIN":
            case "RR": return CustomScheduler.Algorithm.ROUND_ROBIN;
            default: 
                System.out.println("Unknown algorithm: " + algo + ", using PRIORITY");
                return CustomScheduler.Algorithm.PRIORITY;
        }
    }
    
    private static VirtualMemoryManager.ReplacementPolicy parsePageReplacement(String policy) {
        switch (policy.toUpperCase()) {
            case "LRU": return VirtualMemoryManager.ReplacementPolicy.LRU;
            case "FIFO": return VirtualMemoryManager.ReplacementPolicy.FIFO;
            default: 
                System.out.println("Unknown page replacement: " + policy + ", using LRU");
                return VirtualMemoryManager.ReplacementPolicy.LRU;
        }
    }
    
    private static void runSimulation(CustomScheduler.Algorithm schedulingAlgo,
                                     VirtualMemoryManager.ReplacementPolicy pageReplacement,
                                     int numTraders) {
        try {
            StockExchange exchange = new StockExchange(schedulingAlgo, pageReplacement, numTraders);
            exchange.start();
            
            // Wait a bit for completion
            Thread.sleep(1000);
            
        } catch (Exception e) {
            System.err.println("Error in simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printComparisonSummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("COMPARISON SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ OS CONCEPTS DEMONSTRATED                                │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ ✓ CPU Scheduling Algorithms                             │");
        System.out.println("│   - First-Come-First-Served (FCFS)                      │");
        System.out.println("│   - Shortest Job First (SJF)                            │");
        System.out.println("│   - Priority Scheduling with Aging                      │");
        System.out.println("│   - Round Robin with Time Quantum                       │");
        System.out.println("│                                                         │");
        System.out.println("│ ✓ Memory Management                                     │");
        System.out.println("│   - Virtual Memory with Paging                          │");
        System.out.println("│   - Page Tables and Page Faults                         │");
        System.out.println("│   - LRU Page Replacement                                │");
        System.out.println("│   - FIFO Page Replacement                               │");
        System.out.println("│   - Thrashing Detection                                 │");
        System.out.println("│                                                         │");
        System.out.println("│ ✓ Inter-Process Communication                           │");
        System.out.println("│   - Message Queues (Order Submission)                   │");
        System.out.println("│   - Shared Memory (Market Data)                         │");
        System.out.println("│   - Semaphores (Resource Coordination)                  │");
        System.out.println("│                                                         │");
        System.out.println("│ ✓ File Systems                                          │");
        System.out.println("│   - Write-Ahead Logging (WAL)                           │");
        System.out.println("│   - Transactional File System                           │");
        System.out.println("│   - fsync and Durability                                │");
        System.out.println("│   - Crash Recovery                                      │");
        System.out.println("│                                                         │");
        System.out.println("│ ✓ Process Management                                    │");
        System.out.println("│   - Process Control Blocks (PCB)                        │");
        System.out.println("│   - Process State Transitions                           │");
        System.out.println("│   - Context Switching                                   │");
        System.out.println("│   - Performance Metrics                                 │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ PERFORMANCE METRICS TRACKED                             │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ • Throughput (transactions/second)                      │");
        System.out.println("│ • Context Switch Count and Overhead                     │");
        System.out.println("│ • Page Fault Rate and Hit Rate                          │");
        System.out.println("│ • IPC Message Latency                                   │");
        System.out.println("│ • Semaphore Wait Time                                   │");
        System.out.println("│ • File System Flush Count                               │");
        System.out.println("│ • Process Turnaround Time                               │");
        System.out.println("│ • Scheduler Efficiency                                  │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ IMPLEMENTATION HIGHLIGHTS                               │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│ • Thread-safe data structures                           │");
        System.out.println("│ • Realistic market simulation                           │");
        System.out.println("│ • Comprehensive error handling                          │");
        System.out.println("│ • Detailed performance analysis                         │");
        System.out.println("│ • Multiple configuration comparison                     │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Simulation Complete!");
        System.out.println("Check logs/transactions.log for transaction history");
        if (webServer != null) {
            System.out.println("\n>>> Web Dashboard is still running at http://localhost:8080");
            System.out.println(">>> Press Ctrl+C to stop the server and exit");
            
            // Keep server running
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                webServer.stop();
            }
        }
        System.out.println("=".repeat(60) + "\n");
    }
}

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * DataExporter - Exports simulation data to JSON for web visualization.
 * 
 * This class handles real-time data export for the web dashboard,
 * converting Java objects to JSON format without external dependencies.
 */
public class DataExporter {
    private String outputDir;
    private boolean enabled;
    
    public DataExporter(String outputDir) {
        this.outputDir = outputDir;
        this.enabled = true;
        initializeDirectory();
    }
    
    private void initializeDirectory() {
        try {
            Path path = Paths.get(outputDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not create data export directory: " + e.getMessage());
            enabled = false;
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Export system status data
     */
    public synchronized void exportSystemStatus(
            String schedulerAlgo,
            int totalProcesses,
            int runningProcesses,
            int completedProcesses,
            long elapsedTime,
            int totalTrades) {
        if (!enabled) return;
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": ").append(System.currentTimeMillis()).append(",\n");
            json.append("  \"schedulerAlgorithm\": \"").append(schedulerAlgo).append("\",\n");
            json.append("  \"totalProcesses\": ").append(totalProcesses).append(",\n");
            json.append("  \"runningProcesses\": ").append(runningProcesses).append(",\n");
            json.append("  \"completedProcesses\": ").append(completedProcesses).append(",\n");
            json.append("  \"elapsedTime\": ").append(elapsedTime).append(",\n");
            json.append("  \"totalTrades\": ").append(totalTrades).append("\n");
            json.append("}");
            
            writeFile("system_status.json", json.toString());
        } catch (Exception e) {
            // Silent fail - don't interrupt simulation
        }
    }
    
    /**
     * Export process statistics
     */
    public synchronized void exportProcessStats(List<Map<String, Object>> processes) {
        if (!enabled) return;
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": ").append(System.currentTimeMillis()).append(",\n");
            json.append("  \"processes\": [\n");
            
            for (int i = 0; i < processes.size(); i++) {
                Map<String, Object> p = processes.get(i);
                json.append("    {\n");
                json.append("      \"id\": ").append(p.get("id")).append(",\n");
                json.append("      \"name\": \"").append(p.get("name")).append("\",\n");
                json.append("      \"state\": \"").append(p.get("state")).append("\",\n");
                json.append("      \"priority\": ").append(p.get("priority")).append(",\n");
                json.append("      \"trades\": ").append(p.get("trades")).append(",\n");
                json.append("      \"contextSwitches\": ").append(p.get("contextSwitches")).append(",\n");
                json.append("      \"waitTime\": ").append(p.get("waitTime")).append(",\n");
                json.append("      \"cpuTime\": ").append(p.get("cpuTime")).append("\n");
                json.append("    }");
                if (i < processes.size() - 1) json.append(",");
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}");
            
            writeFile("processes.json", json.toString());
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    /**
     * Export memory statistics
     */
    public synchronized void exportMemoryStats(
            long pageFaults,
            long pageHits,
            double faultRate,
            boolean thrashing,
            int usedFrames,
            int totalFrames) {
        if (!enabled) return;
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": ").append(System.currentTimeMillis()).append(",\n");
            json.append("  \"pageFaults\": ").append(pageFaults).append(",\n");
            json.append("  \"pageHits\": ").append(pageHits).append(",\n");
            json.append("  \"faultRate\": ").append(String.format("%.2f", faultRate)).append(",\n");
            json.append("  \"thrashing\": ").append(thrashing).append(",\n");
            json.append("  \"usedFrames\": ").append(usedFrames).append(",\n");
            json.append("  \"totalFrames\": ").append(totalFrames).append(",\n");
            json.append("  \"utilizationPercent\": ").append(String.format("%.2f", (usedFrames * 100.0 / totalFrames))).append("\n");
            json.append("}");
            
            writeFile("memory.json", json.toString());
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    /**
     * Export stock prices
     */
    public synchronized void exportStockPrices(Map<String, Double> stockPrices) {
        if (!enabled) return;
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": ").append(System.currentTimeMillis()).append(",\n");
            json.append("  \"stocks\": [\n");
            
            int count = 0;
            for (Map.Entry<String, Double> entry : stockPrices.entrySet()) {
                json.append("    {\n");
                json.append("      \"symbol\": \"").append(entry.getKey()).append("\",\n");
                json.append("      \"price\": ").append(String.format("%.2f", entry.getValue())).append("\n");
                json.append("    }");
                if (++count < stockPrices.size()) json.append(",");
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}");
            
            writeFile("stocks.json", json.toString());
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    /**
     * Export scheduler statistics
     */
    public synchronized void exportSchedulerStats(
            String algorithm,
            int totalTasks,
            int completedTasks,
            int contextSwitches,
            double avgWaitTime,
            double avgTurnaroundTime) {
        if (!enabled) return;
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": ").append(System.currentTimeMillis()).append(",\n");
            json.append("  \"algorithm\": \"").append(algorithm).append("\",\n");
            json.append("  \"totalTasks\": ").append(totalTasks).append(",\n");
            json.append("  \"completedTasks\": ").append(completedTasks).append(",\n");
            json.append("  \"contextSwitches\": ").append(contextSwitches).append(",\n");
            json.append("  \"avgWaitTime\": ").append(String.format("%.2f", avgWaitTime)).append(",\n");
            json.append("  \"avgTurnaroundTime\": ").append(String.format("%.2f", avgTurnaroundTime)).append("\n");
            json.append("}");
            
            writeFile("scheduler.json", json.toString());
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    /**
     * Export IPC statistics
     */
    public synchronized void exportIPCStats(
            long messagesSent,
            long messagesReceived,
            long sharedMemoryReads,
            long sharedMemoryWrites,
            long semaphoreAcquires,
            long semaphoreReleases) {
        if (!enabled) return;
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"timestamp\": ").append(System.currentTimeMillis()).append(",\n");
            json.append("  \"messageQueue\": {\n");
            json.append("    \"sent\": ").append(messagesSent).append(",\n");
            json.append("    \"received\": ").append(messagesReceived).append("\n");
            json.append("  },\n");
            json.append("  \"sharedMemory\": {\n");
            json.append("    \"reads\": ").append(sharedMemoryReads).append(",\n");
            json.append("    \"writes\": ").append(sharedMemoryWrites).append("\n");
            json.append("  },\n");
            json.append("  \"semaphore\": {\n");
            json.append("    \"acquires\": ").append(semaphoreAcquires).append(",\n");
            json.append("    \"releases\": ").append(semaphoreReleases).append("\n");
            json.append("  }\n");
            json.append("}");
            
            writeFile("ipc.json", json.toString());
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    /**
     * Append trade to history log
     */
    public synchronized void logTrade(String traderName, String action, String symbol, int quantity, double price) {
        if (!enabled) return;
        
        try {
            String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
            String logEntry = String.format("%s | %s | %s | %s | %d @ %.2f\n", 
                timestamp, traderName, action, symbol, quantity, price);
            
            Path file = Paths.get(outputDir, "trade_history.txt");
            Files.write(file, logEntry.getBytes(), 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            // Silent fail
        }
    }
    
    private void writeFile(String filename, String content) {
        try {
            Path file = Paths.get(outputDir, filename);
            Files.write(file, content.getBytes(), 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            // Silent fail
        }
    }
    
    /**
     * Clear all data files (call at simulation start)
     */
    public void clearData() {
        if (!enabled) return;
        
        try {
            String[] files = {"system_status.json", "processes.json", "memory.json", 
                            "stocks.json", "scheduler.json", "ipc.json", "trade_history.txt"};
            for (String file : files) {
                Path path = Paths.get(outputDir, file);
                Files.deleteIfExists(path);
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}

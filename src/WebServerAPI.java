import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Web Server API for Stock Exchange OS Simulator visualization.
 * 
 * Provides REST API endpoints and serves static HTML/CSS/JS files.
 * Runs on localhost:8080 when simulator is started with -web flag.
 * 
 * ENDPOINTS:
 * - GET /api/stats - System statistics
 * - GET /api/stocks - Stock prices
 * - GET /api/processes - Process information
 * - GET /api/scheduler - Scheduler metrics
 * - GET /api/memory - Memory statistics
 * - GET /api/ipc - IPC statistics
 * - GET /api/trades - Recent trades
 * - GET / - Serve index.html
 * 
 * @author Shubhang Kuber
 * @version 1.0
 */
public class WebServerAPI {
    private HttpServer server;
    private static final int PORT = 8080;
    private static final String WEB_ROOT = "web";
    
    public WebServerAPI(StockSimulator simulator) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        setupRoutes(simulator);
    }
    
    private void setupRoutes(StockSimulator simulator) {
        // API Endpoints
        server.createContext("/api/stats", exchange -> {
            try {
                handleStatsRequest(exchange, simulator);
            } catch (Exception e) {
                sendError(exchange, e);
            }
        });
        
        server.createContext("/api/stocks", exchange -> {
            try {
                handleStocksRequest(exchange, simulator);
            } catch (Exception e) {
                sendError(exchange, e);
            }
        });
        
        server.createContext("/api/processes", exchange -> {
            try {
                handleProcessesRequest(exchange, simulator);
            } catch (Exception e) {
                sendError(exchange, e);
            }
        });
        
        server.createContext("/api/scheduler", exchange -> {
            try {
                handleSchedulerRequest(exchange, simulator);
            } catch (Exception e) {
                sendError(exchange, e);
            }
        });
        
        server.createContext("/api/memory", exchange -> {
            try {
                handleMemoryRequest(exchange, simulator);
            } catch (Exception e) {
                sendError(exchange, e);
            }
        });
        
        server.createContext("/api/ipc", exchange -> {
            try {
                handleIPCRequest(exchange, simulator);
            } catch (Exception e) {
                sendError(exchange, e);
            }
        });
        
        server.createContext("/api/trades", exchange -> {
            try {
                handleTradesRequest(exchange, simulator);
            } catch (Exception e) {
                sendError(exchange, e);
            }
        });
        
        // Static Files
        server.createContext("/", exchange -> {
            try {
                handleStaticRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, e);
            }
        });
    }
    
    private void handleStatsRequest(HttpExchange exchange, StockSimulator simulator) throws IOException {
        StringBuilder json = new StringBuilder("{");
        json.append("\"status\":\"running\",");
        json.append("\"timestamp\":").append(System.currentTimeMillis()).append(",");
        json.append("\"schedulerAlgo\":\"").append(simulator.getSchedulerAlgorithm()).append("\",");
        json.append("\"elapsedTime\":").append(simulator.getElapsedTimeSeconds()).append(",");
        json.append("\"totalTrades\":").append(simulator.getTotalTrades()).append(",");
        
        // Add missing fields for system overview
        List<ProcessControlBlock> processes = simulator.getAllProcesses();
        int totalProcesses = processes.size();
        int runningProcesses = 0;
        int completedProcesses = 0;
        for (ProcessControlBlock pcb : processes) {
            if (pcb.getState() == ProcessControlBlock.State.RUNNING) runningProcesses++;
            if (pcb.getState() == ProcessControlBlock.State.TERMINATED) completedProcesses++;
        }
        
        json.append("\"totalProcesses\":").append(totalProcesses).append(",");
        json.append("\"runningProcesses\":").append(runningProcesses).append(",");
        json.append("\"completedProcesses\":").append(completedProcesses).append(",");
        json.append("\"contextSwitches\":").append(simulator.getTotalContextSwitches());
        json.append("}");
        
        sendResponse(exchange, json.toString(), 200);
    }
    
    private void handleStocksRequest(HttpExchange exchange, StockSimulator simulator) throws IOException {
        List<Stock> stocks = simulator.getStocks();
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            if (i > 0) json.append(",");
            json.append("{");
            json.append("\"symbol\":\"").append(stock.getSymbol()).append("\",");
            json.append("\"name\":\"").append(stock.getName()).append("\",");
            json.append("\"price\":").append(String.format("%.2f", stock.getCurrentPrice()));
            json.append("}");
        }
        
        json.append("]");
        sendResponse(exchange, json.toString(), 200);
    }
    
    private void handleProcessesRequest(HttpExchange exchange, StockSimulator simulator) throws IOException {
        List<ProcessControlBlock> processes = simulator.getAllProcesses();
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < processes.size(); i++) {
            ProcessControlBlock pcb = processes.get(i);
            if (i > 0) json.append(",");
            json.append("{");
            json.append("\"pid\":").append(pcb.getProcessId()).append(",");
            json.append("\"state\":\"").append(pcb.getState()).append("\",");
            json.append("\"priority\":").append(pcb.getPriority()).append(",");
            json.append("\"burstTime\":").append(pcb.getBurstTime()).append(",");
            json.append("\"waitTime\":").append(pcb.getWaitingTime()).append(",");
            json.append("\"turnaroundTime\":").append(pcb.getTurnaroundTime());
            json.append("}");
        }
        
        json.append("]");
        sendResponse(exchange, json.toString(), 200);
    }
    
    private void handleSchedulerRequest(HttpExchange exchange, StockSimulator simulator) throws IOException {
        StringBuilder json = new StringBuilder("{");
        json.append("\"algorithm\":\"").append(simulator.getSchedulerAlgorithm()).append("\",");
        json.append("\"contextSwitches\":").append(simulator.getTotalContextSwitches()).append(",");
        json.append("\"avgWaitTime\":").append(String.format("%.2f", simulator.getAverageWaitTime())).append(",");
        json.append("\"avgTurnaroundTime\":").append(String.format("%.2f", simulator.getAverageTurnaroundTime()));
        json.append("}");
        
        sendResponse(exchange, json.toString(), 200);
    }
    
    private void handleMemoryRequest(HttpExchange exchange, StockSimulator simulator) throws IOException {
        StringBuilder json = new StringBuilder("{");
        json.append("\"pageFaults\":").append(simulator.getPageFaults()).append(",");
        json.append("\"pageHits\":").append(simulator.getPageHits()).append(",");
        json.append("\"faultRate\":").append(String.format("%.2f", simulator.getFaultRate())).append(",");
        json.append("\"thrashing\":").append(simulator.isThrashing());
        json.append("}");
        
        sendResponse(exchange, json.toString(), 200);
    }
    
    private void handleIPCRequest(HttpExchange exchange, StockSimulator simulator) throws IOException {
        StringBuilder json = new StringBuilder("{");
        json.append("\"messagesSent\":").append(simulator.getMessagesSent()).append(",");
        json.append("\"messagesReceived\":").append(simulator.getMessagesReceived()).append(",");
        json.append("\"sharedMemReads\":").append(simulator.getSharedMemReads()).append(",");
        json.append("\"sharedMemWrites\":").append(simulator.getSharedMemWrites()).append(",");
        json.append("\"semaphoreAcquires\":").append(simulator.getSemaphoreAcquires()).append(",");
        json.append("\"semaphoreReleases\":").append(simulator.getSemaphoreReleases());
        json.append("}");
        
        sendResponse(exchange, json.toString(), 200);
    }
    
    private void handleTradesRequest(HttpExchange exchange, StockSimulator simulator) throws IOException {
        List<String> trades = simulator.getRecentTrades(20);
        StringBuilder json = new StringBuilder("[");
        
        for (int i = 0; i < trades.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(trades.get(i)).append("\"");
        }
        
        json.append("]");
        sendResponse(exchange, json.toString(), 200);
    }
    
    private void handleStaticRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/") || path.isEmpty()) {
            path = "/index.html";
        }
        
        File file = new File(WEB_ROOT + path);
        
        if (file.exists() && file.isFile()) {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String contentType = getContentType(file.getName());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            sendResponse(exchange, new String(bytes), 200);
        } else {
            sendResponse(exchange, "{\"error\":\"File not found\"}", 404);
        }
    }
    
    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        // Only set Content-Type if not already set (for static files)
        if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        }
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
    
    private void sendError(HttpExchange exchange, Exception e) {
        try {
            String errorJson = "{\"error\":\"" + e.getMessage() + "\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            byte[] bytes = errorJson.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html; charset=utf-8";
        if (fileName.endsWith(".css")) return "text/css; charset=utf-8";
        if (fileName.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (fileName.endsWith(".json")) return "application/json; charset=utf-8";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }
    
    public void start() {
        server.setExecutor(null);
        server.start();
        System.out.println("✓ Web Server started on http://localhost:8080");
    }
    
    public void stop() {
        server.stop(0);
        System.out.println("✓ Web Server stopped");
    }
}

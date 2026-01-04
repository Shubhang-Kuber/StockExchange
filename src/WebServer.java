import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * WebServer - Simple HTTP server to serve the visualization dashboard.
 * 
 * Serves static files (HTML, CSS, JS, JSON) from the web/ directory.
 * Runs on http://localhost:8080
 */
public class WebServer {
    private HttpServer server;
    private int port;
    private String webRoot;
    
    public WebServer(int port, String webRoot) {
        this.port = port;
        this.webRoot = webRoot;
    }
    
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Handle all requests
            server.createContext("/", new StaticFileHandler(webRoot));
            
            server.setExecutor(null); // Use default executor
            server.start();
            
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║   Web Dashboard Started Successfully!                  ║");
            System.out.println("╠════════════════════════════════════════════════════════╣");
            System.out.println("║   Open your browser and go to:                         ║");
            System.out.println("║                                                        ║");
            System.out.println("║   → http://localhost:" + port + "                              ║");
            System.out.println("║                                                        ║");
            System.out.println("║   The dashboard will update automatically in real-time ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println();
        } catch (IOException e) {
            System.err.println("Failed to start web server: " + e.getMessage());
        }
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("\nWeb server stopped.");
        }
    }
    
    /**
     * Handler for serving static files
     */
    static class StaticFileHandler implements HttpHandler {
        private String webRoot;
        
        public StaticFileHandler(String webRoot) {
            this.webRoot = webRoot;
        }
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Default to index.html for root path
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            File file = new File(webRoot + path);
            
            if (!file.exists() || file.isDirectory()) {
                // 404 Not Found
                String response = "404 - File Not Found";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            
            // Determine content type
            String contentType = getContentType(path);
            
            // Enable CORS for data files
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", contentType);
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            
            // Read and send file
            byte[] fileContent = Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, fileContent.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileContent);
            os.close();
        }
        
        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (path.endsWith(".json")) return "application/json; charset=UTF-8";
            if (path.endsWith(".txt")) return "text/plain; charset=UTF-8";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            if (path.endsWith(".svg")) return "image/svg+xml";
            return "application/octet-stream";
        }
    }
}

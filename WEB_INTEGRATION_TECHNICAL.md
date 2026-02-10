# 🔗 Web Integration - Technical Documentation

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Web Browser                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   HTML UI    │  │  JavaScript  │  │     CSS      │       │
│  │  (index.html)│  │(dashboard.js)│  │(styles.css)  │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
│          │                  │                  │              │
│          └──────────────────┼──────────────────┘              │
│                             │                                 │
│                    HTTP GET Requests                          │
│                    (every 500ms)                              │
│                             │                                 │
└─────────────────────────────┼─────────────────────────────────┘
                              │
                    ┌─────────▼────────┐
                    │   localhost:8080 │
                    │   WebServerAPI   │
                    └─────────┬────────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
      ┌─────▼───────┐ ┌──────▼──────┐ ┌───────▼──────┐
      │   /api/     │ │  /api/      │ │  /api/       │
      │   stats     │ │  stocks     │ │  processes   │
      └─────────────┘ └─────────────┘ └──────────────┘
      
      ┌─────────────┐ ┌─────────────┐ ┌──────────────┐
      │  /api/      │ │ /api/       │ │ /api/        │
      │  scheduler  │ │ memory      │ │ ipc          │
      └─────────────┘ └─────────────┘ └──────────────┘
            │                 │                 │
            └─────────────────┼─────────────────┘
                              │
                    ┌─────────▼────────┐
                    │ StockSimulator   │
                    │  (Live Data)     │
                    └──────────────────┘
```

---

## File Structure

### Backend Implementation

#### **WebServerAPI.java** (NEW)
- HTTP Server running on localhost:8080
- 7 REST API endpoints returning JSON
- Serves static files (HTML, CSS, JS)
- Thread-safe concurrent request handling
- CORS headers for cross-origin access

**Key Methods:**
```java
handleStatsRequest()      // System metrics
handleStocksRequest()     // Stock prices
handleProcessesRequest()  // Process info
handleSchedulerRequest()  // Scheduler metrics
handleMemoryRequest()     // Virtual memory stats
handleIPCRequest()        // IPC statistics
handleTradesRequest()     // Trade history
handleStaticRequest()     // Static file serving
```

### Frontend Implementation

#### **index.html** (UPDATED)
- Responsive dashboard layout
- Algorithm selection buttons
- 8 main information panels
- Chart.js integration
- Real-time status indicators

**Components:**
```html
- Header with algorithm selector
- System Overview metrics
- CPU Scheduler panel
- Virtual Memory panel
- Stock Market chart
- Process States table
- IPC Statistics panels
- Trade History table
- Footer with update timestamp
```

#### **dashboard.js** (UPDATED)
- Real-time data fetching via fetch API
- Chart.js library integration
- Parallel data loading (Promise.all)
- Dynamic chart updates
- Error handling with connection status

**Key Functions:**
```javascript
initializeCharts()         // Setup all 4 charts
startUpdates()            // Begin update loop
updateDashboard()         // Main update function
updateSystemOverview()    // Metrics update
updateSchedulerChart()    // Dynamic chart update
updateStockPrices()       // Stock data visualization
updateProcessTable()      // Process state rendering
setConnectionStatus()     // Connection indicator
```

#### **styles.css** (ENHANCED)
- Dark theme optimized for readability
- CSS Grid for responsive layout
- Badge styling for state indicators
- Table styling for data display
- Animation effects

**New Styles:**
```css
.data-table              // Data table styling
.badge                   // State badges
.badge.running/.waiting  // State-specific colors
.status-running         // Animation
.status-offline         // Connection status
.value.success/.danger  // Metric coloring
```

### Data Flow

1. **Initialization (Page Load)**
   ```
   Browser loads index.html
      ↓
   Parses HTML, loads CSS and JS
      ↓
   JavaScript DOMContentLoaded event
      ↓
   initializeUI() → initializeCharts()
      ↓
   startUpdates() launches update loop
   ```

2. **Update Cycle (Every 500ms)**
   ```
   Dashboard.js calls updateDashboard()
      ↓
   Parallel fetch() to 7 API endpoints
      ↓
   JSON responses received
      ↓
   Parse and validate data
      ↓
   Update UI elements
      ↓
   Update chart data
      ↓
   Charts animate with new data
      ↓
   Status indicator updates
      ↓
   Timestamp updates
      ↓
   Wait 500ms, repeat
   ```

---

## API Endpoints Specification

### 1. `/api/stats`
**Purpose:** System overview metrics
**Response:**
```json
{
  "status": "running",
  "schedulerAlgo": "FCFS",
  "elapsedTime": 45,
  "totalTrades": 234,
  "timestamp": 1707043200000
}
```

### 2. `/api/stocks`
**Purpose:** Current stock prices
**Response:**
```json
[
  {"symbol": "AAPL", "name": "Apple Inc.", "price": 150.25},
  {"symbol": "GOOGL", "name": "Alphabet Inc.", "price": 145.80},
  {"symbol": "MSFT", "name": "Microsoft", "price": 380.50},
  {"symbol": "AMZN", "name": "Amazon", "price": 175.30},
  {"symbol": "TSLA", "name": "Tesla", "price": 245.60}
]
```

### 3. `/api/processes`
**Purpose:** Process information
**Response:**
```json
[
  {
    "pid": 1,
    "state": "RUNNING",
    "priority": 5,
    "burstTime": 100,
    "waitTime": 250,
    "turnaroundTime": 350
  },
  {
    "pid": 2,
    "state": "READY",
    "priority": 3,
    "burstTime": 0,
    "waitTime": 500,
    "turnaroundTime": 0
  }
]
```

### 4. `/api/scheduler`
**Purpose:** Scheduler metrics
**Response:**
```json
{
  "algorithm": "FCFS",
  "contextSwitches": 145,
  "avgWaitTime": 325.5,
  "avgTurnaroundTime": 475.3
}
```

### 5. `/api/memory`
**Purpose:** Virtual memory statistics
**Response:**
```json
{
  "pageFaults": 234,
  "pageHits": 1230,
  "faultRate": 16.02,
  "thrashing": false
}
```

### 6. `/api/ipc`
**Purpose:** Inter-process communication stats
**Response:**
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

### 7. `/api/trades`
**Purpose:** Recent trade history
**Response:**
```json
[
  "2024-02-04 10:30:45 - AAPL: BUY 100 @ $150.25",
  "2024-02-04 10:31:12 - GOOGL: SELL 50 @ $145.80",
  "2024-02-04 10:32:33 - MSFT: BUY 75 @ $380.50"
]
```

---

## Data Types and Validation

### Stock Data
```javascript
{
  symbol: String (3-4 chars),
  name: String,
  price: Number (positive, 2 decimals)
}
```

### Process Data
```javascript
{
  pid: Number (positive integer),
  state: String (NEW|READY|RUNNING|WAITING|TERMINATED),
  priority: Number (1-10),
  burstTime: Number (non-negative),
  waitTime: Number (non-negative),
  turnaroundTime: Number (non-negative)
}
```

### Scheduler Data
```javascript
{
  algorithm: String (FCFS|SJF|PRIORITY|ROUND_ROBIN),
  contextSwitches: Number (non-negative),
  avgWaitTime: Number (non-negative, 2 decimals),
  avgTurnaroundTime: Number (non-negative, 2 decimals)
}
```

---

## Error Handling

### HTTP Errors
```javascript
catch (error) {
    console.error('❌ Dashboard update failed:', error);
    setConnectionStatus(false);
}
```

### Missing Data
```javascript
if (!stocks || stocks.length === 0) return;
// Gracefully skip update if data unavailable
```

### Invalid Chart Context
```javascript
if (!charts.scheduler) return;
// Prevent error if chart not initialized
```

---

## Performance Considerations

### Update Frequency
- **500ms interval:** Balances responsiveness with server load
- **Parallel requests:** All 7 API calls run simultaneously
- **No blocking:** UI remains responsive during updates

### Data Caching
- Charts maintain 20-point history
- Only newest data added, oldest removed
- Prevents memory bloat

### Chart Optimization
- `update('none')` skips animation for performance
- Only visible datasets updated
- Efficient DOM manipulation

---

## Browser Compatibility

| Browser | Version | Status |
|---------|---------|--------|
| Chrome  | 60+     | ✅ Full Support |
| Firefox | 55+     | ✅ Full Support |
| Safari  | 11+     | ✅ Full Support |
| Edge    | 79+     | ✅ Full Support |
| IE      | 11      | ❌ Not Supported |

**Requirements:**
- ES6+ JavaScript support
- Fetch API
- CSS Grid
- Chart.js 4.0+

---

## Security Considerations

### Input Validation
- All JSON data validated before use
- HTML entities escaped in trade history
- API responses assumed trusted (local only)

### CORS Settings
```javascript
"Access-Control-Allow-Origin": "*"
"Access-Control-Allow-Methods": "GET, OPTIONS"
```
⚠️ Note: Only for localhost, safe for local development

### Network Isolation
- Runs on localhost:8080 only
- No external network access required
- No authentication needed (local development)

---

## Testing Checklist

- [ ] Simulator starts with `-web` flag
- [ ] Web server listens on port 8080
- [ ] Browser connects to http://localhost:8080
- [ ] Dashboard loads within 2 seconds
- [ ] All 8 API endpoints return valid JSON
- [ ] Charts appear and update
- [ ] Connection status shows "Running"
- [ ] Process table populates
- [ ] Metrics update every 500ms
- [ ] No console errors
- [ ] Responsive on different screen sizes
- [ ] Algorithm buttons highlight correctly
- [ ] Trade history displays properly
- [ ] Thrashing indicator works

---

## Deployment Notes

### Local Development
- ✅ Ready to use as-is
- ✅ No external dependencies
- ✅ No database required
- ✅ Cross-platform compatible

### Future Enhancement Possibilities
- Add WebSocket for real-time bidirectional communication
- Implement data export (CSV/PDF)
- Add algorithm comparison side-by-side
- Record and replay simulations
- Add performance benchmarking graphs

---

## Conclusion

The web integration provides a **professional, real-time visualization** of the Stock Exchange OS Simulator, making all OS concepts immediately observable and educational.

✨ **Status**: COMPLETE AND READY FOR USE ✨

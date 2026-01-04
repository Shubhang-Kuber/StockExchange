// Real-Time Stock Exchange OS Dashboard
// Fetches data every 500ms and updates all visualizations

let charts = {};
let stockPriceHistory = {};
let memoryHistory = {
    timestamps: [],
    faults: [],
    hits: []
};
let currentAlgorithm = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initializeCharts();
    initializeAlgorithmSelector();
    startDataFetching();
});

// Initialize algorithm selector buttons
function initializeAlgorithmSelector() {
    const buttons = document.querySelectorAll('.algo-btn');
    
    buttons.forEach(button => {
        button.addEventListener('click', () => {
            const algo = button.dataset.algo;
            selectAlgorithm(algo);
        });
    });
}

// Handle algorithm selection
function selectAlgorithm(algo) {
    // Update UI - highlight selected button
    document.querySelectorAll('.algo-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.closest('.algo-btn').classList.add('active');
    
    // Update instruction text
    const runCommand = document.getElementById('runCommand');
    const isWindows = navigator.platform.indexOf('Win') > -1;
    const cd = isWindows ? 'cd' : 'cd';
    
    runCommand.innerHTML = `
        Stop current simulation (Ctrl+C) and run: 
        <code>java StockSimulator -web -algo ${algo}</code>
    `;
}

// Initialize all Chart.js charts
function initializeCharts() {
    // Scheduler Chart (Context Switches over time)
    const schedulerCtx = document.getElementById('schedulerChart').getContext('2d');
    charts.scheduler = new Chart(schedulerCtx, {
        type: 'bar',
        data: {
            labels: ['Completed', 'Pending'],
            datasets: [{
                label: 'Tasks',
                data: [0, 0],
                backgroundColor: ['#10b981', '#f59e0b']
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: { beginAtZero: true, grid: { color: '#334155' }, ticks: { color: '#94a3b8' } },
                x: { grid: { display: false }, ticks: { color: '#94a3b8' } }
            }
        }
    });

    // Memory Chart (Page Faults vs Hits)
    const memoryCtx = document.getElementById('memoryChart').getContext('2d');
    charts.memory = new Chart(memoryCtx, {
        type: 'doughnut',
        data: {
            labels: ['Page Hits', 'Page Faults'],
            datasets: [{
                data: [100, 0],
                backgroundColor: ['#10b981', '#ef4444']
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { 
                    position: 'bottom',
                    labels: { color: '#f1f5f9' }
                }
            }
        }
    });

    // Stock Chart (Real-time prices)
    const stockCtx = document.getElementById('stockChart').getContext('2d');
    charts.stock = new Chart(stockCtx, {
        type: 'line',
        data: {
            labels: [],
            datasets: []
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: { 
                    position: 'top',
                    labels: { color: '#f1f5f9' }
                }
            },
            scales: {
                y: { 
                    grid: { color: '#334155' },
                    ticks: { color: '#94a3b8' }
                },
                x: { 
                    grid: { display: false },
                    ticks: { color: '#94a3b8', maxRotation: 0 }
                }
            }
        }
    });

    // IPC Message Chart
    const ipcCtx = document.getElementById('ipcMsgChart').getContext('2d');
    charts.ipc = new Chart(ipcCtx, {
        type: 'bar',
        data: {
            labels: ['Sent', 'Received'],
            datasets: [{
                label: 'Messages',
                data: [0, 0],
                backgroundColor: ['#3b82f6', '#10b981']
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: { beginAtZero: true, grid: { color: '#334155' }, ticks: { color: '#94a3b8' } },
                x: { grid: { display: false }, ticks: { color: '#94a3b8' } }
            }
        }
    });
}

// Start fetching data at regular intervals
function startDataFetching() {
    fetchAllData();
    setInterval(fetchAllData, 500); // Update every 500ms
}

// Fetch all data from JSON files
async function fetchAllData() {
    try {
        await Promise.all([
            fetchSystemStatus(),
            fetchProcesses(),
            fetchMemory(),
            fetchStocks(),
            fetchScheduler(),
            fetchIPC(),
            fetchTradeHistory()
        ]);
        
        updateLastUpdateTime();
    } catch (error) {
        console.error('Error fetching data:', error);
    }
}

// Fetch system status
async function fetchSystemStatus() {
    try {
        const response = await fetch('data/system_status.json?' + Date.now());
        if (!response.ok) return;
        
        const data = await response.json();
        
        document.getElementById('schedulerAlgo').textContent = data.schedulerAlgorithm;
        document.getElementById('elapsedTime').textContent = Math.floor(data.elapsedTime / 1000) + 's';
        document.getElementById('totalTrades').textContent = data.totalTrades;
        document.getElementById('totalProcesses').textContent = data.totalProcesses;
        document.getElementById('runningProcesses').textContent = data.runningProcesses;
        document.getElementById('completedProcesses').textContent = data.completedProcesses;
        
        // Highlight the active algorithm button
        if (currentAlgorithm !== data.schedulerAlgorithm) {
            currentAlgorithm = data.schedulerAlgorithm;
            highlightActiveAlgorithm(data.schedulerAlgorithm);
        }
        
        // Update status indicator
        const statusEl = document.getElementById('systemStatus');
        if (data.completedProcesses === data.totalProcesses && data.totalProcesses > 0) {
            statusEl.textContent = '● Completed';
            statusEl.style.color = '#94a3b8';
        } else {
            statusEl.textContent = '● Running';
            statusEl.style.color = '#10b981';
        }
    } catch (error) {
        // Data not available yet
    }
}

// Highlight the active algorithm button
function highlightActiveAlgorithm(algo) {
    document.querySelectorAll('.algo-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.dataset.algo === algo) {
            btn.classList.add('active');
        }
    });
}

// Fetch process statistics
async function fetchProcesses() {
    try {
        const response = await fetch('data/processes.json?' + Date.now());
        if (!response.ok) return;
        
        const data = await response.json();
        
        const tableHTML = `
            <table>
                <thead>
                    <tr>
                        <th>Process ID</th>
                        <th>Name</th>
                        <th>State</th>
                        <th>Priority</th>
                        <th>Trades</th>
                        <th>Context Switches</th>
                        <th>Wait Time (ms)</th>
                        <th>CPU Time (ms)</th>
                    </tr>
                </thead>
                <tbody>
                    ${data.processes.map(p => `
                        <tr>
                            <td>${p.id}</td>
                            <td>${p.name}</td>
                            <td><span class="state-badge state-${p.state}">${p.state}</span></td>
                            <td>${p.priority}</td>
                            <td>${p.trades}</td>
                            <td>${p.contextSwitches}</td>
                            <td>${p.waitTime}</td>
                            <td>${p.cpuTime}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        
        document.getElementById('processTable').innerHTML = tableHTML;
        
        // Update context switches total
        const totalCS = data.processes.reduce((sum, p) => sum + p.contextSwitches, 0);
        document.getElementById('contextSwitches').textContent = totalCS;
    } catch (error) {
        // Data not available yet
    }
}

// Fetch memory statistics
async function fetchMemory() {
    try {
        const response = await fetch('data/memory.json?' + Date.now());
        if (!response.ok) return;
        
        const data = await response.json();
        
        document.getElementById('pageFaults').textContent = data.pageFaults;
        document.getElementById('pageHits').textContent = data.pageHits;
        document.getElementById('faultRate').textContent = data.faultRate + '%';
        
        const thrashingEl = document.getElementById('thrashing');
        thrashingEl.textContent = data.thrashing ? 'YES' : 'NO';
        thrashingEl.style.color = data.thrashing ? '#ef4444' : '#10b981';
        
        // Update memory chart
        charts.memory.data.datasets[0].data = [data.pageHits, data.pageFaults];
        charts.memory.update('none');
    } catch (error) {
        // Data not available yet
    }
}

// Fetch stock prices
async function fetchStocks() {
    try {
        const response = await fetch('data/stocks.json?' + Date.now());
        if (!response.ok) return;
        
        const data = await response.json();
        const timestamp = new Date(data.timestamp).toLocaleTimeString();
        
        // Initialize stock history if needed
        data.stocks.forEach(stock => {
            if (!stockPriceHistory[stock.symbol]) {
                stockPriceHistory[stock.symbol] = {
                    timestamps: [],
                    prices: []
                };
            }
            
            const history = stockPriceHistory[stock.symbol];
            history.timestamps.push(timestamp);
            history.prices.push(stock.price);
            
            // Keep last 20 data points
            if (history.timestamps.length > 20) {
                history.timestamps.shift();
                history.prices.shift();
            }
        });
        
        // Update chart
        updateStockChart();
    } catch (error) {
        // Data not available yet
    }
}

// Update stock chart with historical data
function updateStockChart() {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];
    const symbols = Object.keys(stockPriceHistory);
    
    if (symbols.length === 0) return;
    
    // Use timestamps from first stock (all should be same)
    const timestamps = stockPriceHistory[symbols[0]].timestamps;
    
    charts.stock.data.labels = timestamps;
    charts.stock.data.datasets = symbols.map((symbol, index) => ({
        label: symbol,
        data: stockPriceHistory[symbol].prices,
        borderColor: colors[index % colors.length],
        backgroundColor: colors[index % colors.length] + '20',
        tension: 0.4,
        borderWidth: 2,
        pointRadius: 0,
        pointHoverRadius: 4
    }));
    
    charts.stock.update('none');
}

// Fetch scheduler statistics
async function fetchScheduler() {
    try {
        const response = await fetch('data/scheduler.json?' + Date.now());
        if (!response.ok) return;
        
        const data = await response.json();
        
        document.getElementById('schedulerAlgoDetail').textContent = data.algorithm;
        document.getElementById('avgWaitTime').textContent = data.avgWaitTime.toFixed(2) + ' ms';
        document.getElementById('avgTurnaround').textContent = data.avgTurnaroundTime.toFixed(2) + ' ms';
        
        // Update scheduler chart
        charts.scheduler.data.datasets[0].data = [
            data.completedTasks,
            data.totalTasks - data.completedTasks
        ];
        charts.scheduler.update('none');
    } catch (error) {
        // Data not available yet
    }
}

// Fetch IPC statistics
async function fetchIPC() {
    try {
        const response = await fetch('data/ipc.json?' + Date.now());
        if (!response.ok) return;
        
        const data = await response.json();
        
        document.getElementById('messagesSent').textContent = data.messageQueue.sent;
        document.getElementById('messagesReceived').textContent = data.messageQueue.received;
        document.getElementById('sharedMemReads').textContent = data.sharedMemory.reads;
        document.getElementById('sharedMemWrites').textContent = data.sharedMemory.writes;
        document.getElementById('semaphoreAcquires').textContent = data.semaphore.acquires;
        document.getElementById('semaphoreReleases').textContent = data.semaphore.releases;
        
        // Update IPC chart
        charts.ipc.data.datasets[0].data = [
            data.messageQueue.sent,
            data.messageQueue.received
        ];
        charts.ipc.update('none');
    } catch (error) {
        // Data not available yet
    }
}

// Fetch trade history
async function fetchTradeHistory() {
    try {
        const response = await fetch('data/trade_history.txt?' + Date.now());
        if (!response.ok) return;
        
        const text = await response.text();
        const lines = text.trim().split('\n');
        
        // Show last 15 trades
        const recentTrades = lines.slice(-15).reverse();
        
        const historyHTML = recentTrades.map(line => {
            const actionClass = line.includes('BUY') ? 'trade-BUY' : 
                              line.includes('SELL') ? 'trade-SELL' : '';
            return `<div class="trade-entry ${actionClass}">${escapeHtml(line)}</div>`;
        }).join('');
        
        document.getElementById('tradeHistory').innerHTML = historyHTML || 
            '<div class="loading">No trades yet...</div>';
    } catch (error) {
        document.getElementById('tradeHistory').innerHTML = 
            '<div class="loading">Waiting for trade data...</div>';
    }
}

// Update last update timestamp
function updateLastUpdateTime() {
    const now = new Date();
    document.getElementById('lastUpdate').textContent = 
        'Last updated: ' + now.toLocaleTimeString();
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Handle errors gracefully
window.addEventListener('error', (e) => {
    console.error('Dashboard error:', e.error);
});

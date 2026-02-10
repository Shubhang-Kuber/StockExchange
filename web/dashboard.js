/**
 * Stock Exchange OS Simulator - Real-Time Dashboard
 * 
 * Connects to WebServerAPI endpoints and updates live charts/metrics
 * Updates every 500ms with real-time system data
 */

const API_URL = 'http://localhost:8080/api';
const UPDATE_INTERVAL = 500;
let isConnected = false;
let charts = {};

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initializeUI();
    initializeCharts();
    startUpdates();
    console.log('✓ Dashboard initialized');
});

// Initialize UI elements
function initializeUI() {
    // Algorithm button handlers
    document.querySelectorAll('.algo-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const algo = this.getAttribute('data-algo');
            updateRunCommand(algo);
            // Highlight active button
            document.querySelectorAll('.algo-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
        });
    });
    
    // Set initial command
    updateRunCommand('FCFS');
}

function updateRunCommand(algo) {
    const cmd = `java StockSimulator -web -algo ${algo}`;
    const runCmdEl = document.getElementById('runCommand');
    runCmdEl.innerHTML = `Stop current simulation (Ctrl+C) and run: <code>${cmd}</code>`;
}

// Initialize Charts
function initializeCharts() {
    // Scheduler Performance Chart
    const schedulerCtx = document.getElementById('schedulerChart');
    if (schedulerCtx) {
        charts.scheduler = new Chart(schedulerCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    {
                        label: 'Avg Wait Time (ms)',
                        data: [],
                        borderColor: '#ef4444',
                        backgroundColor: 'rgba(239, 68, 68, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 3,
                        pointHoverRadius: 5
                    },
                    {
                        label: 'Avg Turnaround (ms)',
                        data: [],
                        borderColor: '#3b82f6',
                        backgroundColor: 'rgba(59, 130, 246, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointRadius: 3,
                        pointHoverRadius: 5
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: { position: 'top' },
                    filler: { propagate: true }
                },
                scales: {
                    y: { 
                        beginAtZero: true,
                        max: 1000
                    }
                }
            }
        });
    }

    // Memory Chart - Page Faults vs Hits
    const memoryCtx = document.getElementById('memoryChart');
    if (memoryCtx) {
        charts.memory = new Chart(memoryCtx, {
            type: 'doughnut',
            data: {
                labels: ['Page Hits', 'Page Faults'],
                datasets: [{
                    data: [0, 0],
                    backgroundColor: ['#10b981', '#ef4444'],
                    borderColor: '#1e293b',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { position: 'bottom' } }
            }
        });
    }

    // Stock Prices Chart
    const stockCtx = document.getElementById('stockChart');
    if (stockCtx) {
        charts.stock = new Chart(stockCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: []
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: { legend: { position: 'top' } },
                scales: {
                    y: { 
                        beginAtZero: false,
                        title: { display: true, text: 'Price ($)' }
                    }
                }
            }
        });
    }

    // IPC Message Chart
    const ipcCtx = document.getElementById('ipcMsgChart');
    if (ipcCtx) {
        charts.ipc = new Chart(ipcCtx, {
            type: 'bar',
            data: {
                labels: ['Sent', 'Received'],
                datasets: [{
                    label: 'Messages',
                    data: [0, 0],
                    backgroundColor: ['#f59e0b', '#10b981'],
                    borderColor: '#1e293b',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { position: 'top' } },
                scales: { y: { beginAtZero: true } }
            }
        });
    }
}

// Start periodic updates
function startUpdates() {
    updateDashboard();
    setInterval(updateDashboard, UPDATE_INTERVAL);
}

// Main update function
async function updateDashboard() {
    try {
        // Fetch all data in parallel
        const [stats, stocks, processes, scheduler, memory, ipc, trades] = await Promise.all([
            fetch(`${API_URL}/stats`).then(r => r.json()),
            fetch(`${API_URL}/stocks`).then(r => r.json()),
            fetch(`${API_URL}/processes`).then(r => r.json()),
            fetch(`${API_URL}/scheduler`).then(r => r.json()),
            fetch(`${API_URL}/memory`).then(r => r.json()),
            fetch(`${API_URL}/ipc`).then(r => r.json()),
            fetch(`${API_URL}/trades`).then(r => r.json())
        ]);

        // Update all UI sections
        updateSystemOverview(stats);
        updateSchedulerInfo(scheduler);
        updateMemoryStats(memory);
        updateStockPrices(stocks);
        updateProcessTable(processes);
        updateIPCStats(ipc);
        updateTradeHistory(trades);
        updateCharts(scheduler, memory, stocks, ipc);
        updateLastUpdate();
        setConnectionStatus(true);

    } catch (error) {
        console.error('❌ Dashboard update failed:', error);
        setConnectionStatus(false);
    }
}

// Update System Overview metrics
function updateSystemOverview(stats) {
    document.getElementById('schedulerAlgo').textContent = stats.schedulerAlgo || '--';
    document.getElementById('elapsedTime').textContent = (stats.elapsedTime || 0) + 's';
    document.getElementById('totalTrades').textContent = stats.totalTrades || 0;
    document.getElementById('totalProcesses').textContent = stats.totalProcesses || 0;
    document.getElementById('runningProcesses').textContent = stats.runningProcesses || 0;
    document.getElementById('completedProcesses').textContent = stats.completedProcesses || 0;
    document.getElementById('contextSwitches').textContent = stats.contextSwitches || 0;
}

// Update Scheduler Info
function updateSchedulerInfo(scheduler) {
    document.getElementById('schedulerAlgoDetail').textContent = scheduler.algorithm || '--';
    document.getElementById('avgWaitTime').textContent = 
        scheduler.avgWaitTime ? scheduler.avgWaitTime.toFixed(2) + ' ms' : '--';
    document.getElementById('avgTurnaround').textContent = 
        scheduler.avgTurnaroundTime ? scheduler.avgTurnaroundTime.toFixed(2) + ' ms' : '--';
}

// Update Memory Stats
function updateMemoryStats(memory) {
    document.getElementById('pageFaults').textContent = memory.pageFaults || 0;
    document.getElementById('pageHits').textContent = memory.pageHits || 0;
    document.getElementById('faultRate').textContent = 
        memory.faultRate ? memory.faultRate.toFixed(2) + '%' : '0%';
    
    const thrashingEl = document.getElementById('thrashing');
    thrashingEl.textContent = memory.thrashing ? 'YES ⚠️' : 'NO ✓';
    thrashingEl.className = memory.thrashing ? 'value danger' : 'value success';
}

// Update Stock Prices
function updateStockPrices(stocks) {
    if (!stocks || stocks.length === 0) return;
    
    const now = new Date().toLocaleTimeString();
    
    if (charts.stock.data.labels.length >= 20) {
        charts.stock.data.labels.shift();
    }
    charts.stock.data.labels.push(now);

    if (charts.stock.data.datasets.length === 0) {
        const colors = ['#ef4444', '#3b82f6', '#10b981', '#f59e0b'];
        stocks.forEach((stock, idx) => {
            charts.stock.data.datasets.push({
                label: stock.symbol,
                data: [],
                borderColor: colors[idx % colors.length],
                backgroundColor: colors[idx % colors.length] + '20',
                tension: 0.3,
                fill: true,
                pointRadius: 2
            });
        });
    }

    stocks.forEach((stock, idx) => {
        if (charts.stock.data.datasets[idx]) {
            if (charts.stock.data.datasets[idx].data.length >= 20) {
                charts.stock.data.datasets[idx].data.shift();
            }
            charts.stock.data.datasets[idx].data.push(stock.price);
        }
    });

    charts.stock.update('none');
}

// Update Process Table
function updateProcessTable(processes) {
    const table = document.getElementById('processTable');
    
    if (!processes || processes.length === 0) {
        table.innerHTML = '<p class="no-data">No processes</p>';
        return;
    }

    let html = `<table class="data-table">
        <thead>
            <tr>
                <th>PID</th>
                <th>State</th>
                <th>Priority</th>
                <th>Burst (ms)</th>
                <th>Wait (ms)</th>
                <th>Turnaround (ms)</th>
            </tr>
        </thead>
        <tbody>`;

    processes.forEach(proc => {
        const stateClass = getStateClass(proc.state);
        html += `<tr>
            <td>${proc.pid}</td>
            <td><span class="badge ${stateClass}">${proc.state}</span></td>
            <td>${proc.priority}</td>
            <td>${proc.burstTime}</td>
            <td>${proc.waitTime}</td>
            <td>${proc.turnaroundTime}</td>
        </tr>`;
    });

    html += '</tbody></table>';
    table.innerHTML = html;
}

// Update IPC Statistics
function updateIPCStats(ipc) {
    document.getElementById('messagesSent').textContent = ipc.messagesSent || 0;
    document.getElementById('messagesReceived').textContent = ipc.messagesReceived || 0;
    document.getElementById('sharedMemReads').textContent = ipc.sharedMemReads || 0;
    document.getElementById('sharedMemWrites').textContent = ipc.sharedMemWrites || 0;
    document.getElementById('semaphoreAcquires').textContent = ipc.semaphoreAcquires || 0;
    document.getElementById('semaphoreReleases').textContent = ipc.semaphoreReleases || 0;
}

// Update Trade History
function updateTradeHistory(trades) {
    const history = document.getElementById('tradeHistory');
    
    if (!trades || trades.length === 0) {
        history.innerHTML = '<p class="no-data">No trades yet</p>';
        return;
    }

    let html = `<table class="data-table">
        <thead>
            <tr>
                <th>Trade Details</th>
            </tr>
        </thead>
        <tbody>`;

    trades.forEach(trade => {
        html += `<tr><td>${escapeHtml(trade)}</td></tr>`;
    });

    html += '</tbody></table>';
    history.innerHTML = html;
}

// Update all charts
function updateCharts(scheduler, memory, stocks, ipc) {
    updateSchedulerChart(scheduler);
    updateMemoryChart(memory);
    updateIPCChart(ipc);
}

function updateSchedulerChart(scheduler) {
    if (!charts.scheduler) return;
    
    const now = new Date().toLocaleTimeString();
    
    if (charts.scheduler.data.labels.length >= 15) {
        charts.scheduler.data.labels.shift();
        charts.scheduler.data.datasets[0].data.shift();
        charts.scheduler.data.datasets[1].data.shift();
    }

    charts.scheduler.data.labels.push(now);
    charts.scheduler.data.datasets[0].data.push(scheduler.avgWaitTime || 0);
    charts.scheduler.data.datasets[1].data.push(scheduler.avgTurnaroundTime || 0);
    charts.scheduler.update('none');
}

function updateMemoryChart(memory) {
    if (!charts.memory) return;
    charts.memory.data.datasets[0].data = [memory.pageHits || 0, memory.pageFaults || 0];
    charts.memory.update('none');
}

function updateIPCChart(ipc) {
    if (!charts.ipc) return;
    charts.ipc.data.datasets[0].data = [ipc.messagesSent || 0, ipc.messagesReceived || 0];
    charts.ipc.update('none');
}

// Utility functions
function setConnectionStatus(connected) {
    isConnected = connected;
    const status = document.getElementById('systemStatus');
    if (connected) {
        status.textContent = '● Running';
        status.className = 'value status-running';
    } else {
        status.textContent = '● Offline';
        status.className = 'value status-offline';
    }
}

function updateLastUpdate() {
    document.getElementById('lastUpdate').textContent = new Date().toLocaleTimeString();
}

function getStateClass(state) {
    const map = {
        'NEW': 'new',
        'READY': 'ready',
        'RUNNING': 'running',
        'WAITING': 'waiting',
        'TERMINATED': 'completed'
    };
    return map[state] || 'default';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

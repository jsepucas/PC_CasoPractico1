let stompClient = null;

// ========================
// Conexión WebSocket (STOMP)
// ========================
function connectWebSocket() {
    const socket = new SockJS('/ws/alerts');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        console.log('Conectado:', frame);

        // Suscribirse a los datos de sensores
        stompClient.subscribe('/topic/data', (msg) => {
            const data = JSON.parse(msg.body);
            updateSensorChart(data.type, data.value);
            addEventToTable(data);
        });

        // Suscribirse a las alertas críticas
        stompClient.subscribe('/topic/alerts', (alert) => {
            const message = JSON.parse(alert.body);
            showAlert(message.content);
        });
    });
}

// ========================
// Tablas y alertas
// ========================
function addEventToTable(data) {
    const tbody = document.querySelector("#eventTable tbody");
    const row = document.createElement("tr");
    const date = new Date(data.timestamp).toLocaleTimeString();

    row.innerHTML = `
        <td>${date}</td>
        <td>${data.type}</td>
        <td>${data.value.toFixed(2)}</td>
        <td>${data.critical ? '⚠️' : '—'}</td>
    `;
    tbody.prepend(row);

    // Limitar la tabla a 15 filas
    if (tbody.rows.length > 15) tbody.deleteRow(15);
}

function showAlert(content) {
    const alertFeed = document.getElementById('alertFeed');
    const div = document.createElement('div');
    div.classList.add('alert');
    div.textContent = content;
    alertFeed.prepend(div);

    setTimeout(() => div.remove(), 7000);
}

// ========================
// Gráficas con Chart.js
// ========================
const charts = {
    movimiento: new Chart(document.getElementById('chart-movimiento'), createChartConfig('Sensor de Movimiento')),
    temperatura: new Chart(document.getElementById('chart-temperatura'), createChartConfig('Sensor de Temperatura')),
    acceso: new Chart(document.getElementById('chart-acceso'), createChartConfig('Sensor de Acceso'))
};

function createChartConfig(label) {
    return {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label,
                data: [],
                borderWidth: 2,
                borderColor: '#58a6ff',
                backgroundColor: 'rgba(88,166,255,0.1)',
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            scales: {
                x: { display: false },
                y: {
                    beginAtZero: true,
                    title: { display: true, text: 'Valor' },
                    ticks: { color: '#c9d1d9' }
                }
            },
            plugins: {
                legend: { labels: { color: '#c9d1d9' } }
            }
        }
    };
}

function updateSensorChart(type, value) {
    const chart = charts[type];
    if (!chart) return;

    const labels = chart.data.labels;
    labels.push(labels.length + 1);
    chart.data.datasets[0].data.push(value);

    if (labels.length > 20) {
        labels.shift();
        chart.data.datasets[0].data.shift();
    }

    chart.update();
}

// ========================
// Inicialización
// ========================
document.addEventListener("DOMContentLoaded", () => {
    connectWebSocket();

    // Mostrar el rol actual desde el backend (Spring Security)
    fetch('/user')
        .then(res => res.json())
        .then(user => {
            document.getElementById("userRole").textContent = `Usuario: ${user.username} (${user.role})`;
        })
        .catch(() => {
            document.getElementById("userRole").textContent = "Usuario: Desconocido";
        });
});

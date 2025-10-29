let paused = false;
let stompClient = null;
let isStompConnected = false;

// Estructuras para almacenar datos de los sensores
const sensorData = {
  movimiento: [],
  temperatura: [],
  acceso: []
};

// Variables para almacenar los contadores
let totalAlerts = 0;
let movimientoAlerts = 0;
let temperaturaAlerts = 0;
let accesoAlerts = 0;
let averageTemperature = 0;
// Últimos valores mostrados (aseguran monotonía en la UI)
let lastMovimientoShown = 0;
let lastTemperaturaShown = 0;
let lastAccesoShown = 0;
// Variables para calcular temperatura media en frontend (simulación)
let tempSum = 0;
let tempCount = 0;

// Crear las gráficas
const charts = {
  movimiento: createChart('chartMovimiento', 'Sensor de Movimiento', '#4ea1ff'),
  temperatura: createChart('chartTemperatura', 'Sensor de Temperatura', '#ffb14e'),
  acceso: createChart('chartAcceso', 'Sensor de Acceso', '#ff4e4e')
};

// Crear un gráfico de tipo línea con Chart.js
function createChart(id, label, color) {
  const ctx = document.getElementById(id).getContext('2d');
  return new Chart(ctx, {
    type: 'line',
    data: {
      labels: [],
      datasets: [{
        label,
        borderColor: color,
        backgroundColor: color + "33",
        borderWidth: 2,
        pointRadius: 3,
        data: []
      }]
    },
    options: {
      animation: false,
      scales: {
        y: { beginAtZero: true }
      },
      plugins: { legend: { labels: { color: "#fff" } } }
    }
  });
}

// Actualiza los gráficos
function updateCharts() {
  Object.keys(charts).forEach(sensor => {
    charts[sensor].data.labels = sensorData[sensor].map((_, i) => i + 1);
    charts[sensor].data.datasets[0].data = sensorData[sensor];
    charts[sensor].update();
  });
}

// Añadimos una función que actualiza el dataset de un sensor concreto
function updateSensorChart(type, value) {
  if (!type || value === undefined || value === null) return;
  const key = type.toString().trim().toLowerCase();
  if (!sensorData.hasOwnProperty(key)) return;

  sensorData[key].push(Number(value));
  if (sensorData[key].length > 20) sensorData[key].shift();
  updateCharts();
}

// Función para actualizar los contadores de alertas
function updateCounters(data) {
    // Si el backend no envía el contador, lo ignoramos.
    if (data.movimientoAlerts !== undefined && data.movimientoAlerts !== null) {
        const incoming = Number(data.movimientoAlerts);
        if (!isNaN(incoming)) {
            // Guardamos y mostramos el mayor entre lo que ya mostramos y lo entrante
            lastMovimientoShown = Math.max(lastMovimientoShown, incoming);
            document.getElementById('movimiento-count').textContent = lastMovimientoShown;
        }
    }

    if (data.temperaturaAlerts !== undefined && data.temperaturaAlerts !== null) {
        const incoming = Number(data.temperaturaAlerts);
        if (!isNaN(incoming)) {
            lastTemperaturaShown = Math.max(lastTemperaturaShown, incoming);
            document.getElementById('temperatura-count').textContent = lastTemperaturaShown;
        }
    }

    if (data.accesoAlerts !== undefined && data.accesoAlerts !== null) {
        const incoming = Number(data.accesoAlerts);
        if (!isNaN(incoming)) {
            lastAccesoShown = Math.max(lastAccesoShown, incoming);
            document.getElementById('acceso-count').textContent = lastAccesoShown;
        }
    }

    // Temperatura media: la media puede subir o bajar, la mostramos tal cual si viene
    if (data.averageTemperature !== undefined && data.averageTemperature !== null) {
        const avg = Number(data.averageTemperature);
        if (!isNaN(avg)) {
            document.getElementById('average-temp').textContent = avg.toFixed(2) + "°C";
        }
    }
}

// Conexión WebSocket para recibir datos en tiempo real
function connectWebSocket() {
    const socket = new SockJS('/ws/alerts');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        console.log('Conectado:', frame);
        isStompConnected = true;

        stompClient.subscribe('/topic/data', (msg) => {
            const payload = JSON.parse(msg.body);

            // DEBUG: mostrar payload recibido
            console.debug('WS /topic/data payload:', payload);

            // Actualizar gráfico con el tipo y valor
            updateSensorChart(payload.type, payload.value);

            // Construir un evento compatible con addEventRow
            const hora = payload.timestamp ? new Date(payload.timestamp).toLocaleTimeString() : new Date().toLocaleTimeString();
            const evento = {
                hora,
                sensor: payload.type || 'desconocido',
                valor: (payload.value !== undefined && payload.value !== null) ? Number(payload.value).toFixed(2) : '—',
                critico: payload.critical ? '⚠️' : '—'
            };

            addEventRow(evento);  // Añadir eventos a la tabla

            // Primero aplicar cualquier contador absoluto que venga del backend
            updateCounters({
                movimientoAlerts: payload.movimientoAlerts,
                temperaturaAlerts: payload.temperaturaAlerts,
                accesoAlerts: payload.accesoAlerts,
                averageTemperature: payload.averageTemperature
            });

            // Si el backend no envía contadores absolutos (o lo hace de forma opcional),
            // incrementamos localmente el contador del sensor cuando es crítico para evitar saltos grandes.
            if (payload.critical) {
                const key = (payload.type || '').toString().trim().toLowerCase();
                // Si no vino el contador absoluto para este tipo, hacemos incremento local
                const absProvided = (key === 'movimiento' && payload.movimientoAlerts !== undefined)
                    || (key === 'temperatura' && payload.temperaturaAlerts !== undefined)
                    || (key === 'acceso' && payload.accesoAlerts !== undefined);

                if (!absProvided) {
                    switch (key) {
                        case 'movimiento':
                            lastMovimientoShown = Math.max(lastMovimientoShown + 1, lastMovimientoShown + 1);
                            document.getElementById('movimiento-count').textContent = lastMovimientoShown;
                            break;
                        case 'temperatura':
                            lastTemperaturaShown = Math.max(lastTemperaturaShown + 1, lastTemperaturaShown + 1);
                            document.getElementById('temperatura-count').textContent = lastTemperaturaShown;
                            break;
                        case 'acceso':
                            lastAccesoShown = Math.max(lastAccesoShown + 1, lastAccesoShown + 1);
                            document.getElementById('acceso-count').textContent = lastAccesoShown;
                            break;
                    }
                }
            }
        });

        stompClient.subscribe('/topic/alerts', (alert) => {
            const message = JSON.parse(alert.body);
            showAlert(message.content);  // Mostrar alerta en el panel
        });
    }, (err) => {
        console.error('STOMP connect error', err);
        isStompConnected = false;
    });
}

// Mostrar alerta en tiempo real
function showAlert(message) {
    const container = document.getElementById("alert-container");
    const alert = document.createElement("div");
    alert.className = "alert";
    alert.textContent = message;
    container.prepend(alert);
    setTimeout(() => alert.remove(), 5000);
}

// Añadir evento a la tabla de eventos recientes
function addEventRow({ hora, sensor, valor, critico }) {
    const tabla = document.getElementById("tabla-eventos");
    const fila = document.createElement("tr");
    fila.innerHTML = `<td>${hora}</td><td>${sensor}</td><td>${valor}</td><td>${critico}</td>`;
    tabla.prepend(fila);
    if (tabla.rows.length > 10) tabla.deleteRow(10);
}

// Conectar a WebSocket al cargar la página
document.addEventListener("DOMContentLoaded", connectWebSocket);

// Control de pausa / reanudación
document.getElementById("pause-btn").addEventListener("click", () => {
  paused = !paused;
  document.getElementById("pause-btn").textContent = paused ? "▶ Reanudar" : "⏸ Pausar";
});

// Resetear estadísticas
document.getElementById("reset-btn").addEventListener("click", () => {
  Object.keys(sensorData).forEach(sensor => sensorData[sensor] = []);
  document.getElementById("tabla-eventos").innerHTML = "";
  document.getElementById("alert-container").innerHTML = "";

  // Si estamos conectados al servidor, no resetear los contadores locales: el backend es la fuente de la verdad
  if (!isStompConnected) {
    totalAlerts = 0;
    movimientoAlerts = 0;
    temperaturaAlerts = 0;
    accesoAlerts = 0;
    tempSum = 0;
    tempCount = 0;
    averageTemperature = 0;
    // Resetear también los últimos mostrados
    lastMovimientoShown = 0;
    lastTemperaturaShown = 0;
    lastAccesoShown = 0;
    updateCounters({
      movimientoAlerts,
      temperaturaAlerts,
      accesoAlerts,
      averageTemperature
    });
  }

  updateCharts();
});

// Simulación de lecturas en tiempo real
setInterval(() => {
  const sensores = ["movimiento", "temperatura", "acceso"];
  const sensor = sensores[Math.floor(Math.random() * sensores.length)];

  let valor;
  let isCritical = false;

  switch (sensor) {
    case "movimiento":
      valor = Math.random() * 100;
      isCritical = valor > 80;
      break;
    case "temperatura":
      valor = 15 + Math.random() * 25;
      isCritical = valor > 35;
      break;
    case "acceso":
      valor = Math.random() > 0.5 ? 1 : 0;
      isCritical = valor === 1 && Math.random() > 0.8;
      break;
  }

  onNewSensorData(sensor, valor, isCritical);
}, 2000);

// Maneja nueva lectura simulada
function onNewSensorData(sensor, value, isCritical) {
  if (paused) return;

  sensorData[sensor].push(value);
  if (sensorData[sensor].length > 20) sensorData[sensor].shift();
  updateCharts();

  const hora = new Date().toLocaleTimeString();
  const evento = {
    hora,
    sensor,
    valor: value.toFixed(2),
    critico: isCritical ? "⚠️" : "—"
  };
  addEventRow(evento);

  // Actualizar contadores locales para la simulación solamente si NO estamos conectados al servidor
  if (!isStompConnected) {
    if (isCritical) {
      totalAlerts++;
      switch (sensor) {
        case 'movimiento':
          movimientoAlerts++;
          break;
        case 'temperatura':
          temperaturaAlerts++;
          break;
        case 'acceso':
          accesoAlerts++;
          break;
      }
    }

    if (sensor === 'temperatura') {
      tempSum += value;
      tempCount++;
      averageTemperature = tempCount > 0 ? tempSum / tempCount : 0;
    }

    updateCounters({
      movimientoAlerts,
      temperaturaAlerts,
      accesoAlerts,
      averageTemperature
    });
  }

  if (isCritical && !isStompConnected) {
    showAlert(`Alerta crítica detectada en sensor de ${sensor.toUpperCase()} (valor: ${value.toFixed(2)})`);
  }
}

// ========== MOSTRAR USUARIO ==========

async function loadUserInfo() {
  try {
    const res = await fetch("/user");
    const data = await res.json();
    document.getElementById("user-info").textContent = `Usuario: ${data.username}`;
  } catch {
    document.getElementById("user-info").textContent = "Usuario: desconocido";
  }
}

document.addEventListener("DOMContentLoaded", loadUserInfo);

// ========== SELECCIÓN DE SENSORES ==========

const toggleSelector = document.getElementById("toggleSelector");
const dropdown = document.getElementById("sensorDropdown");
const options = document.querySelectorAll(".sensor-option");
let activeSensor = "movimiento"; // gráfico mostrado por defecto

toggleSelector.addEventListener("click", () => {
  dropdown.classList.toggle("hidden");
});

options.forEach(opt => {
  opt.addEventListener("click", () => {
    const selected = opt.dataset.sensor;
    document.querySelectorAll(".sensor-chart").forEach(canvas => canvas.classList.add("hidden"));
    document.getElementById(`chart${capitalize(selected)}`).classList.remove("hidden");
    dropdown.classList.add("hidden");
    toggleSelector.textContent = `Sensor: ${capitalize(selected)} ▾`;
    activeSensor = selected;
  });
});

function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1);
}

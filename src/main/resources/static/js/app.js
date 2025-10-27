let paused = false;

// Estructuras para almacenar datos de los sensores
const sensorData = {
  movimiento: [],
  temperatura: [],
  acceso: []
};

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

  if (isCritical) {
    showAlert(`Alerta crítica detectada en sensor de ${sensor.toUpperCase()} (valor: ${value.toFixed(2)})`);
  }
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

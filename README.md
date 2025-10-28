# [ PC ] Stark Industries – Sistema de Seguridad Concurrente

Enlace: https://github.com/jsepucas/PC_CasoPractico1.git

## 👥 INTEGRANTES DEL GRUPO

| Nombre  | NP            |
|----------|--------------------------------|
| **Pablo García** | 165210 |
| **Juan Sepúlveda**  | 154412 | 
| **Nerea Quintanilla** | 154409 | 

---

## [ 📘 ] DESCRIPCIÓN GENERAL

Este proyecto implementa un **sistema de seguridad avanzado para Stark Industries**, diseñado para **monitorizar sensores de movimiento, temperatura y acceso en tiempo real**.  
El objetivo es gestionar los datos de forma **concurrente, segura y eficiente**, garantizando una respuesta inmediata ante posibles amenazas o intrusiones.

El sistema se apoya en el ecosistema **Spring Framework**, combinando **Spring Boot**, **Spring Security**, **WebSocket (STOMP)**, **Thymeleaf** y **Chart.js** para ofrecer una solución completa: backend concurrente, control de acceso robusto y una interfaz visual moderna.

---

## [ 🧠 ] LÓGICA GENERAL DE LA SOLUCIÓN

1. **Gestión de sensores:**  
   Cada tipo de sensor (movimiento, temperatura, acceso) está implementado como un *bean* independiente controlado por el contenedor de Spring (IoC).  
   Se utiliza `@Async` junto con un `ThreadPoolTaskExecutor` para procesar los datos de forma paralela y no bloqueante.

2. **Procesamiento concurrente:**  
   Los sensores generan lecturas simuladas en paralelo mediante `@Scheduled` y `@Async`.  
   Estas lecturas son procesadas en tiempo real y publicadas en canales STOMP accesibles desde el frontend.

3. **Control de acceso:**  
   El sistema implementa **Spring Security** con diferentes roles:  
   - `ADMIN`: acceso completo (incluido Actuator y configuración).  
   - `TECH`: acceso a API de sensores.  
   - `USER`: acceso limitado al panel de visualización.  
   Se definen usuarios en memoria con contraseñas encriptadas.

4. **Notificaciones en tiempo real:**  
   Se utiliza **WebSocket** con STOMP para enviar datos y alertas desde el backend al frontend.  
   Las alertas críticas (por ejemplo, temperatura alta o movimiento sospechoso) se muestran instantáneamente en pantalla.

5. **Monitorización y logs:**  
   **Spring Actuator** está habilitado para supervisar el estado del sistema (`/actuator/health`, `/actuator/metrics`).  
   Además, los servicios usan `@Slf4j` para registrar la actividad y los eventos de seguridad.

---

## [ ⚙️ ] ESTRUCTURA DEL PROYECTO

> A continuación se detallan los archivos más relevantes para comprender la solución :)

### 🏗️ Configuración y arranque
- **`StarkIndustriesApplication.java`** → Clase principal del proyecto. Inicializa Spring Boot y habilita `@EnableAsync` para la ejecución concurrente.  
- **`application.properties`** → Configuración de servidor, logging, Actuator, WebSocket y seguridad.

### ⚙️ Configuración de Spring
- **`config/AsyncConfig.java`** → Define el *thread pool* usado por los procesos asíncronos de los sensores.  
- **`config/SecurityConfig.java`** → Configura usuarios, roles y las reglas de acceso mediante Spring Security.  
- **`config/WebSocketConfig.java`** → Establece el endpoint `/ws/alerts` y el broker `/topic/**` para comunicación en tiempo real.

### 📡 Controladores
- **`controller/HomeController.java`** → Gestiona rutas de inicio y redirecciones a login o dashboard.  
- **`controller/DashboardController.java`** → Carga el panel principal y los datos del usuario autenticado.  
- **`controller/SensorController.java`** → Recibe y enruta datos de sensores hacia los servicios correspondientes.

### 🧠 Servicios
- **`service/SensorSimulationService.java`** → Simula lecturas periódicas de sensores usando `@Scheduled` y `@Async`.  
- **`service/MovementSensorService.java`**, **`TemperatureSensorService.java`**, **`AccessSensorService.java`** → Procesan cada tipo de sensor y determinan condiciones críticas.  
- **`service/NotificationService.java`** → Publica los datos y alertas en los canales STOMP del frontend.  
- **`service/SecurityLogService.java`** → Registra logs de actividad y eventos de seguridad.

### 💾 Modelos
- **`model/SensorData.java`** → Clase que representa cada lectura (tipo, valor, criticidad, timestamp).

### 💻 Interfaz de usuario
- **`templates/login.html`** → Página de inicio de sesión integrada con Spring Security.  
- **`templates/dashboard.html`** → Panel visual con tres gráficas en tiempo real, tabla de eventos y alertas dinámicas.  
- **`static/js/app.js`** → Controla las gráficas (Chart.js), eventos recientes, alertas y el estado de pausa/reinicio.  
- **`static/css/style.css`** → Estilos visuales del panel (modo oscuro, layout adaptativo).

---

## [ 🔐 ] SEGURIDAD DEL SISTEMA

- **Framework:** Spring Security  
- **Roles definidos:**
  - `ADMIN` → acceso completo y monitorización (Actuator).  
  - `TECH` → acceso a API de sensores.  
  - `USER` → acceso de solo lectura al panel.
- **Usuarios de prueba:**
  | Usuario | Contraseña | Rol |
  |----------|-------------|-----|
  | `tony` | `ironman` | ADMIN |
  | `rhodey` | `war_machine` | TECH |
  | `pepper` | `rescue` | USER |

---

## [ ⚡ ] CONCURRENCIA Y RENDIEMIENTO

El uso de `@Async` y `ThreadPoolTaskExecutor` permite que los sensores se ejecuten de manera simultánea.  
Cada lectura se procesa en hilos independientes y se envía al frontend en tiempo real, manteniendo la interfaz fluida incluso con múltiples eventos por segundo.  

---

## [ 📡 ] COMUNICACIÓN EN TIEMPO REAL

- **Backend → Frontend:** STOMP sobre WebSocket  
  - `/topic/data`: envía lecturas en tiempo real.  
  - `/topic/alerts`: notifica alertas críticas.  
- **Frontend:** recibe las actualizaciones y las refleja en las gráficas y tabla sin recargar la página.  

---

## [ 📊 ] MONOTORIZACIÓN Y LOGS

- **Spring Actuator** habilitado para endpoints de salud y métricas.  
- **Logging estructurado** mediante `@Slf4j`, mostrando actividad concurrente, alertas y autenticaciones.

---

## [ ✅ ] RESULTADOS Y CRITERIOS DE ÉXITO

- El sistema procesa datos de tres sensores en paralelo sin bloqueos.  
- Las gráficas se actualizan en tiempo real y las alertas aparecen inmediatamente.  
- El control de acceso funciona correctamente según el rol de cada usuario.  
- Los endpoints de Actuator permiten verificar el estado general del sistema.

**Criterios cumplidos:**
- Procesamiento concurrente eficiente.  
- Alertas entregadas en tiempo real.  
- Control de acceso funcional y seguro.  
- Sistema estable, monitorizable y sin caídas.

---

## [ 🎨 ] ELEMENTOS VISUALES

- **Gráficas (Chart.js):** evolución de cada sensor.  
- **Tabla dinámica:** últimos eventos registrados.  
- **Alertas visuales:** notificaciones en pantalla ante detecciones críticas.  
- **Panel unificado:** interfaz moderna con identidad visual de Stark Industries.

---

## [ 📚 ] REFERENCIAS

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)  
- [Spring Boot Reference Guide](https://spring.io/projects/spring-boot)  
- [Spring Security Reference](https://spring.io/projects/spring-security)  
- [Baeldung: Spring WebSocket + STOMP Guide](https://www.baeldung.com/websockets-spring)

---

## [ ▶️ ] EJECUCIÓN DEL PROYECTO

1. Abrir el proyecto en IntelliJ o Eclipse.  
2. Ejecutar la clase principal `StarkIndustriesApplication.java`.  
   O desde consola:
   ```bash
   mvn spring-boot:run

3. Acceder en el navegador a:
   ```bash
   http://localhost:8080/login

4. Iniciar sesión con cualquiera de los usuarios de prueba.
5. Visualizar el panel con las gráficas, tabla de eventos y alertas en tiempo real.

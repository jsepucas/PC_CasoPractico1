package com.starkindustries.securitysystem.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Servicio responsable de enviar notificaciones y datos en tiempo real
 * a través de WebSocket (STOMP).
 *
 * Envía dos tipos de mensajes:
 * - /topic/data  → datos normales de sensores
 * - /topic/alerts → alertas críticas
 */
@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Declaración de variables para contadores de alertas y temperatura media
    private int totalAlerts = 0;
    private int movimientoAlerts = 0;
    private int temperaturaAlerts = 0;
    private int accesoAlerts = 0;
    private double totalTemperatura = 0;
    private int totalTemperaturaCount = 0;

    // Constructor donde se inyecta el SimpMessagingTemplate
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Envía lecturas normales de sensores al frontend.
     *
     * @param type      tipo de sensor (movimiento, temperatura, acceso)
     * @param value     valor leído
     * @param critical  indica si la lectura es crítica
     */
    public void sendSensorData(String type, double value, boolean critical) {
        // Contabilizamos las alertas por tipo de sensor
        if (critical) {
            totalAlerts++;
            switch (type) {
                case "movimiento":
                    movimientoAlerts++; // Actualizamos las alertas de movimiento
                    break;
                case "temperatura":
                    temperaturaAlerts++; // Actualizamos las alertas de temperatura
                    break;
                case "acceso":
                    accesoAlerts++; // Actualizamos las alertas de acceso
                    break;
            }
        }

        // Actualizamos la temperatura media (solo para el sensor de temperatura)
        if ("temperatura".equals(type)) {
            totalTemperatura += value;
            totalTemperaturaCount++;
        }

        // Enviamos los datos al frontend con los contadores actualizados
        messagingTemplate.convertAndSend("/topic/data", Map.of(
                "type", type,
                "value", value,
                "critical", critical,
                "timestamp", System.currentTimeMillis(),
                "totalAlerts", totalAlerts,
                "movimientoAlerts", movimientoAlerts,
                "temperaturaAlerts", temperaturaAlerts,
                "accesoAlerts", accesoAlerts,
                "averageTemperature", totalTemperaturaCount > 0 ? totalTemperatura / totalTemperaturaCount : 0
        ));
    }


    /**
     * Envía una alerta crítica al frontend.
     *
     * @param message contenido textual de la alerta
     */
    public void sendAlert(String message) {
        messagingTemplate.convertAndSend("/topic/alerts", Map.of(
                "content", message,
                "timestamp", System.currentTimeMillis()
        ));
    }
}

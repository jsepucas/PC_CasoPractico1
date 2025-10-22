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
        messagingTemplate.convertAndSend("/topic/data", Map.of(
                "type", type,
                "value", value,
                "critical", critical,
                "timestamp", System.currentTimeMillis()
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

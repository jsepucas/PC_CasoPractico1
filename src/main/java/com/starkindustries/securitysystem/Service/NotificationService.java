package com.starkindustries.securitysystem.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final Counter eventsProcessedCounter;

    private int totalAlerts = 0;
    private int movimientoAlerts = 0;
    private int temperaturaAlerts = 0;
    private int accesoAlerts = 0;
    private double totalTemperatura = 0;
    private int totalTemperaturaCount = 0;

    // Constructor donde se inyecta SimpMessagingTemplate
    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate, MeterRegistry meterRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.eventsProcessedCounter = Counter.builder("events.processed")
                .description("Número de lecturas de sensores procesadas")
                .register(meterRegistry);
    }

    // Constructor adicional para facilitar los tests que usan SimpleMeterRegistry
    public NotificationService(SimpMessagingTemplate messagingTemplate, io.micrometer.core.instrument.simple.SimpleMeterRegistry simpleMeterRegistry) {
        this(messagingTemplate, (MeterRegistry) simpleMeterRegistry);
    }

    // Constructor conveniente para tests: crea internamente un SimpleMeterRegistry
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this(messagingTemplate, new io.micrometer.core.instrument.simple.SimpleMeterRegistry());
    }

    /**
     * Enviar los datos del sensor al frontend
     */
    public void sendSensorData(String type, double value, boolean critical) {
        // Incrementar la métrica de eventos procesados
        this.eventsProcessedCounter.increment();

        // Contabilizamos las alertas
        if (critical) {
            totalAlerts++;
            switch (type) {
                case "movimiento":
                    movimientoAlerts++;
                    break;
                case "temperatura":
                    temperaturaAlerts++;
                    break;
                case "acceso":
                    accesoAlerts++;
                    break;
            }
        }

        // Calcular la temperatura media
        if ("temperatura".equals(type)) {
            totalTemperatura += value;
            totalTemperaturaCount++;
        }

        // Enviar los datos al frontend con los contadores actualizados
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
     * Enviar una alerta crítica al frontend
     */
    public void sendAlert(String message) {
        messagingTemplate.convertAndSend("/topic/alerts", Map.of(
                "content", message,
                "timestamp", System.currentTimeMillis()
        ));
    }

    public int getTotalAlerts() {
        return totalAlerts;
    }

    public int getMovimientoAlerts() {
        return movimientoAlerts;
    }

    public int getTemperaturaAlerts() {
        return temperaturaAlerts;
    }

    public int getAccesoAlerts() {
        return accesoAlerts;
    }

    public double getAverageTemperature() {
        return totalTemperaturaCount > 0 ? totalTemperatura / totalTemperaturaCount : 0;
    }

    public double getEventsProcessed() {
        return eventsProcessedCounter.count();
    }
}
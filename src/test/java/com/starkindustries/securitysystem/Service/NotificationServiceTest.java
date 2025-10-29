package com.starkindustries.securitysystem.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private SimpMessagingTemplate messagingTemplate;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        notificationService = new NotificationService(messagingTemplate);
    }

    @Test
    void sendSensorData_incrementsMetricAndSendsMessage() {
        // Enviar una lectura crítica de movimiento
        notificationService.sendSensorData("movimiento", 90.5, true);

        // Comprobar métrica incrementada
        assertEquals(1.0, notificationService.getEventsProcessed(), 0.0001);

        // Verificar que SimpMessagingTemplate.convertAndSend fue llamado con /topic/data
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate, times(1)).convertAndSend(destinationCaptor.capture(), payloadCaptor.capture());

        assertEquals("/topic/data", destinationCaptor.getValue());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) payloadCaptor.getValue();
        assertEquals("movimiento", payload.get("type"));
        assertEquals(90.5, ((Number) payload.get("value")).doubleValue());
        assertEquals(true, payload.get("critical"));

        // Comprobar que los contadores propios del servicio se incrementaron
        assertEquals(1, notificationService.getTotalAlerts());
        assertEquals(1, notificationService.getMovimientoAlerts());
    }
}

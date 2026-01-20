package com.starkindustries.securitysystem.Service;

import com.starkindustries.securitysystem.Model.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j


//Le avisa al sistema que esta clase es un servicio, hace calculos y lógica 
@Service
public class MotionSensorService {

    private final NotificationService notificationService;

    public MotionSensorService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async("sensorExecutor")
    public void processSensorData(SensorData data) {
        log.info("[SensorMovimiento] Valor: {} (Crítico: {})", data.getValue(), data.isCritical());

        // Enviar los datos al frontend
        notificationService.sendSensorData(data.getType(), data.getValue(), data.isCritical());

        // Enviar alerta si es crítica
        if (data.isCritical()) {
            notificationService.sendAlert("⚠️ Movimiento crítico detectado!");
        }
    }
}



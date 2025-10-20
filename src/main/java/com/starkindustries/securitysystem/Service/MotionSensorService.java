package com.starkindustries.securitysystem.Service;

import com.starkindustries.securitysystem.Model.SensorData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio que procesa datos provenientes de sensores de movimiento.
 * Utiliza procesamiento asíncrono para manejar múltiples lecturas simultáneamente.
 */
@Service
public class MotionSensorService {

    @Async("sensorExecutor")
    public void processSensorData(SensorData data) {
        System.out.println("[MotionSensor] Procesando: " + data);
        if (data.isCritical()) {
            System.out.println("[MotionSensor] ALERTA: Movimiento crítico detectado.");
        }
    }
}

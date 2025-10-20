package com.starkindustries.securitysystem.Service;

import com.starkindustries.securitysystem.Model.SensorData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio que procesa datos de sensores de acceso o intrusión.
 */
@Service
public class AccessSensorService {

    @Async("sensorExecutor")
    public void processSensorData(SensorData data) {
        System.out.println("[AccessSensor] Procesando: " + data);
        if (data.isCritical()) {
            System.out.println("[AccessSensor] ALERTA: Acceso no autorizado detectado.");
        }
    }
}

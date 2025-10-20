package com.starkindustries.securitysystem.Service;

import com.starkindustries.securitysystem.Model.SensorData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio que procesa datos de sensores de temperatura.
 */
@Service
public class TemperatureSensorService {

    @Async("sensorExecutor")
    public void processSensorData(SensorData data) {
        System.out.println("[TemperatureSensor] Procesando: " + data);
        if (data.isCritical()) {
            System.out.println("[TemperatureSensor] ALERTA: Temperatura fuera de rango.");
        }
    }
}

package com.starkindustries.securitysystem.Service;

import com.starkindustries.securitysystem.Model.SensorData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Servicio encargado de simular lecturas concurrentes
 * de los sensores de movimiento, temperatura y acceso.
 */
@Service
@EnableScheduling
public class SensorSimulationService {

    private final MotionSensorService motionSensorService;
    private final TemperatureSensorService temperatureSensorService;
    private final AccessSensorService accessSensorService;
    private final Random random = new Random();

    public SensorSimulationService(MotionSensorService motionSensorService,
                                   TemperatureSensorService temperatureSensorService,
                                   AccessSensorService accessSensorService) {
        this.motionSensorService = motionSensorService;
        this.temperatureSensorService = temperatureSensorService;
        this.accessSensorService = accessSensorService;
    }

    @Async("sensorExecutor")
    @Scheduled(fixedRate = 3000)
    public void simulateMotionSensor() {
        SensorData data = new SensorData();
        data.setType("movimiento");
        data.setValue(random.nextInt(100));
        data.setTimestamp(System.currentTimeMillis());
        data.setCritical(data.getValue() > 80);
        motionSensorService.processSensorData(data);
    }

    @Async("sensorExecutor")
    @Scheduled(fixedRate = 5000)
    public void simulateTemperatureSensor() {
        SensorData data = new SensorData();
        data.setType("temperatura");
        data.setValue(15 + random.nextDouble() * 20);
        data.setTimestamp(System.currentTimeMillis());
        data.setCritical(data.getValue() > 30);
        temperatureSensorService.processSensorData(data);
    }

    @Async("sensorExecutor")
    @Scheduled(fixedRate = 7000)
    public void simulateAccessSensor() {
        SensorData data = new SensorData();
        data.setType("acceso");
        data.setValue(random.nextInt(2)); // 0=cerrado, 1=abierto
        data.setTimestamp(System.currentTimeMillis());
        data.setCritical(data.getValue() == 1 && random.nextBoolean());
        accessSensorService.processSensorData(data);
    }
}

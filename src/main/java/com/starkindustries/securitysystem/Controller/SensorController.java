package com.starkindustries.securitysystem.Controller;

import com.starkindustries.securitysystem.Model.SensorData;
import com.starkindustries.securitysystem.Service.AccessSensorService;
import com.starkindustries.securitysystem.Service.MotionSensorService;
import com.starkindustries.securitysystem.Service.TemperatureSensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador central encargado de recibir y distribuir los datos de los sensores.
 */
@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    private final MotionSensorService motionService;
    private final TemperatureSensorService tempService;
    private final AccessSensorService accessService;

    public SensorController(MotionSensorService motionService,
                            TemperatureSensorService tempService,
                            AccessSensorService accessService) {
        this.motionService = motionService;
        this.tempService = tempService;
        this.accessService = accessService;
    }

    @PostMapping("/data")
    public ResponseEntity<String> receiveData(@RequestBody SensorData data) {
        switch (data.getType().toLowerCase()) {
            case "movimiento" -> motionService.processSensorData(data);
            case "temperatura" -> tempService.processSensorData(data);
            case "acceso" -> accessService.processSensorData(data);
            default -> System.out.println("Tipo de sensor no reconocido: " + data.getType());
        }
        return ResponseEntity.ok("Datos recibidos y procesados concurrentemente.");
    }
}

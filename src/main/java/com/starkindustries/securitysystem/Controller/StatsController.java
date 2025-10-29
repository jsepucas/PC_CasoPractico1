package com.starkindustries.securitysystem.Controller;

import com.starkindustries.securitysystem.Service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    private final NotificationService notificationService;

    public StatsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of(
                "totalAlerts", notificationService.getTotalAlerts(),
                "movimientoAlerts", notificationService.getMovimientoAlerts(),
                "temperaturaAlerts", notificationService.getTemperaturaAlerts(),
                "accesoAlerts", notificationService.getAccesoAlerts(),
                "averageTemperature", notificationService.getAverageTemperature(),
                "eventsProcessed", notificationService.getEventsProcessed()
        );
    }
}


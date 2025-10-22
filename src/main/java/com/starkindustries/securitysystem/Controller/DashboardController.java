package com.starkindustries.securitysystem.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador responsable de servir el panel de control HTML.
 */
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // busca dashboard.html en /resources/templates/
    }
}

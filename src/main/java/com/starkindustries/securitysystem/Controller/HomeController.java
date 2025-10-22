package com.starkindustries.securitysystem.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirige la ruta raíz ("/") hacia el panel principal ("/dashboard").
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }
}

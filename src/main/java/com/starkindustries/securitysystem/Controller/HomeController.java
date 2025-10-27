// java
package com.starkindustries.securitysystem.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String rootRedirect() {
        // redirige la raíz al login
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        // devuelve login.html en src/main/resources/templates/
        return "login";
    }
}

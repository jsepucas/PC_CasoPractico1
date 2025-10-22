package com.starkindustries.securitysystem.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador que devuelve la información del usuario autenticado.
 */
@RestController
public class UserController {

    @GetMapping("/user")
    public Map<String, String> getUser(Authentication authentication) {
        Map<String, String> userData = new HashMap<>();

        if (authentication != null) {
            userData.put("username", authentication.getName());
            userData.put("role", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_UNKNOWN"));
        } else {
            userData.put("username", "anonymous");
            userData.put("role", "none");
        }

        return userData;
    }
}

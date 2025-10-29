package com.starkindustries.securitysystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicación principal del Sistema de Seguridad Concurrente de Stark Industries.
 * - Usa Spring Boot 3.5.6.
 * - Habilita el procesamiento asíncrono mediante @EnableAsync.
 * - Este es el punto de entrada principal de la aplicación.
 */
@SpringBootApplication
@EnableAsync
public class    AppStarkSistemaSeguridad {

    public static void main(String[] args) {
        SpringApplication.run(AppStarkSistemaSeguridad.class, args);
    }
}

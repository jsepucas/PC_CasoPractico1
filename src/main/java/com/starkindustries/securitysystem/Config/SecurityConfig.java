package com.starkindustries.securitysystem.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración central de seguridad del sistema.
 *
 * Define los usuarios, roles y permisos de acceso a los endpoints REST.
 * En esta versión inicial, los usuarios se cargan en memoria.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad.
     *
     * - Desactiva CSRF para facilitar las pruebas REST.
     * - Define permisos de acceso según el rol del usuario.
     * - Habilita autenticación básica y formulario.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/sensor/data").hasAnyRole("ADMIN", "TECH")
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())   // Login por formulario
                .httpBasic(Customizer.withDefaults());  // Login por cabecera HTTP (útil para Postman)
        return http.build();
    }

    /**
     * Define usuarios y roles almacenados en memoria.
     *
     * En producción, esto se reemplazaría por un UserDetailsService conectado a base de datos.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("tony")
                .password("ironman")
                .roles("ADMIN")
                .build();

        UserDetails technician = User.withDefaultPasswordEncoder()
                .username("rhodey")
                .password("war_machine")
                .roles("TECH")
                .build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("pepper")
                .password("rescue")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, technician, user);
    }
}

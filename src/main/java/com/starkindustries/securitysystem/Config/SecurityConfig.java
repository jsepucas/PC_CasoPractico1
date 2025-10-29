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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/dashboard", "/user").authenticated()
                        .requestMatchers("/api/sensor/**").hasAnyRole("ADMIN", "TECH")
                        .requestMatchers("/api/stats").hasAnyRole("ADMIN", "TECH")
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")                 // Ruta personalizada de login
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")   // Redirige al login tras logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

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

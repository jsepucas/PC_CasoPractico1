package com.starkindustries.securitysystem.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración central de concurrencia para el sistema de sensores.
 * Define un pool de hilos dedicado al procesamiento concurrente de datos.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "sensorExecutor")
    public Executor sensorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("SensorThread-");
        executor.initialize();
        return executor;
    }
}

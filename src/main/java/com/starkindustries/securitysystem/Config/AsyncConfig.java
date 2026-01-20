package com.starkindustries.securitysystem.Config;

// Import: org.springframework.context.annotation.Bean
// - Para tontos: esto es como decirle a Spring "oye, este método crea algo que quiero que guarde y reutilice".
// - Técnico: la anotación @Bean se usa para declarar un método que produce un bean gestionado por el contenedor de Spring.
import org.springframework.context.annotation.Bean;

// Import: org.springframework.context.annotation.Configuration
// - Para tontos: marca esta clase como un lugar donde ponemos configuraciones (como una receta) que Spring leerá.
// - Técnico: @Configuration indica que la clase contiene definiciones de beans y puede ser procesada por el contenedor Spring para generar beans en tiempo de ejecución.
import org.springframework.context.annotation.Configuration;

// Import: org.springframework.scheduling.annotation.EnableAsync
// - Para tontos: activa la capacidad de ejecutar tareas en segundo plano (asincrónico) dentro de la app.
// - Técnico: @EnableAsync habilita el procesamiento de métodos anotados con @Async y permite que Spring procese invocaciones asíncronas.
import org.springframework.scheduling.annotation.EnableAsync;

// Import: org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
// - Para tontos: es una caja (un ejecutor) que crea y gestiona varios hilos (trabajadores) para ejecutar tareas al mismo tiempo.
// - Técnico: ThreadPoolTaskExecutor es una implementación de TaskExecutor basada en un pool de hilos que se integra con Spring y java.util.concurrent.
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// Import: java.util.concurrent.Executor
// - Para tontos: es la interfaz simple que representa "algo que puede ejecutar tareas"; devolvemos esto para no atar el código a una implementación concreta.
// - Técnico: Executor es la interfaz base en java.concurrent para ejecutar runnables (tareas). Devuelve un tipo general para mayor abstracción.
import java.util.concurrent.Executor;

/**
 * Configuración central de concurrencia para el sistema de sensores.
 * Define un pool de hilos dedicado al procesamiento concurrente de datos.
 *
 * Explicación para tontos:
 * - Esta clase le dice a Spring: "cuando necesites ejecutar cosas en segundo plano, usa este grupo de trabajadores llamados sensorExecutor".
 * - Piensa en ello como contratar varios empleados (hilos) para que procesen lecturas de sensores al mismo tiempo.
 *
 * Explicación técnica (sintaxis y comportamiento):
 * - @Configuration: marca la clase para que Spring la procese en tiempo de arranque y registre los beans definidos.
 * - @EnableAsync: habilita la ejecución asíncrona (por ejemplo, cuando otros métodos usen @Async o invoquen este executor explícitamente).
 * - El método sensorExecutor() está anotado con @Bean y devuelve un Executor; Spring registrará ese objeto en el contexto con el nombre "sensorExecutor".
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    //explicacion para entender yo basico:

    // @Bean(name = "sensorExecutor")
    // esto crea y registra el "sensorExecutor" para que otras partes de la app lo usen.
    // - Técnico: declara un bean con nombre "sensorExecutor" que será gestionado por el contenedor Spring. Al devolver la interfaz Executor, le damos a Spring la instancia concreta del ThreadPoolTaskExecutor.
    @Bean(name = "sensorExecutor")
    public Executor sensorExecutor() {
        // Crear un objeto ThreadPoolTaskExecutor (implementación concreta)
        // - Para tontos: estamos construyendo la caja con los trabajadores.
        // - Técnico: instanciamos ThreadPoolTaskExecutor para configurar el tamaño del pool y otras propiedades.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Número mínimo de hilos que siempre intentará mantener (core pool size)
        // - Para tontos: cuántos empleados activos hay siempre disponibles.
        // - Técnico: setCorePoolSize define el número de hilos que se mantienen incluso si están inactivos.
        executor.setCorePoolSize(8);

        // Número máximo de hilos que puede crecer cuando hay mucha carga (max pool size)
        // - Para tontos: el máximo de empleados que puedes contratar temporalmente cuando llega mucho trabajo.
        // - Técnico: setMaxPoolSize define el límite superior de hilos en el pool.
        executor.setMaxPoolSize(20);

        // Capacidad de la cola de tareas en espera antes de crear nuevos hilos (queue capacity)
        // - Para tontos: cuántas tareas pueden esperar en la fila antes de necesitar más empleados.
        // - Técnico: setQueueCapacity define cuántas Runnable pueden encolarse; si se llena y hay menos que maxPoolSize hilos, se crearán más hilos.
        executor.setQueueCapacity(100);

        // Prefijo para el nombre de los hilos creados por este executor
        // - Para tontos: ayuda a identificar en los logs qué hilo pertenece a los sensores (ej. "SensorThread-1").
        // - Técnico: setThreadNamePrefix establece el prefijo para los nombres de los hilos creados por el pool.
        executor.setThreadNamePrefix("SensorThread-");

        // Inicializar el ejecutor para que empiece a aceptar tareas
        // - Para tontos: arrancamos la caja y dejamos que los empleados empiecen a trabajar.
        // - Técnico: initialize prepara internamente la estructura del ThreadPoolTaskExecutor y crea los hilos necesarios.
        executor.initialize();

        // Devolvemos la instancia como la interfaz Executor (abstracción)
        // - Para tontos: damos la caja lista para que otros la usen.
        // - Técnico: devolvemos la referencia; Spring la guardará como bean y otros componentes inyectarán este Executor por nombre o tipo.
        return executor;
    }
}

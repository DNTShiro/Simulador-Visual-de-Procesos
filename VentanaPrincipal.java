package modelo;

/**
 * Ciclo de vida del proceso dentro del simulador.
 */
public enum EstadoProceso {
    NUEVO,       // Recien creado, aun no ingresa a la cola de listos
    LISTO,       // En la cola de listos, esperando CPU
    EJECUCION,   // Actualmente usando la CPU
    ESPERA_MEMORIA, // No pudo cargarse por falta de RAM simulada
    TERMINADO    // Finalizo su ejecucion
}

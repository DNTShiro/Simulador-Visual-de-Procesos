package controlador;

import modelo.Proceso;
import java.util.HashMap;

/**
 * Simula una memoria RAM de tamaño fijo. Antes de que un proceso entre
 * a ejecucion, el planificador le pregunta a esta clase si hay espacio.
 */
public class GestorMemoria {

    private int capacidadTotalKB;
    private int usadaKB;
    private HashMap<Integer, Integer> asignaciones = new HashMap<Integer, Integer>(); // pid -> KB usados

    public GestorMemoria(int capacidadTotalKB) {
        this.capacidadTotalKB = capacidadTotalKB;
        this.usadaKB = 0;
    }

    public int getLibreKB() {
        return capacidadTotalKB - usadaKB;
    }

    // Intenta reservar memoria para un proceso. Devuelve true si alcanzo el espacio.
    public boolean asignar(Proceso p) {
        if (asignaciones.containsKey(p.getPid())) {
            return true; // ya tenia memoria asignada
        }
        if (p.getMemoriaRequerida() <= getLibreKB()) {
            asignaciones.put(p.getPid(), p.getMemoriaRequerida());
            usadaKB = usadaKB + p.getMemoriaRequerida();
            return true;
        }
        return false;
    }

    // Libera la memoria de un proceso cuando termina
    public void liberar(Proceso p) {
        if (asignaciones.containsKey(p.getPid())) {
            int kb = asignaciones.get(p.getPid());
            usadaKB = usadaKB - kb;
            asignaciones.remove(p.getPid());
        }
    }

    public boolean tieneMemoria(int pid) {
        return asignaciones.containsKey(pid);
    }

    public void reiniciar() {
        asignaciones.clear();
        usadaKB = 0;
    }
}

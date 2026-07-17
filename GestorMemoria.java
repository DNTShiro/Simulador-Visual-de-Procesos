package controlador;

import modelo.Procesos;
import java.util.HashMap;

/**
 * Simula una memoria RAM de tamaño fijo.
 */
public class GestorMemoria {

    private int capacidadTotalKB;
    private int usadaKB;

    // pid -> KB usados
    private HashMap<Integer, Integer> asignaciones =
            new HashMap<Integer, Integer>();

    public GestorMemoria(int capacidadTotalKB) {
        this.capacidadTotalKB = capacidadTotalKB;
        this.usadaKB = 0;
    }

    public int getLibreKB() {
        return capacidadTotalKB - usadaKB;
    }

    // Intenta reservar memoria para un proceso
    public boolean asignar(Procesos p) {

        if (asignaciones.containsKey(p.getPid())) {
            return true; // ya tenía memoria asignada
        }

        if (p.getMemoriaRequerida() <= getLibreKB()) {

            asignaciones.put(
                    p.getPid(),
                    p.getMemoriaRequerida()
            );

            usadaKB += p.getMemoriaRequerida();

            return true;
        }

        return false;
    }

    // Libera la memoria cuando el proceso termina
    public void liberar(Procesos p) {

        if (asignaciones.containsKey(p.getPid())) {

            int kb = asignaciones.get(p.getPid());

            usadaKB -= kb;

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

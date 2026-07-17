package controlador;

import modelo.Proceso;
import java.util.HashMap;

/**
 * Representa un tramo de tiempo en el que un proceso ocupo la CPU.
 * Se usa tanto para dibujar el Diagrama de Gantt como para animar la simulacion.
 */
public class BloqueGantt{

    public int pid;
    public String nombre;
    public int inicio;
    public int fin;

    public BloqueGantt( int pid, String nombre, int inicio, int fin) {
        this.pid = pid;
        this.nombre = nombre;
        this.inicio = inicio;
        this.fin = fin;
    }

    public int duracion () {
        return fin - inicio;
    }
}

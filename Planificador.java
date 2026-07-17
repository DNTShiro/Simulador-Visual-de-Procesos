package controlador;

import modelo.Proceso;
import java.util.List;

/**
 * Guarda todo lo que produce el planificador: la linea de tiempo (para
 * el diagrama de Gantt), los procesos con sus tiempos ya calculados y
 * los mensajes de advertencia (por ejemplo, de memoria insuficiente).
 */
public class ResultadoSimulacion {
    public List<BloqueGantt> lineaTiempo;
    public List<Proceso> procesos;
    public List<String> mensajes;
    public int tiempoTotal;

    public ResultadoSimulacion(List<BloqueGantt> lineaTiempo, List<Proceso> procesos,
                                List<String> mensajes, int tiempoTotal) {
        this.lineaTiempo = lineaTiempo;
        this.procesos = procesos;
        this.mensajes = mensajes;
        this.tiempoTotal = tiempoTotal;
    }

    public double getEsperaPromedio() {
        int suma = 0;
        for (int i = 0; i < procesos.size(); i++) {
            suma = suma + procesos.get(i).getTiempoEspera();
        }
        return (double) suma / procesos.size();
    }

    public double getRetornoPromedio() {
        int suma = 0;
        for (int i = 0; i < procesos.size(); i++) {
            suma = suma + procesos.get(i).getTiempoRetorno();
        }
        return (double) suma / procesos.size();
    }
}

package modelo;

/**
 * Representa un proceso (PCB - Bloque de Control de Proceso).
 * Guarda los datos que se piden al usuario y los datos que calcula
 * el planificador durante la simulacion.
 */
public class Proceso {

    private int pid;
    private String nombre;
    private int tiempoLlegada;
    private int rafaga;          // tiempo de CPU que necesita
    private int prioridad;
    private int memoriaRequerida; // KB simulados

    private int tiempoRestante;
    private EstadoProceso estado;
    private int tiempoInicio = -1;
    private int tiempoFin = -1;
    private boolean advertenciaMemoria = false;

    public Proceso(int pid, String nombre, int tiempoLlegada, int rafaga, int prioridad, int memoriaRequerida) {
        this.pid = pid;
        this.nombre = nombre;
        this.tiempoLlegada = tiempoLlegada;
        this.rafaga = rafaga;
        this.prioridad = prioridad;
        this.memoriaRequerida = memoriaRequerida;
        this.tiempoRestante = rafaga;
        this.estado = EstadoProceso.NUEVO;
    }

    public int getPid() { 
        return pid; 
    }
    public String getNombre() { 
        return nombre; 
    }
    public int getTiempoLlegada() { 
        return tiempoLlegada; 
    }
    public int getRafaga() { 
        return rafaga; 
    }
    public int getPrioridad() { 
        return prioridad; 
    }
    public int getMemoriaRequerida() { 
        return memoriaRequerida; 
    }

    public int getTiempoRestante() { 
        return tiempoRestante; 
    }
    public void setTiempoRestante(int tiempoRestante) { 
        this.tiempoRestante = tiempoRestante; 
    }

    public EstadoProceso getEstado() { 
        return estado; 
    }
    public void setEstado(EstadoProceso estado) { 
        this.estado = estado; 
    }

    public int getTiempoInicio() { 
        return tiempoInicio; 
    }
    public void setTiempoInicio(int t) {
        if (tiempoInicio == -1) tiempoInicio = t;
    }

    public int getTiempoFin() { 
        return tiempoFin; 
    }
    public void setTiempoFin(int tiempoFin) { 
        this.tiempoFin = tiempoFin; 
    }

    public boolean isAdvertenciaMemoria() { 
        return advertenciaMemoria; 
    }
    public void setAdvertenciaMemoria(boolean valor) { 
        advertenciaMemoria = valor; 
    }

    // Tiempo de retorno = fin - llegada
    public int getTiempoRetorno() {
        if (tiempoFin == -1) return -1;
        return tiempoFin - tiempoLlegada;
    }

    // Tiempo de espera = retorno - rafaga
    public int getTiempoEspera() {
        if (tiempoFin == -1) return -1;
        return getTiempoRetorno() - rafaga;
    }

    // Vuelve a dejar el proceso como recien creado, para simular otra vez
    public void reiniciar() {
        tiempoRestante = rafaga;
        estado = EstadoProceso.NUEVO;
        tiempoInicio = -1;
        tiempoFin = -1;
        advertenciaMemoria = false;
    }

    public String toString() {
        return "P" + pid + "-" + nombre;
    }
}


/**
 * Representa un proceso (PCB - Bloque de Control de Proceso).
 * Guarda los datos que se piden al usuario y los datos que calcula
 * el planificador durante la simulacion.
 */
public class Procesos {
    int ID;
    String nombre;
    int tiempoLLegada;
    int prioridad;
    int rafaga; //tiempo del CPU que necesita 
    int memoriaRequerida;
    int tiempoRequerido;
    EstadoProceso estado;
    int tiempoInicio = -1;
    int tiempoFin = -1;
    boolean advertenciaMemoria;

    public Procesos(int ID, String nombre, int tiempoLLegada, int rafaga, int prioridad, int memoriaRequerida) {
        this.ID = ID;
        this.nombre = nombre;
        this.tiempoLLegada = tiempoLLegada;
        this.rafaga = rafaga;
        this.prioridad = prioridad;
        this.memoriaRequerida = memoriaRequerida;
        this.tiempoRestante = rafaga;
        this.estado = EstadoProceso.NUEVO;
    }

    public int getID() {
        return ID;
    }
    public String getnombre() {
        return nombre;
    }
    public int gettiempoLLegada() {
        return tiempoLLegada;
    }
    public int getrafaga() {
        return rafaga;
    }
    public int getprioridad() {
        return prioridad;
    }
    public int getmemoriaRequerida() {
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
        if (tiempoInicio == -1){
            tiempoInicio = t;
        }
    }
}

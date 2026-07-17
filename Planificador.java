package controlador;

import modelo.EstadoProceso;
import modelo.Proceso;

import java.util.ArrayList;
import java.util.List;

/**
 * Contiene los tres algoritmos de planificacion: FCFS, SJF y Round Robin.
 * Antes de que un proceso pase a "Ejecucion" se valida si hay memoria RAM
 * simulada disponible (GestorMemoria). Si no alcanza, el proceso se marca
 * con una advertencia pero se deja avanzar igual, para no trabar la demo.
 */
public class Planificador {

    public static final int FCFS = 1;
    public static final int SJF = 2;
    public static final int ROUND_ROBIN = 3;

    private GestorMemoria memoria;

    public Planificador(GestorMemoria memoria) {
        this.memoria = memoria;
    }

    public ResultadoSimulacion planificar(List<Proceso> listaOriginal, int algoritmo, int quantum) {
        // Reiniciamos los procesos por si ya se habian simulado antes
        for (int i = 0; i < listaOriginal.size(); i++) {
            listaOriginal.get(i).reiniciar();
        }
        memoria.reiniciar();

        if (algoritmo == FCFS) {
            return ejecutarFCFS(listaOriginal);
        } else if (algoritmo == SJF) {
            return ejecutarSJF(listaOriginal);
        } else {
            return ejecutarRoundRobin(listaOriginal, quantum);
        }
    }

    // ---------------------------------------------------------
    // Ordena una lista de procesos por tiempo de llegada (burbuja)
    // ---------------------------------------------------------
    private List<Proceso> ordenarPorLlegada(List<Proceso> lista) {
        List<Proceso> copia = new ArrayList<Proceso>(lista);
        for (int i = 0; i < copia.size() - 1; i++) {
            for (int j = 0; j < copia.size() - 1 - i; j++) {
                if (copia.get(j).getTiempoLlegada() > copia.get(j + 1).getTiempoLlegada()) {
                    Proceso temp = copia.get(j);
                    copia.set(j, copia.get(j + 1));
                    copia.set(j + 1, temp);
                }
            }
        }
        return copia;
    }

    // ---------------------------------------------------------
    // FCFS: el que llega primero, se atiende primero
    // ---------------------------------------------------------
    private ResultadoSimulacion ejecutarFCFS(List<Proceso> procesos) {
        List<Proceso> orden = ordenarPorLlegada(procesos);
        List<BloqueGantt> gantt = new ArrayList<BloqueGantt>();
        List<String> mensajes = new ArrayList<String>();
        int tiempoActual = 0;

        for (int i = 0; i < orden.size(); i++) {
            Proceso p = orden.get(i);
            if (p.getTiempoLlegada() > tiempoActual) {
                tiempoActual = p.getTiempoLlegada();
            }
            p.setEstado(EstadoProceso.LISTO);
            validarMemoria(p, mensajes, tiempoActual);

            int inicio = tiempoActual;
            p.setTiempoInicio(inicio);
            p.setEstado(EstadoProceso.EJECUCION);

            int fin = inicio + p.getRafaga();
            gantt.add(new BloqueGantt(p.getPid(), p.getNombre(), inicio, fin));

            tiempoActual = fin;
            p.setTiempoRestante(0);
            p.setTiempoFin(fin);
            p.setEstado(EstadoProceso.TERMINADO);
            memoria.liberar(p);
        }
        return new ResultadoSimulacion(gantt, orden, mensajes, tiempoActual);
    }

    // ---------------------------------------------------------
    // SJF (no expropiativo): entre los que ya llegaron, se ejecuta
    // el que tenga la rafaga (tiempo de CPU) mas corta
    // ---------------------------------------------------------
    private ResultadoSimulacion ejecutarSJF(List<Proceso> procesos) {
        List<Proceso> pendientes = new ArrayList<Proceso>(procesos);
        List<Proceso> terminados = new ArrayList<Proceso>();
        List<BloqueGantt> gantt = new ArrayList<BloqueGantt>();
        List<String> mensajes = new ArrayList<String>();
        int tiempoActual = 0;

        while (pendientes.size() > 0) {
            // Buscar, entre los que ya llegaron, el de menor rafaga
            Proceso elegido = null;
            for (int i = 0; i < pendientes.size(); i++) {
                Proceso p = pendientes.get(i);
                if (p.getTiempoLlegada() <= tiempoActual) {
                    if (elegido == null || p.getRafaga() < elegido.getRafaga()) {
                        elegido = p;
                    }
                }
            }

            if (elegido == null) {
                // Nadie ha llegado todavia: buscamos el proximo que llega
                Proceso proximo = pendientes.get(0);
                for (int i = 1; i < pendientes.size(); i++) {
                    if (pendientes.get(i).getTiempoLlegada() < proximo.getTiempoLlegada()) {
                        proximo = pendientes.get(i);
                    }
                }
                tiempoActual = proximo.getTiempoLlegada();
                continue;
            }

            elegido.setEstado(EstadoProceso.LISTO);
            validarMemoria(elegido, mensajes, tiempoActual);

            int inicio = tiempoActual;
            elegido.setTiempoInicio(inicio);
            elegido.setEstado(EstadoProceso.EJECUCION);

            int fin = inicio + elegido.getRafaga();
            gantt.add(new BloqueGantt(elegido.getPid(), elegido.getNombre(), inicio, fin));

            tiempoActual = fin;
            elegido.setTiempoRestante(0);
            elegido.setTiempoFin(fin);
            elegido.setEstado(EstadoProceso.TERMINADO);
            memoria.liberar(elegido);

            pendientes.remove(elegido);
            terminados.add(elegido);
        }
        return new ResultadoSimulacion(gantt, terminados, mensajes, tiempoActual);
    }

    // ---------------------------------------------------------
    // Round Robin: cada proceso usa la CPU un maximo de "quantum"
    // de tiempo y luego vuelve al final de la cola si le falta.
    // ---------------------------------------------------------
    private ResultadoSimulacion ejecutarRoundRobin(List<Proceso> procesos, int quantum) {
        if (quantum <= 0) quantum = 1;

        List<Proceso> orden = ordenarPorLlegada(procesos);
        ColaProcesos cola = new ColaProcesos();
        List<BloqueGantt> gantt = new ArrayList<BloqueGantt>();
        List<String> mensajes = new ArrayList<String>();

        int tiempoActual = 0;
        int indiceArribo = 0;
        int restantes = orden.size();

        // Meter a la cola los que llegan en t = 0
        while (indiceArribo < orden.size() && orden.get(indiceArribo).getTiempoLlegada() <= tiempoActual) {
            Proceso p = orden.get(indiceArribo);
            p.setEstado(EstadoProceso.LISTO);
            cola.encolar(p);
            indiceArribo++;
        }

        while (restantes > 0) {

            if (cola.estaVacia()) {
                // No hay nadie listo, avanzamos el reloj hasta el proximo que llegue
                if (indiceArribo < orden.size()) {
                    tiempoActual = orden.get(indiceArribo).getTiempoLlegada();
                    while (indiceArribo < orden.size() && orden.get(indiceArribo).getTiempoLlegada() <= tiempoActual) {
                        Proceso p = orden.get(indiceArribo);
                        p.setEstado(EstadoProceso.LISTO);
                        cola.encolar(p);
                        indiceArribo++;
                    }
                } else {
                    break;
                }
                continue;
            }

            Proceso p = cola.decolar();
            validarMemoria(p, mensajes, tiempoActual);
            p.setTiempoInicio(tiempoActual);
            p.setEstado(EstadoProceso.EJECUCION);

            int corrida = quantum;
            if (p.getTiempoRestante() < quantum) {
                corrida = p.getTiempoRestante();
            }
            int inicio = tiempoActual;
            int fin = inicio + corrida;
            gantt.add(new BloqueGantt(p.getPid(), p.getNombre(), inicio, fin));

            tiempoActual = fin;
            p.setTiempoRestante(p.getTiempoRestante() - corrida);

            // Meter a la cola los procesos que llegaron durante esta rafaga
            while (indiceArribo < orden.size() && orden.get(indiceArribo).getTiempoLlegada() <= tiempoActual) {
                Proceso nuevo = orden.get(indiceArribo);
                nuevo.setEstado(EstadoProceso.LISTO);
                cola.encolar(nuevo);
                indiceArribo++;
            }

            if (p.getTiempoRestante() > 0) {
                p.setEstado(EstadoProceso.LISTO);
                cola.encolar(p);
            } else {
                p.setTiempoFin(tiempoActual);
                p.setEstado(EstadoProceso.TERMINADO);
                memoria.liberar(p);
                restantes--;
            }
        }

        return new ResultadoSimulacion(gantt, orden, mensajes, tiempoActual);
    }

    private void validarMemoria(Proceso p, List<String> mensajes, int tiempoActual) {
        if (!memoria.tieneMemoria(p.getPid())) {
            boolean ok = memoria.asignar(p);
            if (!ok) {
                if (!p.isAdvertenciaMemoria()) {
                    mensajes.add("[t=" + tiempoActual + "] Advertencia: " + p + " necesita "
                            + p.getMemoriaRequerida() + " KB pero solo hay " + memoria.getLibreKB() + " KB libres.");
                }
                p.setAdvertenciaMemoria(true);
            } else {
                p.setAdvertenciaMemoria(false);
            }
        }
    }
}

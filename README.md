# Simulador Visual de Procesos
Trabajo de Investigacion / Programacion II
Estructura del codigo fuente/
├ ├── Main.java                    -> Punto de entrada
├ ├── modelo/
      ├── Proceso.java             -> PCB (Estructura de datos)
      └── EstadoProcesos.java      -> Enum Ciclo de vida
├ ├── controlador/
      ├── Planificador.java        -> Algoritmos FCFS / SJF / Round Robin
      ├── GestorMemoria.java       -> RAM simulada (asignación/liberación)
      ├── ColaProcesos.java        -> nodos enlazados
      ├── BloqueGantt.java         -> Tramo de tiempo para el diagrama
      └── ResultadoSimulacion.java -> Salida de la simulación + métricas
├ ├── persistencia/
      └── Gestor de archivo.java   -> Guardar/cargar procesos.txt
├ ├── vista/
      ├── VentanaGrafica.java      -> Ventana Principal
      ├── PanelGantt.java          -> Diagrama animado

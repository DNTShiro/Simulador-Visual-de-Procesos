# Simulador Visual de Procesos
Trabajo de Investigacion / Programacion II
Estructura del codigo fuente/
├ ├── Main.java                    -> Punto de entrada
├ ├── modelo/
      ├── Proceso.java             -> PCB (Estructura de datos)
├ ├── controlador/  
      ├── Planificador             -> Algoritmos (
      ├── GestorMemoria            ->
├── controlador/
 │    ├── Planificador.java        -> Algoritmos FCFS / SJF / Round Robin
 │    ├── GestorMemoria.java       -> RAM simulada (asignación/liberación)
 │    ├── BloqueGantt.java         -> Tramo de tiempo para el diagrama
 │    └── ResultadoSimulacion.java -> Salida de la simulación + métricas

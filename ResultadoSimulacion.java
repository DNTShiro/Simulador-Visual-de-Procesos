import modelo.Proceso;
import java.utilList;

public call ResultadoSimulacion {
  public List<BlloqueGantt> LineaTiempo;
  public List<Proceso> procesos;
  public List<String> mensajes;
  public int tiempoTotal;

  public ResultadoSimuladocion(List<BloqueGantt> lineaTiempo, List<Proceso> procesos;List<String> mensajes, int tiempoTotal) {
    this.lineaTiempo = lineaTiempo ;
    this.procesos = procesos ;
    this.mensajes = mensajes;
    this.tiempoTotal = tiempoTotal;
  }
  public double getEsperaPromedio() {
    int suma = 0;
    for (int i = 0; i < procesos.size(); i++){
      suma = suma + procesos.get(i).getTiempoEspera();
    }
    return (double) suma / procesos.size();
  }
  public double getRetornoPromedio() {
    int suma = 0;
    for (int i = 0; i < procesos.size(); i++){
      suma = suma + procesos.get(i).getTiempoRetorno();
    }
    return (double) suma / procesos.size();
  }
}    

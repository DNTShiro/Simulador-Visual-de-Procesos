import modelo.EstadoProceso;
import modelo.Proceso;

import java.util.ArrayList;
import java.util.List;


public class GeneradorVisual{
   public ResultadoSimulacion generar(List<Proceso> procesos){
      // reiniciar procesos para poder simular de nuevo
      for (int i= 0; i <procesos.size(); i++) {
          procesos.get(i).reiniciar();
      }
   List<BloqueGantt> gant = new ArrayList <BloqueGentt>();
   List<String> mensajes = new ArrayList<String>();
   int tiempoActual = 0;

   // ---placeholder: solo ejecuta en el orden de la Lista---
   for (int i = 0; i < procesos.size(); i++){
       proceso p = procesos.get(1);

           if (p.getTiempoLlegada() > tiempoActual){
               tiempoActual = p.getTiempoLLegada();
           }

           p.setEstado(EstadoProceso.LISTO);

           int inicio = tiempoActual;
           p.setTiempoInicio(inicio);
           p.setEstado(EstadoProceso.EJECUCION);

           int fin = inicio + p.getRafaga();
           grantt.add(newBloqueGantt(p.gettid(), p.getNombre();inicioo,fin)));

           tiempoActual = fin;
           p.setTiempoRestante(0);
           p.setTiempoFin(fin);
           p.setEstdo(EstadoProceso.TERMINADO);

           }
         return new ResultadoSimulacion( gantt, procesos, mensajes, tiempoActual);
      }
}

package persistencia;

import modelo.Proceso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Guarda y carga la lista de procesos en un archivo de texto plano,
 * para que el usuario no tenga que volver a escribir todo cada vez.
 * Cada linea del archivo tiene el formato:
 *   pid;nombre;llegada;rafaga;prioridad;memoria
 */
public class GestorArchivos {

    public void guardar(File archivo, List<Proceso> procesos) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        bw.write("PID;NOMBRE;LLEGADA;RAFAGA;PRIORIDAD;MEMORIA_KB");
        bw.newLine();
        for (int i = 0; i < procesos.size(); i++) {
            Proceso p = procesos.get(i);
            bw.write(p.getPid() + ";" + p.getNombre() + ";" + p.getTiempoLlegada() + ";"
                    + p.getRafaga() + ";" + p.getPrioridad() + ";" + p.getMemoriaRequerida());
            bw.newLine();
        }
        bw.close();
    }

    public List<Proceso> cargar(File archivo) throws IOException {
        List<Proceso> procesos = new ArrayList<Proceso>();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea = br.readLine(); // saltar la cabecera
        while ((linea = br.readLine()) != null) {
            if (linea.trim().length() == 0) continue;
            String[] campos = linea.split(";");
            if (campos.length < 6) continue;
            int pid = Integer.parseInt(campos[0].trim());
            String nombre = campos[1].trim();
            int llegada = Integer.parseInt(campos[2].trim());
            int rafaga = Integer.parseInt(campos[3].trim());
            int prioridad = Integer.parseInt(campos[4].trim());
            int memoria = Integer.parseInt(campos[5].trim());
            procesos.add(new Proceso(pid, nombre, llegada, rafaga, prioridad, memoria));
        }
        br.close();
        return procesos;
    }

    public void guardarReporte(File archivo, String contenido) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        bw.write(contenido);
        bw.close();
    }
}

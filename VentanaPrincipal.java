import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Ventana principal del simulador. 
 */
public class VentanaPrincipal extends JFrame implements ActionListener {
    
    //Lista de procesos que se van a simular
    List<Proceso> procesos = new ArrayList<Proceso>();
    int siguienteID = 1;
    
    //tabla de procesos (con DefaultTableModel)
    String[] columnas = {"ID", "Nombre", "Llegada", "Rafaga", "Prioridad", "Mem(KB)", "Estado", "Restante", "Espera", "Retorno"};
    DefaultTableModel modeloTabla = new DefaultTableModel(columnas,0);
    JTable tablaProcesos = new JTable(modeloTabla);
    
    //Controladores
    
    public VentanaPrincipal() {
        setTitle("Simulador de Planificacion de CPU");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
}

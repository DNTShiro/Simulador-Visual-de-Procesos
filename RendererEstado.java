import modelo.EstadoProceso;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * Colorea cada fila de la tabla de procesos segun su estado:
 *   Ejecutando -> verde   Listo -> azul   Esperando RAM -> amarillo
 *   Nuevo -> blanco/gris  Terminado -> rojo
 *
 * Se aplica a TODA la fila (no solo a la celda de Estado) para que se
 * note de un vistazo, con el texto en negro para que se lea bien sobre
 * los colores claros.
 */
public class RendererEstado extends DefaultTableCellRenderer {

    // Indice de la columna "Estado" dentro del modelo de la tabla
    private static final int COLUMNA_ESTADO = 6;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Object valorEstado = table.getModel().getValueAt(row, COLUMNA_ESTADO);
        EstadoProceso estado = mapear(String.valueOf(valorEstado));
        Color color = Tema.colorDeEstado(estado);

        setBackground(isSelected ? color.darker() : color);
        setForeground(Color.BLACK);
        setHorizontalAlignment(column == COLUMNA_ESTADO ? CENTER : LEFT);
        return c;
    }

    // La tabla guarda el estado como texto ("Ejecucion", "Listo", etc.),
    // asi que lo traducimos de vuelta al enum para buscar su color.
    private EstadoProceso mapear(String texto) {
        if (texto.equals("Ejecucion")) return EstadoProceso.EJECUCION;
        if (texto.equals("Listo")) return EstadoProceso.LISTO;
        if (texto.equals("Espera (RAM)")) return EstadoProceso.ESPERA_MEMORIA;
        if (texto.equals("Terminado")) return EstadoProceso.TERMINADO;
        return EstadoProceso.NUEVO;
    }
}

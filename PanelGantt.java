package vista;

import controlador.BloqueGantt;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;

/**
 * Dibuja el Diagrama de Gantt de la simulacion: bloques de colores (uno
 * por proceso) en una linea de tiempo, mas un cursor rojo que muestra
 * el instante actual mientras se anima la simulacion.
 */
public class PanelGantt extends JPanel {

    private static final int ALTO_BARRA = 50;
    private static final int MARGEN_IZQ = 50;
    private static final int Y_BARRA = 50;
    private static final int PIXELES_POR_UNIDAD = 40;

    // Paleta de colores fija y sencilla (se reparte segun el PID)
    private static final Color[] COLORES = {
            Color.CYAN, Color.ORANGE, Color.GREEN, Color.PINK,
            Color.YELLOW, Color.MAGENTA, Color.LIGHT_GRAY, Color.RED
    };

    private List<BloqueGantt> bloques = new ArrayList<BloqueGantt>();
    private int tiempoTotal = 0;
    private int tiempoActual = 0;

    public void setDatos(List<BloqueGantt> bloques, int tiempoTotal) {
        this.bloques = bloques;
        this.tiempoTotal = tiempoTotal;
        if (this.tiempoTotal < 1) this.tiempoTotal = 1;
        this.tiempoActual = 0;

        int ancho = MARGEN_IZQ + this.tiempoTotal * PIXELES_POR_UNIDAD + 60;
        int alto = Y_BARRA + ALTO_BARRA + 100;
        setPreferredSize(new java.awt.Dimension(Math.max(ancho, 400), alto));
        revalidate();
        repaint();
    }

    public void setTiempoActual(int t) {
        this.tiempoActual = t;
        repaint();
    }

    private Color colorDe(int pid) {
        return COLORES[pid % COLORES.length];
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bloques.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("Aun no se ha ejecutado ninguna simulacion.", 20, 30);
            return;
        }

        g.setColor(Color.DARK_GRAY);
        g.drawString("Diagrama de Gantt - tiempo actual: " + tiempoActual, 10, 20);

        // Dibujar cada bloque de ejecucion
        for (int i = 0; i < bloques.size(); i++) {
            BloqueGantt b = bloques.get(i);
            int x = MARGEN_IZQ + b.inicio * PIXELES_POR_UNIDAD;
            int ancho = (b.fin - b.inicio) * PIXELES_POR_UNIDAD;

            boolean ejecutandoAhora = (b.inicio <= tiempoActual && tiempoActual < b.fin);

            g.setColor(colorDe(b.pid));
            g.fillRect(x, Y_BARRA, ancho, ALTO_BARRA);

            g.setColor(ejecutandoAhora ? Color.RED : Color.BLACK);
            g.drawRect(x, Y_BARRA, ancho, ALTO_BARRA);

            String etiqueta = "P" + b.pid;
            FontMetrics fm = g.getFontMetrics();
            int tx = x + (ancho - fm.stringWidth(etiqueta)) / 2;
            g.setColor(Color.BLACK);
            g.drawString(etiqueta, tx, Y_BARRA + ALTO_BARRA / 2 + 5);

            g.drawString("" + b.inicio, x - 3, Y_BARRA + ALTO_BARRA + 15);
        }

        int xFinal = MARGEN_IZQ + tiempoTotal * PIXELES_POR_UNIDAD;
        g.setColor(Color.BLACK);
        g.drawString("" + tiempoTotal, xFinal - 3, Y_BARRA + ALTO_BARRA + 15);

        // Cursor del reloj de simulacion
        int xCursor = MARGEN_IZQ + tiempoActual * PIXELES_POR_UNIDAD;
        g.setColor(Color.RED);
        g.drawLine(xCursor, Y_BARRA - 10, xCursor, Y_BARRA + ALTO_BARRA + 10);
    }
}

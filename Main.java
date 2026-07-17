package vista;

import controlador.BloqueGantt;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.ArrayList;

/**
 * Dibuja el Diagrama de Gantt de la simulacion en tema oscuro: bloques
 * de colores (uno por proceso) sobre una linea de tiempo, con un cursor
 * que avanza mientras se anima la simulacion.
 */
public class PanelGantt extends JPanel {

    private static final int ALTO_BARRA = 46;
    private static final int MARGEN_IZQ = 40;
    private static final int Y_REGLA = 30;
    private static final int Y_BARRA = 46;
    private static final int PIXELES_POR_UNIDAD = 42;

    private static final Color[] COLORES = {
            new Color(0, 200, 190), new Color(52, 152, 219), new Color(155, 89, 182),
            new Color(230, 126, 34), new Color(46, 204, 113), new Color(241, 196, 15),
            new Color(231, 76, 60), new Color(149, 165, 166)
    };

    private List<BloqueGantt> bloques = new ArrayList<BloqueGantt>();
    private int tiempoTotal = 0;
    private int tiempoActual = 0;

    public PanelGantt() {
        setBackground(Tema.FONDO_PANEL);
    }

    public void setDatos(List<BloqueGantt> bloques, int tiempoTotal) {
        this.bloques = bloques;
        this.tiempoTotal = tiempoTotal;
        if (this.tiempoTotal < 1) this.tiempoTotal = 1;
        this.tiempoActual = 0;

        int ancho = MARGEN_IZQ + this.tiempoTotal * PIXELES_POR_UNIDAD + 40;
        int alto = Y_BARRA + ALTO_BARRA + 60;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(Tema.FUENTE_MONO);

        if (bloques.isEmpty()) {
            g2.setColor(Tema.TEXTO_TENUE);
            g2.drawString("Aun no se ha ejecutado ninguna simulacion.", 20, 30);
            return;
        }

        // Regla de tiempo (numeros arriba)
        g2.setColor(Tema.TEXTO_TENUE);
        for (int t = 0; t <= tiempoTotal; t++) {
            int x = MARGEN_IZQ + t * PIXELES_POR_UNIDAD;
            g2.drawString(String.valueOf(t), x - 3, Y_REGLA);
        }
        g2.setColor(new Color(50, 55, 60));
        g2.drawLine(MARGEN_IZQ, Y_BARRA - 4, MARGEN_IZQ + tiempoTotal * PIXELES_POR_UNIDAD, Y_BARRA - 4);

        // Bloques de ejecucion
        for (int i = 0; i < bloques.size(); i++) {
            BloqueGantt b = bloques.get(i);
            int x = MARGEN_IZQ + b.inicio * PIXELES_POR_UNIDAD;
            int ancho = (b.fin - b.inicio) * PIXELES_POR_UNIDAD;

            boolean ejecutandoAhora = (b.inicio <= tiempoActual && tiempoActual < b.fin);
            Color base = colorDe(b.pid);

            g2.setColor(ejecutandoAhora ? base : base.darker().darker());
            g2.fillRoundRect(x + 2, Y_BARRA, ancho - 4, ALTO_BARRA, 6, 6);

            g2.setColor(ejecutandoAhora ? Color.WHITE : base.darker());
            g2.drawRoundRect(x + 2, Y_BARRA, ancho - 4, ALTO_BARRA, 6, 6);

            String etiqueta = "P" + b.pid;
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (ancho - fm.stringWidth(etiqueta)) / 2;
            g2.setColor(ejecutandoAhora ? Color.BLACK : Tema.TEXTO);
            g2.drawString(etiqueta, tx, Y_BARRA + ALTO_BARRA / 2 + 5);
        }

        // Cursor del reloj de simulacion
        int xCursor = MARGEN_IZQ + tiempoActual * PIXELES_POR_UNIDAD;
        g2.setColor(Tema.ACENTO);
        g2.drawLine(xCursor, Y_REGLA + 4, xCursor, Y_BARRA + ALTO_BARRA + 8);
    }
}

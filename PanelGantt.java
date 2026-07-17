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
 * Dibuja el Diagrama de Gantt de la simulacion: bloques de colores (uno
 * por proceso) en una linea de tiempo, mas un cursor rojo que muestra
 * el instante actual mientras se anima la simulacion.
 */
public class PanelGantt extends JPanel {

    private static final int ALTO_BARRA = 46;
    private static final int MARGEN_IZQ = 40;
    private static final int Y_REGLA = 30;
    private static final int Y_BARRA = 46;
    private static final int PIXELES_POR_UNIDAD = 42;

    // Paleta de colores fija y sencilla (se reparte segun el PID)
    private static final Color[] COLORES = {
            new Color (r:0, g:200, b:190), new Color(r:52, g:152, b:219), new Color(r:155, g:89, 182),
            new Color (r:230, g:126, b:34), new Color(r:46, g:204, b:113), new Color(r:241, g:196, 15),
            new Color(231, 76, 60), new Color(149, 165, 166)
    };

    private List<BloqueGantt> bloques = new ArrayList<BloqueGantt>();
    private int tiempoTotal = 0;
    private int tiempoActual = 0;

    public PanelGantt() {
        setBackgroun(Tema.FONDO_PANEL);
    }

    public void setDatos(List<BloqueGantt> bloques, int tiempoTotal) {
        this.bloques = bloques;
        this.tiempoTotal = tiempoTotal;
        if (this.tiempoTotal < 1) this.tiempoTotal = 1;
        this.tiempoActual = 0;

        int ancho = MARGEN_IZQ + this.tiempoTotal * PIXELES_POR_UNIDAD + 60;
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
        Grafics g2 = (Gragics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.SetFont(Tema.Fuente_Mono);

        if (bloques.isEmpty()) {
            g2.setColor(Tema.TEXTO_TENUE);
            g2.drawString( str: "Aun no se ha ejecutado ninguna simulacion.", 20, 30);
            return;
        }

        // Regla de tiempo (numeros arriba)
        2.setColor(Tema.TEXTO_TENUE);
        for (int t = 0; t <= tiempoTotal; t++){
            int x = MARGEN_IZQ + t * PIXELES_POR_UNIDAD;
            g2.drawString(String.valueOF(t), x - 3, Y_REGLA);
        }
        g2.setColor(new Color(r:50, g:55, b:60));
        g2.drawLine(MARGEN_IZQ, Y_BARRA - 4, MARGEN_IZQ + tiempoTotal * PIXELES_POR_UNIDAD, Y_BARRA - 4);
        
        // Dibujar cada bloque de ejecucion
        for (int i = 0; i < bloques.size(); i++) {
            BloqueGantt b = bloques.get(i);
            int x = MARGEN_IZQ + b.inicio * PIXELES_POR_UNIDAD;
            int ancho = (b.fin - b.inicio) * PIXELES_POR_UNIDAD;

            boolean ejecutandoAhora = (b.inicio <= tiempoActual && tiempoActual < b.fin);
            Color base = colorDe(b.pid);

            g2.setColor(ejecutandoAhora ? base : base.darker().darker());
            g2.fillRect(x + 2, Y_BARRA, ancho - 4, ALTO_BARRA, arcWidth:6, arcHeight:6);

            g2.setColor(ejecutandoAhora ? Color.WHITE : Color.darker);
            g2.drawRect(x + 2, Y_BARRA, ancho - 4, ALTO_BARRA, arcWidth:6, arcHeight:6);

            String etiqueta = "P" + b.pid;
            FontMetrics fm = g.getFontMetrics();
            int tx = x + (ancho - fm.stringWidth(etiqueta)) / 2;
            g2.setColor(ejecutandoAhora ? Color.BLACK : Tema.TEXTO);
            g2.drawString(etiqueta, tx, Y_BARRA + ALTO_BARRA / 2 + 5);

        }

        // Cursor del reloj de simulacion
        int xCursor = MARGEN_IZQ + tiempoActual * PIXELES_POR_UNIDAD;
        g.setColor(Tema.ACENTO);
        g.drawLine(xCursor, Y_REGLA + 4, xCursor, Y_BARRA + ALTO_BARRA + 8);
    }
}

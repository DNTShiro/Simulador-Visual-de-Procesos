package vista;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Barra de progreso simple dibujada a mano con Java 2D (Graphics2D).
 * Se usa tanto para el uso de CPU como para el uso de RAM, solo cambia
 * el color de relleno y el texto que se muestra a la derecha.
 */
public class PanelBarra extends JPanel {

    private int valor = 0;
    private int maximo = 100;
    private String etiqueta = "";
    private Color colorRelleno = Tema.ACENTO;

    public PanelBarra() {
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(200, 26));
    }

    public void setValores(int valor, int maximo, String etiqueta, Color colorRelleno) {
        this.valor = valor;
        this.maximo = Math.max(maximo, 1);
        this.etiqueta = etiqueta;
        this.colorRelleno = colorRelleno;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        int barraAlto = alto - 6;

        // Fondo de la Barra (vacio)
        g2.setColor(new Color( 35, 40, 45));
        g2.fillRoundRect(0, 0, ancho, barraAlto, 6, 6);

        // Relleno proporcional al valor
        double proporcion = Math.min( 1.0, (double) valor / maximo);
        int anchoRelleno = (int) (ancho * proporcion);
        g2.setColor(colorRelleno);
        g2.fillRoundRect(0, 0, anchoRelleno, barraAlto, 6, 6);

        //Borde de la barra
        g2.setColor(Tema.BORDE);
        g2.drawRoundRect(0, 0, ancho - 1, barraAlto - 1, 6, 6);

        //texto encima de la barra
       g2.setFont(new Font("Monospaced", Font.BOLD, 12));
       g2.setColor(Color.WHITE);
       g2.drawString(etiqueta, 8, barraAlto - 7);
    }
}
      
      

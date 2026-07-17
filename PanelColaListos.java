package com.mycompany.vista;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Dibuja la cola de procesos "Listos" como una fila de cajas unidas con
 * flechas, tal como se ve en una cola FIFO: [P2] -> [P4] -> [P6] -> ...
 */
public class PanelColaListos extends JPanel {

    private List<String> etiquetas = new ArrayList<String>();

    public PanelColaListos() {
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(400, 60));
    }

    public void setProcesos(List<String> etiquetas) {
        this.etiquetas = etiquetas;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(Tema.FUENTE_MONO_NEGRITA);

        if (etiquetas.isEmpty()) {
            g2.setColor(Tema.TEXTO_TENUE);
            g2.drawString("(vacia)", 10, 30);
            return;
        }

        int x = 10;
        int y = 10;
        int alto = 30;
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < etiquetas.size(); i++) {
            String texto = etiquetas.get(i);
            int ancho = fm.stringWidth(texto) + 24;

            g2.setColor(Tema.LISTO.darker());
            g2.fillRoundRect(x, y, ancho, alto, 8, 8);
            g2.setColor(Tema.LISTO);
            g2.drawRoundRect(x, y, ancho, alto, 8, 8);

            g2.setColor(Color.WHITE);
            g2.drawString(texto, x + 12, y + 20);

            x += ancho;

            if (i < etiquetas.size() - 1) {
                g2.setColor(Tema.ACENTO);
                g2.drawLine(x, y + alto / 2, x + 18, y + alto / 2);
                g2.drawLine(x + 18, y + alto / 2, x + 12, y + alto / 2 - 5);
                g2.drawLine(x + 18, y + alto / 2, x + 12, y + alto / 2 + 5);
                x += 24;
            }
        }
    }
}
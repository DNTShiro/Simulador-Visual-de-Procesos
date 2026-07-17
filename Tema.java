import java.awt.Color;
import java.awt.Font;

/**
 * Colores y fuentes del tema oscuro tipo "terminal", para que todos los
 * paneles se vean consistentes.
 */
public class Tema {
    public static final Color FONDO = new Color(10, 12, 16);
    public static final Color FONDO_PANEL = new Color(16, 19, 25);
    public static final Color BORDE = new Color(0, 200, 190);
    public static final Color TEXTO = new Color(210, 235, 235);
    public static final Color TEXTO_TENUE = new Color(120, 150, 150);
    public static final Color ACENTO = new Color(0, 230, 210);

    // Colores de estado (los que pediste)
    public static final Color EJECUCION = new Color(46, 204, 113);   // verde
    public static final Color LISTO = new Color(52, 152, 219);       // azul
    public static final Color ESPERA_MEMORIA = new Color(241, 196, 15); // amarillo
    public static final Color NUEVO = new Color(200, 200, 200);      // blanco/gris
    public static final Color TERMINADO = new Color(231, 76, 60);    // rojo

    public static final Font FUENTE_MONO = new Font("Monospaced", Font.PLAIN, 13);
    public static final Font FUENTE_MONO_NEGRITA = new Font("Monospaced", Font.BOLD, 13);
    public static final Font FUENTE_TITULO = new Font("Monospaced", Font.BOLD, 15);

    public static Color colorDeEstado(modelo.EstadoProceso estado) {
        switch (estado) {
            case EJECUCION: return EJECUCION;
            case LISTO: return LISTO;
            case ESPERA_MEMORIA: return ESPERA_MEMORIA;
            case TERMINADO: return TERMINADO;
            default: return NUEVO;
        }
    }
}


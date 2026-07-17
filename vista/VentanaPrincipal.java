package vista;

import controlador.BloqueGantt;
import controlador.GestorMemoria;
import controlador.Planificador;
import controlador.ResultadoSimulacion;
import modelo.EstadoProceso;
import modelo.Proceso;
import persistencia.GestorArchivos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
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
 * Ventana principal: dashboard oscuro estilo terminal con el estado del
 * sistema, uso de RAM, cola de listos y diagrama de Gantt, todo
 * actualizandose en vivo mientras corre la animacion.
 */
public class VentanaPrincipal extends JFrame implements ActionListener {

    private List<Proceso> procesos = new ArrayList<Proceso>();
    private int siguientePid = 1;

    private String[] columnas = {"PID", "Nombre", "Llegada", "Rafaga", "Prioridad", "Mem(KB)", "Estado", "Restante", "Espera", "Retorno"};
    private DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0);
    private JTable tablaProcesos = new JTable(modeloTabla);

    private JComboBox<String> comboAlgoritmo = new JComboBox<String>(new String[]{"FCFS", "SJF", "Round Robin"});
    private JTextField txtQuantum = new JTextField("2", 3);
    private JTextField txtMemoriaTotal = new JTextField("1024", 5);

    private JButton btnAgregar = new JButton("+ Proceso");
    private JButton btnAleatorio = new JButton("Aleatorio");
    private JButton btnEliminar = new JButton("Eliminar");
    private JButton btnGuardar = new JButton("Guardar");
    private JButton btnCargar = new JButton("Cargar");
    private JButton btnIniciar = new JButton("\u25B6 Ejecutar");
    private JButton btnReiniciar = new JButton("\u27F3 Reiniciar");

    private PanelGantt panelGantt = new PanelGantt();
    private PanelBarra barraCpu = new PanelBarra();
    private PanelBarra barraRam = new PanelBarra();
    private PanelColaListos panelColaListos = new PanelColaListos();

    private JLabel lblHora = new JLabel("Tiempo: 0");
    private JLabel lblProcesoActual = new JLabel("Proceso actual: -");
    private JLabel lblAlgoritmoActual = new JLabel("Algoritmo: -");

    private JTextArea areaLog = new JTextArea(8, 25);

    private GestorArchivos gestorArchivos = new GestorArchivos();
    private Timer timerAnimacion;
    private ResultadoSimulacion resultadoActual;
    private int relojAnimacion = 0;
    private int ticksConCpuOcupada = 0;
    private int ticksTotales = 0;

    public VentanaPrincipal() {
        setTitle("Simulador de Planificacion de CPU");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 820);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FONDO);
        setLayout(new BorderLayout());

        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirContenido(), BorderLayout.CENTER);

        configurarTabla();

        btnAgregar.addActionListener(this);
        btnAleatorio.addActionListener(this);
        btnEliminar.addActionListener(this);
        btnGuardar.addActionListener(this);
        btnCargar.addActionListener(this);
        btnIniciar.addActionListener(this);
        btnReiniciar.addActionListener(this);
    }

    // -----------------------------------------------------------
    // Construccion de la interfaz
    // -----------------------------------------------------------

    private JPanel construirEncabezado() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(Tema.FONDO);
        contenedor.setBorder(new EmptyBorder(8, 10, 4, 10));

        JLabel titulo = new JLabel("SIMULADOR DE PLANIFICACION DE CPU");
        titulo.setFont(Tema.FUENTE_TITULO);
        titulo.setForeground(Tema.ACENTO);

        JPanel filaTitulo = new JPanel(new BorderLayout());
        filaTitulo.setOpaque(false);
        filaTitulo.add(titulo, BorderLayout.WEST);
        filaTitulo.add(lblHora, BorderLayout.EAST);
        lblHora.setForeground(Tema.TEXTO_TENUE);
        lblHora.setFont(Tema.FUENTE_MONO);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        controles.setOpaque(false);
        estilizarEtiqueta(controles, "Algoritmo:");
        estilizarCombo(comboAlgoritmo);
        controles.add(comboAlgoritmo);
        estilizarEtiqueta(controles, "Quantum:");
        estilizarCampo(txtQuantum);
        controles.add(txtQuantum);
        estilizarEtiqueta(controles, "RAM (KB):");
        estilizarCampo(txtMemoriaTotal);
        controles.add(txtMemoriaTotal);

        estilizarBoton(btnIniciar, Tema.EJECUCION);
        estilizarBoton(btnReiniciar, Tema.LISTO);
        controles.add(btnIniciar);
        controles.add(btnReiniciar);

        JPanel controlesSecundarios = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        controlesSecundarios.setOpaque(false);
        JButton[] extras = {btnAgregar, btnAleatorio, btnEliminar, btnGuardar, btnCargar};
        for (JButton b : extras) {
            estilizarBoton(b, Tema.FONDO_PANEL);
            controlesSecundarios.add(b);
        }

        contenedor.add(filaTitulo);
        contenedor.add(controles);
        contenedor.add(controlesSecundarios);
        return contenedor;
    }

    private void estilizarEtiqueta(JPanel contenedor, String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Tema.TEXTO);
        l.setFont(Tema.FUENTE_MONO);
        contenedor.add(l);
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setBackground(Tema.FONDO_PANEL);
        combo.setForeground(Tema.TEXTO);
        combo.setFont(Tema.FUENTE_MONO);
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(Tema.FONDO_PANEL);
        campo.setForeground(Tema.TEXTO);
        campo.setCaretColor(Tema.TEXTO);
        campo.setFont(Tema.FUENTE_MONO);
        campo.setBorder(new LineBorder(Tema.BORDE, 1));
    }

    private void estilizarBoton(JButton boton, Color colorFondo) {
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFont(Tema.FUENTE_MONO_NEGRITA);
        boton.setFocusPainted(false);
        boton.setBorder(new LineBorder(Tema.BORDE, 1));
    }

    private JScrollPane construirContenido() {
        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Tema.FONDO);
        columna.setBorder(new EmptyBorder(6, 10, 10, 10));

        columna.add(construirFilaProcesosEstado());
        columna.add(Box.createVerticalStrut(10));
        columna.add(construirPanelSeccion("RAM", envolver(barraRam)));
        columna.add(Box.createVerticalStrut(10));
        columna.add(construirPanelSeccion("COLA DE LISTOS", envolver(panelColaListos)));
        columna.add(Box.createVerticalStrut(10));

        JScrollPane scrollGantt = new JScrollPane(panelGantt);
        scrollGantt.setBorder(null);
        scrollGantt.getViewport().setBackground(Tema.FONDO_PANEL);
        scrollGantt.setPreferredSize(new Dimension(1000, 160));
        JPanel panelGanttSeccion = construirPanelSeccion("DIAGRAMA DE GANTT", scrollGantt);
        panelGanttSeccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        columna.add(panelGanttSeccion);
        columna.add(Box.createVerticalStrut(10));

        areaLog.setEditable(false);
        areaLog.setBackground(Tema.FONDO_PANEL);
        areaLog.setForeground(Tema.TEXTO);
        areaLog.setFont(Tema.FUENTE_MONO);
        areaLog.setCaretColor(Tema.TEXTO);
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(null);
        JPanel panelLogSeccion = construirPanelSeccion("EVENTOS", scrollLog);
        panelLogSeccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        columna.add(panelLogSeccion);

        JScrollPane scrollGeneral = new JScrollPane(columna);
        scrollGeneral.setBorder(null);
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
        scrollGeneral.getViewport().setBackground(Tema.FONDO);
        return scrollGeneral;
    }

    private JPanel construirFilaProcesosEstado() {
        JPanel fila = new JPanel(new GridLayout(1, 2, 10, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JScrollPane scrollTabla = new JScrollPane(tablaProcesos);
        scrollTabla.setBorder(null);
        scrollTabla.getViewport().setBackground(Tema.FONDO_PANEL);
        fila.add(construirPanelSeccion("PROCESOS", scrollTabla));

        JPanel panelEstado = new JPanel();
        panelEstado.setLayout(new BoxLayout(panelEstado, BoxLayout.Y_AXIS));
        panelEstado.setOpaque(false);
        panelEstado.setBorder(new EmptyBorder(6, 6, 6, 6));

        JLabel tituloCpu = new JLabel("CPU");
        tituloCpu.setForeground(Tema.TEXTO);
        tituloCpu.setFont(Tema.FUENTE_MONO_NEGRITA);
        tituloCpu.setAlignmentX(Component.LEFT_ALIGNMENT);

        barraCpu.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraCpu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        lblProcesoActual.setForeground(Tema.TEXTO);
        lblProcesoActual.setFont(Tema.FUENTE_MONO);
        lblProcesoActual.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblProcesoActual.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblAlgoritmoActual.setForeground(Tema.TEXTO);
        lblAlgoritmoActual.setFont(Tema.FUENTE_MONO);
        lblAlgoritmoActual.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAlgoritmoActual.setBorder(new EmptyBorder(6, 0, 0, 0));

        panelEstado.add(tituloCpu);
        panelEstado.add(Box.createVerticalStrut(6));
        panelEstado.add(barraCpu);
        panelEstado.add(lblProcesoActual);
        panelEstado.add(lblAlgoritmoActual);
        panelEstado.add(Box.createVerticalGlue());

        fila.add(construirPanelSeccion("ESTADO DEL SISTEMA", panelEstado));
        return fila;
    }

    private JPanel envolver(JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(6, 6, 6, 6));
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel construirPanelSeccion(String titulo, JComponent contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Tema.FONDO_PANEL);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        TitledBorder borde = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Tema.BORDE, 1), " " + titulo + " ");
        borde.setTitleColor(Tema.ACENTO);
        borde.setTitleFont(Tema.FUENTE_MONO_NEGRITA);
        panel.setBorder(borde);
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    private void configurarTabla() {
        tablaProcesos.setRowHeight(24);
        tablaProcesos.setBackground(Tema.FONDO_PANEL);
        tablaProcesos.setForeground(Tema.TEXTO);
        tablaProcesos.setFont(Tema.FUENTE_MONO);
        tablaProcesos.setGridColor(new Color(40, 45, 50));
        tablaProcesos.setSelectionBackground(Tema.ACENTO.darker());
        tablaProcesos.getTableHeader().setBackground(Tema.FONDO);
        tablaProcesos.getTableHeader().setForeground(Tema.ACENTO);
        tablaProcesos.getTableHeader().setFont(Tema.FUENTE_MONO_NEGRITA);
        tablaProcesos.setDefaultRenderer(Object.class, new RendererEstado());
    }

    // -----------------------------------------------------------
    // Eventos de botones
    // -----------------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == btnAgregar) agregarProcesoManual();
        else if (origen == btnAleatorio) agregarProcesoAleatorio();
        else if (origen == btnEliminar) eliminarSeleccionado();
        else if (origen == btnGuardar) guardarEnArchivo();
        else if (origen == btnCargar) cargarDeArchivo();
        else if (origen == btnIniciar) iniciarSimulacion();
        else if (origen == btnReiniciar) reiniciarTodo();
    }

    private void agregarProcesoManual() {
        try {
            String nombre = JOptionPane.showInputDialog(this, "Nombre del proceso:", "Proceso" + siguientePid);
            if (nombre == null) return;
            if (nombre.trim().length() == 0) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacio.");
                return;
            }
            String texto = JOptionPane.showInputDialog(this, "Tiempo de llegada:", "0");
            if (texto == null) return;
            int llegada = Integer.parseInt(texto.trim());

            texto = JOptionPane.showInputDialog(this, "Tiempo de rafaga (CPU):", "5");
            if (texto == null) return;
            int rafaga = Integer.parseInt(texto.trim());

            texto = JOptionPane.showInputDialog(this, "Prioridad (1 = mas alta):", "1");
            if (texto == null) return;
            int prioridad = Integer.parseInt(texto.trim());

            texto = JOptionPane.showInputDialog(this, "Memoria requerida (KB):", "100");
            if (texto == null) return;
            int memoria = Integer.parseInt(texto.trim());

            if (llegada < 0 || rafaga <= 0 || prioridad <= 0 || memoria <= 0) {
                JOptionPane.showMessageDialog(this, "Los valores no pueden ser negativos, y rafaga/prioridad/memoria deben ser mayores que 0.");
                return;
            }

            Proceso p = new Proceso(siguientePid, nombre.trim(), llegada, rafaga, prioridad, memoria);
            procesos.add(p);
            siguientePid++;
            refrescarTabla();
            log("Proceso agregado: " + p);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Debes escribir solo numeros.", "Dato invalido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProcesoAleatorio() {
        Random r = new Random();
        int llegada = r.nextInt(10);
        int rafaga = 1 + r.nextInt(10);
        int prioridad = 1 + r.nextInt(5);
        int memoria = 50 + r.nextInt(450);
        Proceso p = new Proceso(siguientePid, "Auto" + siguientePid, llegada, rafaga, prioridad, memoria);
        procesos.add(p);
        siguientePid++;
        refrescarTabla();
        log("Proceso aleatorio generado: " + p);
    }

    private void eliminarSeleccionado() {
        int fila = tablaProcesos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila de la tabla primero.");
            return;
        }
        Proceso p = procesos.get(fila);
        procesos.remove(fila);
        refrescarTabla();
        log("Proceso eliminado: " + p);
    }

    private void reiniciarTodo() {
        detenerAnimacion();
        for (int i = 0; i < procesos.size(); i++) procesos.get(i).reiniciar();
        refrescarTabla();
        panelGantt.setDatos(new ArrayList<BloqueGantt>(), 0);
        panelColaListos.setProcesos(new ArrayList<String>());
        barraCpu.setValores(0, 100, "0 %", Tema.EJECUCION);
        barraRam.setValores(0, 100, "0 /0 KB", Tema.LISTO);
        lblProcesoActual.setText("Proceso actual: -");
        lblHora.setText("Tiempo: 0");
        areaLog.setText("");
        log("Simulacion reiniciada.");
    }

    private void guardarEnArchivo() {
        if (procesos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay procesos para guardar.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("procesos.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                gestorArchivos.guardar(chooser.getSelectedFile(), procesos);
                log("Procesos guardados en: " + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage());
            }
        }
    }

    private void cargarDeArchivo() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                List<Proceso> cargados = gestorArchivos.cargar(chooser.getSelectedFile());
                procesos.clear();
                procesos.addAll(cargados);
                int maxPid = 0;
                for (int i = 0; i < procesos.size(); i++) {
                    if (procesos.get(i).getPid() > maxPid) maxPid = procesos.get(i).getPid();
                }
                siguientePid = maxPid + 1;
                refrescarTabla();
                panelGantt.setDatos(new ArrayList<BloqueGantt>(), 0);
                log("Se cargaron " + cargados.size() + " procesos.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se pudo leer el archivo: " + ex.getMessage());
            }
        }
    }

    // -----------------------------------------------------------
    // Simulacion
    // -----------------------------------------------------------
    private void iniciarSimulacion() {
        if (procesos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega al menos un proceso antes de simular.");
            return;
        }
        detenerAnimacion();

        int memoriaTotal;
        int quantum;
        try {
            memoriaTotal = Integer.parseInt(txtMemoriaTotal.getText().trim());
            quantum = Integer.parseInt(txtQuantum.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "RAM total y Quantum deben ser numeros.");
            return;
        }

        int algoritmoSeleccionado;
        String textoAlgoritmo = (String) comboAlgoritmo.getSelectedItem();
        if (textoAlgoritmo.equals("FCFS")) algoritmoSeleccionado = Planificador.FCFS;
        else if (textoAlgoritmo.equals("SJF")) algoritmoSeleccionado = Planificador.SJF;
        else algoritmoSeleccionado = Planificador.ROUND_ROBIN;

        GestorMemoria memoria = new GestorMemoria(memoriaTotal);
        Planificador planificador = new Planificador(memoria);
        resultadoActual = planificador.planificar(procesos, algoritmoSeleccionado, quantum);

        for (int i = 0; i < resultadoActual.mensajes.size(); i++) log(resultadoActual.mensajes.get(i));
        log("Simulacion calculada con " + textoAlgoritmo + ". Iniciando animacion...");

        panelGantt.setDatos(resultadoActual.lineaTiempo, resultadoActual.tiempoTotal);
        lblAlgoritmoActual.setText("Algoritmo: " + textoAlgoritmo);
        refrescarTabla();

        relojAnimacion = 0;
        ticksConCpuOcupada = 0;
        ticksTotales = 0;

        final int memoriaTotalFinal = memoriaTotal;
        timerAnimacion = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                actualizarEstadosEnTiempo(relojAnimacion);
                actualizarPanelesEnVivo(memoriaTotalFinal);
                panelGantt.setTiempoActual(relojAnimacion);
                refrescarTabla();

                if (relojAnimacion >= resultadoActual.tiempoTotal) {
                    timerAnimacion.stop();
                    mostrarReporteFinal();
                }
                relojAnimacion++;
            }
        });
        timerAnimacion.start();
    }

    private void actualizarEstadosEnTiempo(int t) {
        for (int i = 0; i < procesos.size(); i++) {
            Proceso p = procesos.get(i);
            if (t < p.getTiempoLlegada()) {
                p.setEstado(EstadoProceso.NUEVO);
                continue;
            }
            if (p.getTiempoFin() != -1 && t >= p.getTiempoFin()) {
                p.setEstado(EstadoProceso.TERMINADO);
                continue;
            }
            boolean enEjecucion = false;
            for (int j = 0; j < resultadoActual.lineaTiempo.size(); j++) {
                BloqueGantt b = resultadoActual.lineaTiempo.get(j);
         if (b.pid == p.getPid() && b.inicio <= t && t < b.fin) {
                enEjecucion = true;
                    break;
                }
            }
            p.setEstado(enEjecucion ? EstadoProceso.EJECUCION : EstadoProceso.LISTO);
        }
    }

    // Actualiza CPU, RAM, cola de listos y etiquetas en cada tick de la animacion
    private void actualizarPanelesEnVivo(int memoriaTotal) {
        lblHora.setText("Tiempo: " + relojAnimacion);

        Proceso enEjecucion = null;
        List<String> listos = new ArrayList<String>();
        int memoriaUsada = 0;

        for (int i = 0; i < procesos.size(); i++) {
            Proceso p = procesos.get(i);
            if (p.getEstado() == EstadoProceso.EJECUCION) enEjecucion = p;
            if (p.getEstado() == EstadoProceso.LISTO) listos.add("P" + p.getPid());
            if (p.getEstado() == EstadoProceso.EJECUCION || p.getEstado() == EstadoProceso.LISTO) {
                memoriaUsada += p.getMemoriaRequerida();
            }
        }

        ticksTotales++;
        if (enEjecucion != null) ticksConCpuOcupada++;
        int porcentajeCpu = (ticksTotales == 0) ? 0 : (int) (100.0 * ticksConCpuOcupada / ticksTotales);

        barraCpu.setValores(porcentajeCpu, 100, porcentajeCpu + " %", Tema.EJECUCION);
        lblProcesoActual.setText("Proceso actual: " + (enEjecucion != null ? "P" + enEjecucion.getPid() : "-"));

        if (memoriaUsada > memoriaTotal) memoriaUsada = memoriaTotal;
        barraRam.setValores(memoriaUsada, memoriaTotal, memoriaUsada + " /" + memoriaTotal + " KB", Tema.LISTO);

        panelColaListos.setProcesos(listos);
    }

    private void detenerAnimacion() {
        if (timerAnimacion != null && timerAnimacion.isRunning()) timerAnimacion.stop();
    }

    private void mostrarReporteFinal() {
        for (int i = 0; i < procesos.size(); i++) procesos.get(i).setEstado(EstadoProceso.TERMINADO);
        refrescarTabla();
        panelColaListos.setProcesos(new ArrayList<String>());
        lblProcesoActual.setText("Proceso actual: -");

        StringBuilder sb = new StringBuilder();
        sb.append("===== REPORTE FINAL =====\n");
        sb.append("Tiempo total de simulacion: " + resultadoActual.tiempoTotal + "\n\n");
        for (int i = 0; i < procesos.size(); i++) {
            Proceso p = procesos.get(i);
            sb.append("PID " + p.getPid() + " " + p.getNombre()
                    + "  Espera=" + p.getTiempoEspera() + "  Retorno=" + p.getTiempoRetorno()
                    + (p.isAdvertenciaMemoria() ? "  (advertencia de memoria)" : "") + "\n");
        }
        sb.append("\nTiempo de espera promedio: " + String.format("%.2f", resultadoActual.getEsperaPromedio()) + "\n");
        sb.append("Tiempo de retorno promedio: " + String.format("%.2f", resultadoActual.getRetornoPromedio()) + "\n");
        log(sb.toString());

        int opcion = JOptionPane.showConfirmDialog(this,
                "Simulacion finalizada.\nEspera promedio: " + String.format("%.2f", resultadoActual.getEsperaPromedio())
                        + "\nRetorno promedio: " + String.format("%.2f", resultadoActual.getRetornoPromedio())
                        + "\n\nDeseas guardar el reporte en un archivo?",
                "Simulacion completa", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("reporte_simulacion.txt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    gestorArchivos.guardarReporte(chooser.getSelectedFile(), sb.toString());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "No se pudo guardar el reporte: " + ex.getMessage());
                }
            }
        }
    }

    // -----------------------------------------------------------
    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < procesos.size(); i++) {
            Proceso p = procesos.get(i);
            String espera = (p.getTiempoEspera() == -1) ? "-" : String.valueOf(p.getTiempoEspera());
            String retorno = (p.getTiempoRetorno() == -1) ? "-" : String.valueOf(p.getTiempoRetorno());
            String nombreMostrado = p.getNombre() + (p.isAdvertenciaMemoria() ? " (!)" : "");

            Object[] fila = {
                    p.getPid(), nombreMostrado, p.getTiempoLlegada(), p.getRafaga(), p.getPrioridad(),
                    p.getMemoriaRequerida(), traducirEstado(p.getEstado()), p.getTiempoRestante(), espera, retorno
            };
            modeloTabla.addRow(fila);
        }
    }

    private String traducirEstado(EstadoProceso estado) {
        if (estado == EstadoProceso.NUEVO) return "Nuevo";
        if (estado == EstadoProceso.LISTO) return "Listo";
        if (estado == EstadoProceso.EJECUCION) return "Ejecucion";
        if (estado == EstadoProceso.ESPERA_MEMORIA) return "Espera (RAM)";
        return "Terminado";
    }

    private void log(String mensaje) {
        areaLog.append("* " + mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
}
Move VentanaPrincipal.java to modelo package

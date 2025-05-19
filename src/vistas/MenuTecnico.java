package vistas;

import modelo.Usuario;
import modelo.Incidencia;
import controladores.IncidenciaDAO;
import controladores.UsuarioDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MenuTecnico extends JFrame {

    private Usuario tecnico;
    private IncidenciaDAO incidenciaDAO = new IncidenciaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private DefaultTableModel modeloAsignadas;
    private JComboBox<Incidencia> comboCambiarEstado;
    private JComboBox<Incidencia> comboResolver;

    public MenuTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
        setTitle("Menú Técnico - " + tecnico.getNombre());
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Ver Asignadas", crearPanelAsignadas());
        tabs.addTab("Cambiar Estado", crearPanelCambiarEstado());
        tabs.addTab("Resolver", crearPanelResolver());

        tabs.addChangeListener(e -> {
            int i = tabs.getSelectedIndex();
            if (i == 0) {
                actualizarAsignadas();
            }
            if (i == 1) {
                cargarComboCambiarEstado();
            }
            if (i == 2) {
                cargarComboResolver();
            }
        });

        JButton btnCerrar = new JButton("Cerrar sesión");
        btnCerrar.setBackground(new Color(204, 0, 0));
    btnCerrar.setForeground(Color.WHITE);
    btnCerrar.setFocusPainted(false);
    btnCerrar.setBorderPainted(false);
    btnCerrar.setOpaque(true);

        btnCerrar.addActionListener(e -> {
            dispose(); // Cierra MenuTecnico
            new Login().setVisible(true); // Vuelve al login
        });

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.add(btnCerrar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(tabs, BorderLayout.CENTER);
        contenedor.add(panelInferior, BorderLayout.SOUTH);

        add(contenedor);
    }

    private JPanel crearPanelAsignadas() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"ID", "Título","Descripcion", "Estado", "Cliente"};
        modeloAsignadas = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloAsignadas);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        actualizarAsignadas();
        return panel;
    }

    private void actualizarAsignadas() {
        modeloAsignadas.setRowCount(0);
        List<Incidencia> lista = incidenciaDAO.obtenerAsignadasATecnico(tecnico.getId());
        for (Incidencia i : lista) {
            if (!"resuelta".equalsIgnoreCase(i.getEstado())) {
                String nombreCliente = usuarioDAO.obtenerNombrePorId(i.getClienteId());
                modeloAsignadas.addRow(new Object[]{i.getId(), i.getTitulo(),i.getDescripcion(), i.getEstado(), nombreCliente});
            }
        }
    }

    private JPanel crearPanelCambiarEstado() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        comboCambiarEstado = new JComboBox<>();
        JComboBox<String> comboEstado = new JComboBox<>();
        JButton btnCambiar = new JButton("Cambiar Estado");

        cargarComboCambiarEstado();

        // Listener que actualiza comboEstado según la incidencia seleccionada
        comboCambiarEstado.addActionListener(e -> {
            Incidencia seleccionada = (Incidencia) comboCambiarEstado.getSelectedItem();

            comboEstado.removeAllItems();

            if (seleccionada == null || seleccionada.getId() == -1) {
                // Opción inicial, deshabilitar comboEstado
                comboEstado.setEnabled(false);
            } else {
                comboEstado.setEnabled(true);
                String estadoActual = seleccionada.getEstado().toLowerCase();

                if ("asignada".equals(estadoActual)) {
                    comboEstado.addItem("en curso");
                } else if ("pendiente".equals(estadoActual)) {
                    comboEstado.addItem("en curso");
                } else if ("en curso".equals(estadoActual)) {
                    comboEstado.addItem("pendiente");
                } else {
                    // Para otros estados, solo poner el estado actual y deshabilitar para evitar cambio
                    comboEstado.addItem(seleccionada.getEstado());
                    comboEstado.setEnabled(false);
                }
            }
        });

        btnCambiar.addActionListener(e -> {
            Incidencia seleccionada = (Incidencia) comboCambiarEstado.getSelectedItem();
            if (seleccionada == null || seleccionada.getId() == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona una incidencia válida.");
                return;
            }
            String estadoNuevo = (String) comboEstado.getSelectedItem();
            if (estadoNuevo == null || estadoNuevo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona un estado válido.");
                return;
            }
            incidenciaDAO.cambiarEstado(seleccionada.getId(), estadoNuevo, tecnico.getId());
            JOptionPane.showMessageDialog(this, "Estado actualizado.");
            cargarComboCambiarEstado();
            actualizarAsignadas();
        });

        panel.add(new JLabel("Incidencia:"));
        panel.add(comboCambiarEstado);
        panel.add(new JLabel("Nuevo Estado:"));
        panel.add(comboEstado);
        panel.add(new JLabel());
        panel.add(btnCambiar);
        return panel;
    }

    private void cargarComboCambiarEstado() {
        comboCambiarEstado.removeAllItems();
        Incidencia opcionInicial = new Incidencia();
        opcionInicial.setId(-1);
        opcionInicial.setTitulo("Selecciona una incidencia");
        opcionInicial.setEstado("");
        comboCambiarEstado.addItem(opcionInicial);

        List<Incidencia> lista = incidenciaDAO.obtenerAsignadasATecnico(tecnico.getId());
        for (Incidencia i : lista) {
            if ("asignada".equalsIgnoreCase(i.getEstado()) || "en curso".equalsIgnoreCase(i.getEstado())) {
                comboCambiarEstado.addItem(i);
            }
        }

        comboCambiarEstado.setSelectedIndex(0);
    }

    private JPanel crearPanelResolver() {
        JPanel panel = new JPanel(new BorderLayout());
        comboResolver = new JComboBox<>();
        JButton btnResolver = new JButton("Resolver incidencia");

        btnResolver.addActionListener(e -> {
            Incidencia seleccionada = (Incidencia) comboResolver.getSelectedItem();
            if (seleccionada != null) {
                JTextArea areaMotivo = new JTextArea(5, 20);
                int opcion = JOptionPane.showConfirmDialog(this, new JScrollPane(areaMotivo), "Motivo de resolución",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (opcion == JOptionPane.OK_OPTION) {
                    String motivo = areaMotivo.getText();
                    incidenciaDAO.resolverIncidencia(seleccionada.getId(), motivo, tecnico.getId());
                    JOptionPane.showMessageDialog(this, "Incidencia resuelta.");
                    cargarComboResolver();
                    actualizarAsignadas();
                }
            }
        });

        JPanel arriba = new JPanel();
        arriba.add(new JLabel("Selecciona incidencia en curso:"));
        arriba.add(comboResolver);

        panel.add(arriba, BorderLayout.NORTH);
        panel.add(btnResolver, BorderLayout.SOUTH);
        return panel;
    }

    private void cargarComboResolver() {
        comboResolver.removeAllItems();
        List<Incidencia> lista = incidenciaDAO.obtenerAsignadasATecnico(tecnico.getId());
        for (Incidencia i : lista) {
            if ("en curso".equalsIgnoreCase(i.getEstado())) {
                comboResolver.addItem(i);
            }
        }
    }
}

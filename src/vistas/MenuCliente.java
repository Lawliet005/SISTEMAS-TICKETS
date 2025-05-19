package vistas;

import modelo.Usuario;
import controladores.IncidenciaDAO;
import controladores.UsuarioDAO;
import modelo.Incidencia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MenuCliente extends JFrame {

    private Usuario cliente;
    private IncidenciaDAO incidenciaDAO = new IncidenciaDAO();
    private DefaultTableModel modeloHistorial;
    private JTextArea txtResultado;
    private JTable tablaHistorial;
    private JTabbedPane tabs;

    public MenuCliente(Usuario cliente) {
        this.cliente = cliente;
        setTitle("Menú Cliente - " + cliente.getNombre());
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        tabs = new JTabbedPane();

        tabs.addTab("Crear Incidencia", crearPanelNuevaIncidencia());
        tabs.addTab("Historial", crearPanelHistorial());
        tabs.addTab("Consultar Estado", crearPanelEstado());

        tabs.addChangeListener(e -> {
            int index = tabs.getSelectedIndex();
            if (index == 1) {
                actualizarHistorial();
            }
        });

        JButton btnCerrar = new JButton("Cerrar sesión");
        
        btnCerrar.setBackground(new Color(204, 0, 0));
    btnCerrar.setForeground(Color.WHITE);
    btnCerrar.setFocusPainted(false);
    btnCerrar.setBorderPainted(false);
    btnCerrar.setOpaque(true);

        btnCerrar.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.add(btnCerrar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(tabs, BorderLayout.CENTER);
        contenedor.add(panelInferior, BorderLayout.SOUTH);

        add(contenedor);
    }

    private JPanel crearPanelNuevaIncidencia() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField txtTitulo = new JTextField();
        JTextArea txtDescripcion = new JTextArea(4, 20);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);

        JButton btnEnviar = new JButton("Enviar");

        btnEnviar.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            if (titulo.isEmpty() || descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }

            Incidencia inc = new Incidencia();
            inc.setTitulo(titulo);
            inc.setDescripcion(descripcion);
            inc.setClienteId(cliente.getId());
            inc.setFechaCreacion(LocalDateTime.now());

            boolean ok = incidenciaDAO.crearIncidencia(inc);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Incidencia registrada correctamente.");
                txtTitulo.setText("");
                txtDescripcion.setText("");
                actualizarHistorial();
                txtResultado.setText("");
                tabs.setSelectedIndex(1); // Ir directamente al historial
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar la incidencia.");
            }
        });

        form.add(new JLabel("Título:"));
        form.add(txtTitulo);
        form.add(new JLabel("Descripción:"));
        form.add(scrollDesc);
        form.add(new JLabel());
        form.add(btnEnviar);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"ID", "Título","Descipcion", "Estado", "Fecha"};
        modeloHistorial = new DefaultTableModel(columnas, 0);
        tablaHistorial = new JTable(modeloHistorial);
        panel.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);
        actualizarHistorial();
        return panel;
    }

    private void actualizarHistorial() {
        modeloHistorial.setRowCount(0);
        List<Incidencia> lista = incidenciaDAO.obtenerIncidenciasPorCliente(cliente.getId());
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (Incidencia i : lista) {
            modeloHistorial.addRow(new Object[]{
                i.getId(), i.getTitulo(),i.getDescripcion(), i.getEstado(), i.getFechaCreacion().format(formato)
            });
        }
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de controles superiores
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JRadioButton rbId = new JRadioButton("Buscar por ID");
        JRadioButton rbLista = new JRadioButton("Seleccionar de la lista");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbId);
        grupo.add(rbLista);

        JTextField txtId = new JTextField(5);
        txtId.setEnabled(false);

        JComboBox<Incidencia> comboIncidencias = new JComboBox<>();
        comboIncidencias.setEnabled(false);

        comboIncidencias.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Incidencia) {
                    Incidencia i = (Incidencia) value;
                    setText("ID " + i.getId() + " - " + i.getTitulo());
                }
                return this;
            }
        });

        JButton btnBuscar = new JButton("Consultar");

        // Área de resultado
        txtResultado = new JTextArea(5, 30);
        txtResultado.setEditable(false);
        JScrollPane scrollResultado = new JScrollPane(txtResultado);

        // Listeners para los radio buttons
        rbId.addActionListener(e -> {
            txtId.setEnabled(true);
            comboIncidencias.setEnabled(false);
        });

        rbLista.addActionListener(e -> {
            txtId.setEnabled(false);
            comboIncidencias.setEnabled(true);
            cargarIncidenciasCombo(comboIncidencias);
        });

        // Acción del botón buscar
        btnBuscar.addActionListener(e -> {
            Incidencia i = null;

            if (rbId.isSelected()) {
                try {
                    int id = Integer.parseInt(txtId.getText());
                    i = incidenciaDAO.obtenerPorIdYCliente(id, cliente.getId());
                } catch (NumberFormatException ex) {
                    txtResultado.setText("Introduce un ID válido.");
                    return;
                }
            } else if (rbLista.isSelected()) {
                i = (Incidencia) comboIncidencias.getSelectedItem();
            }

            if (i != null) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                String tecnicoNombre = (i.getTecnicoId() != null)
                        ? usuarioDAO.obtenerNombrePorId(i.getTecnicoId())
                        : "Pendiente";
                txtResultado.setText("Título: " + i.getTitulo()
                        + "\nDescripcion: " + i.getDescripcion()
                        + "\nEstado: " + i.getEstado()
                        + "\nTécnico asignado: " + tecnicoNombre
                        + "\nResolución: " + (i.getResolucion() != null ? i.getResolucion() : "Sin resolver"));
            } else {
                txtResultado.setText("No se encontró la incidencia.");
            }
        });

        // Agregar componentes al panel superior
        arriba.add(rbId);
        arriba.add(txtId);
        arriba.add(rbLista);
        arriba.add(comboIncidencias);
        arriba.add(btnBuscar);

        panel.add(arriba, BorderLayout.NORTH);
        panel.add(scrollResultado, BorderLayout.CENTER);
        return panel;
    }

    private void cargarIncidenciasCombo(JComboBox<Incidencia> combo) {
        combo.removeAllItems();
        List<Incidencia> lista = incidenciaDAO.obtenerIncidenciasPorCliente(cliente.getId());
        for (Incidencia i : lista) {
            combo.addItem(i);
        }
    }
}

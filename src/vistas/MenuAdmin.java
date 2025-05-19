package vistas;

import modelo.Usuario;
import controladores.UsuarioDAO;
import controladores.IncidenciaDAO;
import modelo.Incidencia;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MenuAdmin extends JFrame {
    private Usuario admin;
    private JTabbedPane tabbedPane;
    private UsuarioDAO usuarioDAO;
    private IncidenciaDAO incidenciaDAO;
    private DefaultTableModel modeloUsuarios;
    private DefaultTableModel modeloAsignar;
    private DefaultTableModel modeloTickets;

    public MenuAdmin(Usuario admin) {
        this.admin = admin;
        this.usuarioDAO = new UsuarioDAO();
        this.incidenciaDAO = new IncidenciaDAO();
        setTitle("Menú Administrador - " + admin.getNombre());
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        iniciarComponentes();
    }

    private void iniciarComponentes() {
    tabbedPane = new JTabbedPane();

    tabbedPane.addTab("Usuarios", crearPanelUsuarios());
    tabbedPane.addTab("Asignar Incidencias", crearPanelAsignar());
    tabbedPane.addTab("Ver Tickets", crearPanelTickets());

    tabbedPane.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0 -> actualizarTablaUsuarios(modeloUsuarios);
                case 1 -> actualizarTablaAsignar(modeloAsignar);
                case 2 -> actualizarTablaTickets(modeloTickets);
            }
        }
    });

    add(tabbedPane, BorderLayout.CENTER);

    // Panel para el botón cerrar sesión
    JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnCerrarSesion = new JButton("Cerrar Sesión");
    btnCerrarSesion.setBackground(new Color(204, 0, 0));
    btnCerrarSesion.setForeground(Color.WHITE);
    btnCerrarSesion.setFocusPainted(false);
    btnCerrarSesion.setBorderPainted(false);
    btnCerrarSesion.setOpaque(true);
    btnCerrarSesion.addActionListener(e -> {
        dispose(); // cierra esta ventana
        new Login().setVisible(true); // asumiendo que Login es tu clase de login
    });
    panelCerrar.add(btnCerrarSesion);

    add(panelCerrar, BorderLayout.SOUTH);
}

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnas = {"ID", "Nombre", "Correo", "Rol"};
        modeloUsuarios = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloUsuarios);
        JScrollPane scroll = new JScrollPane(tabla);

        actualizarTablaUsuarios(modeloUsuarios);

        JPanel acciones = new JPanel();
        JTextField txtNombre = new JTextField(10);
        JTextField txtCorreo = new JTextField(10);
        JTextField txtPass = new JTextField(10);
        JComboBox<String> comboRol = new JComboBox<>(new String[]{"cliente", "tecnico", "admin"});

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar");

        btnAgregar.addActionListener(e -> {
            Usuario nuevo = new Usuario();
            nuevo.setNombre(txtNombre.getText());
            nuevo.setCorreo(txtCorreo.getText());
            nuevo.setContraseña(txtPass.getText());
            nuevo.setRol((String) comboRol.getSelectedItem());
            usuarioDAO.insertarUsuario(nuevo);
            actualizarTablaUsuarios(modeloUsuarios);
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                int id = (int) tabla.getValueAt(fila, 0);
                usuarioDAO.eliminarUsuario(id);
                actualizarTablaUsuarios(modeloUsuarios);
            }
        });

        acciones.add(new JLabel("Nombre:")); acciones.add(txtNombre);
        acciones.add(new JLabel("Correo:")); acciones.add(txtCorreo);
        acciones.add(new JLabel("Pass:")); acciones.add(txtPass);
        acciones.add(new JLabel("Rol:")); acciones.add(comboRol);
        acciones.add(btnAgregar); acciones.add(btnEliminar);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private void actualizarTablaUsuarios(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[]{u.getId(), u.getNombre(), u.getCorreo(), u.getRol()});
        }
    }

    private JPanel crearPanelAsignar() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"ID", "Título","Descripcion", "Cliente", "Estado"};
        modeloAsignar = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloAsignar);
        JScrollPane scroll = new JScrollPane(tabla);

        JComboBox<Usuario> comboTecnico = new JComboBox<>();
        for (Usuario u : usuarioDAO.obtenerPorRol("tecnico")) {
            comboTecnico.addItem(u);
        }

        JButton btnAsignar = new JButton("Asignar Técnico");
        btnAsignar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            Usuario tecnico = (Usuario) comboTecnico.getSelectedItem();
            if (fila != -1 && tecnico != null) {
                int idIncidencia = (int) tabla.getValueAt(fila, 0);
                incidenciaDAO.asignarTecnico(idIncidencia, tecnico.getId());
                actualizarTablaAsignar(modeloAsignar);
            }
        });

        actualizarTablaAsignar(modeloAsignar);

        JPanel acciones = new JPanel();
        acciones.add(new JLabel("Técnico:"));
        acciones.add(comboTecnico);
        acciones.add(btnAsignar);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private void actualizarTablaAsignar(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        
        for (Incidencia i : incidenciaDAO.obtenerSinAsignar()) {
            modelo.addRow(new Object[]{i.getId(), i.getTitulo(),i.getDescripcion(), usuarioDAO.obtenerNombrePorId(i.getClienteId()), i.getEstado()});
        }
    }

    private JPanel crearPanelTickets() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"ID", "Título","Descripcion", "Estado", "Cliente", "Técnico", "Resolución"};
        modeloTickets = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTickets);
        JScrollPane scroll = new JScrollPane(tabla);

        actualizarTablaTickets(modeloTickets);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void actualizarTablaTickets(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (Incidencia i : incidenciaDAO.obtenerTodas()) {
            String nombreCliente = usuarioDAO.obtenerNombrePorId(i.getClienteId());
            Integer tecnicoId = i.getTecnicoId();
            String nombreTecnico = (tecnicoId != null) ? usuarioDAO.obtenerNombrePorId(tecnicoId) : "No asignado";
            modelo.addRow(new Object[]{
                i.getId(), i.getTitulo(),i.getDescripcion(), i.getEstado(),
                nombreCliente, nombreTecnico, i.getResolucion()
            });
        }
    }
}
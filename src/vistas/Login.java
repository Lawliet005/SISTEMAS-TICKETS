package vistas;

import controladores.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

    private JTextField campoCorreo;
    private JPasswordField campoPassword;
    private JButton botonLogin;
    private UsuarioDAO usuarioDAO;

    public Login() {
        setTitle("Inicio de Sesión");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Establecer icono de la aplicación
        setIconImage(new ImageIcon(getClass().getResource("/imagenes/icono.png")).getImage());

        usuarioDAO = new UsuarioDAO();
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        JLabel etiquetaCorreo = new JLabel("Correo:");
        JLabel etiquetaPassword = new JLabel("Contraseña:");
        campoCorreo = new JTextField(20);
        campoPassword = new JPasswordField(20);
        botonLogin = new JButton("Iniciar sesión");
        JButton botonCerrar = new JButton("Cerrar");

        botonLogin.addActionListener(this::iniciarSesion);
        botonCerrar.addActionListener(e -> System.exit(0)); // Cierra la aplicación

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCampos.add(etiquetaCorreo);
        panelCampos.add(campoCorreo);
        panelCampos.add(etiquetaPassword);
        panelCampos.add(campoPassword);

        JPanel panelBotones = new JPanel(new BorderLayout());
        panelBotones.add(botonCerrar, BorderLayout.WEST);
        panelBotones.add(botonLogin, BorderLayout.EAST);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.add(panelCampos, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void iniciarSesion(ActionEvent e) {
        String correo = campoCorreo.getText();
        String contraseña = new String(campoPassword.getPassword());

        Usuario usuario = usuarioDAO.obtenerUsuarioPorCorreoYContraseña(correo, contraseña);
        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + usuario.getNombre());
            this.dispose();
            switch (usuario.getRol()) {
                case "cliente":
                    new MenuCliente(usuario).setVisible(true);
                    break;
                case "tecnico":
                    new MenuTecnico(usuario).setVisible(true);
                    break;
                case "admin":
                    new MenuAdmin(usuario).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Rol desconocido");
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos");
        }
    }
}

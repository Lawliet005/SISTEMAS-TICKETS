// controladores/UsuarioDAO.java
package controladores;

import conexion.ConexionBD;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario obtenerUsuarioPorCorreoYContraseña(String correo, String contraseña) {
        String sql = "SELECT * FROM Usuario WHERE correo = ? AND contraseña = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, contraseña);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extraerUsuario(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(extraerUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public List<Usuario> obtenerPorRol(String rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Usuario WHERE rol = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rol);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usuarios.add(extraerUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuario (nombre, correo, contraseña, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getContraseña());
            stmt.setString(4, usuario.getRol());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertarUsuario(String nombre, String correo, String contraseña, String rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContraseña(contraseña);
        usuario.setRol(rol);
        return insertarUsuario(usuario);
    }

    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM Usuario WHERE id = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Usuario extraerUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setCorreo(rs.getString("correo"));
        u.setContraseña(rs.getString("contraseña"));
        u.setRol(rs.getString("rol"));
        return u;
    }

    public String obtenerNombrePorId(int id) {
        String sql = "SELECT nombre FROM Usuario WHERE id = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Desconocido";
    }
}

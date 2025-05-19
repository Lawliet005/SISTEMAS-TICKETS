// controladores/IncidenciaDAO.java
package controladores;

import conexion.ConexionBD;
import modelo.Incidencia;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDAO {

    public boolean crearIncidencia(Incidencia i) {
        String sql = "INSERT INTO Incidencia (titulo, descripcion, estado, cliente_id, fecha_creacion) VALUES (?, ?, 'pendiente', ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, i.getTitulo());
            stmt.setString(2, i.getDescripcion());
            stmt.setInt(3, i.getClienteId());
            stmt.setTimestamp(4, Timestamp.valueOf(i.getFechaCreacion()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Incidencia> obtenerIncidenciasPorCliente(int clienteId) {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM Incidencia WHERE cliente_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(extraer(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Incidencia obtenerPorIdYCliente(int id, int clienteId) {
        String sql = "SELECT * FROM Incidencia WHERE id = ? AND cliente_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, clienteId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extraer(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Incidencia> obtenerAsignadasATecnico(int tecnicoId) {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM Incidencia WHERE tecnico_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tecnicoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(extraer(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean cambiarEstado(int id, String estado, int tecnicoId) {
        String sql = "UPDATE Incidencia SET estado = ? WHERE id = ? AND tecnico_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, id);
            stmt.setInt(3, tecnicoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean resolverIncidencia(int id, String resolucion, int tecnicoId) {
        String sql = "UPDATE Incidencia SET estado = 'resuelta', resolucion = ?, fecha_resolucion = CURRENT_TIMESTAMP WHERE id = ? AND tecnico_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resolucion);
            stmt.setInt(2, id);
            stmt.setInt(3, tecnicoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Incidencia> obtenerSinAsignar() {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM Incidencia WHERE tecnico_id IS NULL";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(extraer(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean asignarTecnico(int incidenciaId, int tecnicoId) {
        String sql = "UPDATE Incidencia SET tecnico_id = ?, estado = 'asignada' WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tecnicoId);
            stmt.setInt(2, incidenciaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Incidencia> obtenerTodas() {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM Incidencia";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(extraer(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Incidencia extraer(ResultSet rs) throws SQLException {
        Incidencia i = new Incidencia();
        i.setId(rs.getInt("id"));
        i.setTitulo(rs.getString("titulo"));
        i.setDescripcion(rs.getString("descripcion"));
        i.setEstado(rs.getString("estado"));
        i.setClienteId(rs.getInt("cliente_id"));
        i.setTecnicoId(rs.getObject("tecnico_id") != null ? rs.getInt("tecnico_id") : null);
        Timestamp fc = rs.getTimestamp("fecha_creacion");
        if (fc != null) i.setFechaCreacion(fc.toLocalDateTime());
        Timestamp fr = rs.getTimestamp("fecha_resolucion");
        if (fr != null) i.setFechaResolucion(fr.toLocalDateTime());
        i.setResolucion(rs.getString("resolucion"));
        return i;
    }
}

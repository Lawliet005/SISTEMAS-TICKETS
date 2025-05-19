package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7778445?useSSL=false&serverTimezone=UTC";
    private static final String USER = "sql7778445";
    private static final String PASSWORD = "1R5sUMXlwl"; // cambia si tienes contraseña

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error de conexión a la base de datos: " + e.getMessage());
            return null;
        }
    }
}

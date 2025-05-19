
import conexion.ConexionBD;
import java.sql.Connection;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author fernando
 */
public class TestFile {
    public static void main(String[] args) {
        Connection con = ConexionBD.conectar();
        
        System.out.println("Hoal");
        
    }
}

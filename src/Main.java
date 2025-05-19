
import vistas.Login;


public class Main {
    public static void main(String[] args) {
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}

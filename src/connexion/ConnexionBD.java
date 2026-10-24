package connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionBD {

    private static final String URL      = "jdbc:mysql://localhost:3306/gestion_stock";
    private static final String USER     = "root";
    private static final String PASSWORD = "";  // vide avec XAMPP

    private static Connection connexion = null;

    public static Connection getConnexion() {
        if (connexion == null) {
            try {
                connexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion réussie à la base de données !");
            } catch (SQLException e) {
                System.out.println("Erreur de connexion : " + e.getMessage());
            }
        }
        return connexion;
    }

    public static void fermerConnexion() {
        if (connexion != null) {
            try {
                connexion.close();
                connexion = null;
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }
}
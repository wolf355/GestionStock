package dao;

import connexion.ConnexionBD;
import modele.Departement;
import modele.Utilisateur;

import java.sql.*;

public class UtilisateurDAO {

    private Connection connexion;

    public UtilisateurDAO() {
        this.connexion = ConnexionBD.getConnexion();
    }

    // ===== AUTHENTIFICATION =====
    public Utilisateur authentifier(String login,
                                    String motDePasse) {
        String sql = "SELECT u.id_utilisateur, " +
                "u.login, u.mot_de_passe, u.role, " +
                "u.id_departement as user_dept_id, " +
                "d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM utilisateur u " +
                "LEFT JOIN departement d " +
                "ON u.id_departement = d.id_departement " +
                "WHERE u.login = ? " +
                "AND u.mot_de_passe = ?";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setString(1, login);
            ps.setString(2, motDePasse);

            // DEBUG - afficher ce qu'on cherche
            System.out.println("=== DEBUG AUTH ===");
            System.out.println("Login saisi    : [" + login + "]");
            System.out.println("MDP saisi      : [" + motDePasse + "]");

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Utilisateur trouvé !");
                System.out.println("Role : " + rs.getString("role"));

                // Construire le département (peut être null si LEFT JOIN sans correspondance)
                Departement dept = null;
                int deptId = rs.getInt("user_dept_id");
                if (!rs.wasNull()) {
                    dept = new Departement();
                    dept.setIdDepartement(deptId);
                    dept.setNom(rs.getString("dept_nom"));
                    dept.setResponsable(rs.getString("responsable"));
                    dept.setDescription(rs.getString("description"));
                }

                // Construire et retourner l'utilisateur authentifié
                return new Utilisateur(
                        rs.getInt("id_utilisateur"),
                        rs.getString("login"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role"),
                        dept
                );
            } else {
                System.out.println("Aucun utilisateur trouvé !");

                // Vérifier si le login existe sans le mdp
                String sqlCheck = "SELECT login, mot_de_passe " +
                        "FROM utilisateur " +
                        "WHERE login = ?";
                PreparedStatement ps2 =
                        connexion.prepareStatement(sqlCheck);
                ps2.setString(1, login);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    System.out.println("Login trouvé mais MDP incorrect !");
                    System.out.println("MDP en base : [" +
                            rs2.getString("mot_de_passe") + "]");
                    System.out.println("MDP saisi   : [" +
                            motDePasse + "]");
                } else {
                    System.out.println("Login introuvable en base !");
                }
            }
            System.out.println("=== FIN DEBUG ===");

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return null;
    }

    // ===== AJOUTER =====
    public boolean ajouter(Utilisateur u) {
        String sql = "INSERT INTO utilisateur " +
                "(login, mot_de_passe, role, " +
                "id_departement) " +
                "VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setString(1, u.getLogin());
            ps.setString(2, u.getMotDePasse());
            ps.setString(3, u.getRole());
            if (u.getDepartement() != null) {
                ps.setInt(4,
                        u.getDepartement().getIdDepartement());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur ajouter user : " +
                    e.getMessage());
            return false;
        }
    }

    // ===== MODIFIER MOT DE PASSE =====
    public boolean modifierMotDePasse(int id,
                                      String nouveauMdp) {
        String sql = "UPDATE utilisateur " +
                "SET mot_de_passe = ? " +
                "WHERE id_utilisateur = ?";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setString(1, nouveauMdp);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur modif mdp : " +
                    e.getMessage());
            return false;
        }
    }
}
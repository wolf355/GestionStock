package dao;

import connexion.ConnexionBD;
import modele.Departement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartementDAO {

    private Connection connexion;

    public DepartementDAO() {
        this.connexion = ConnexionBD.getConnexion();
    }

    // ===== AJOUTER =====
    public boolean ajouter(Departement d) {
        String sql = "INSERT INTO departement (nom, responsable, description) " +
                "VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setString(1, d.getNom());
            ps.setString(2, d.getResponsable());
            ps.setString(3, d.getDescription());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur ajouter département : " + e.getMessage());
            return false;
        }
    }

    // ===== MODIFIER =====
    public boolean modifier(Departement d) {
        String sql = "UPDATE departement SET nom=?, responsable=?, description=? " +
                "WHERE id_departement=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setString(1, d.getNom());
            ps.setString(2, d.getResponsable());
            ps.setString(3, d.getDescription());
            ps.setInt(4, d.getIdDepartement());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur modifier département : " + e.getMessage());
            return false;
        }
    }

    // ===== SUPPRIMER =====
    public boolean supprimer(int id) {
        String sql = "DELETE FROM departement WHERE id_departement=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur supprimer département : " + e.getMessage());
            return false;
        }
    }

    // ===== FIND BY ID =====
    public Departement findById(int id) {
        String sql = "SELECT * FROM departement WHERE id_departement=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Departement(
                        rs.getInt("id_departement"),
                        rs.getString("nom"),
                        rs.getString("responsable"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur findById département : " + e.getMessage());
        }
        return null;
    }

    // ===== FIND ALL =====
    public List<Departement> findAll() {
        List<Departement> liste = new ArrayList<>();
        String sql = "SELECT * FROM departement";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                liste.add(new Departement(
                        rs.getInt("id_departement"),
                        rs.getString("nom"),
                        rs.getString("responsable"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur findAll département : " + e.getMessage());
        }
        return liste;
    }
}
package dao;

import connexion.ConnexionBD;
import modele.Departement;
import modele.TypeMateriel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeMaterielDAO {

    private Connection connexion;

    public TypeMaterielDAO() {
        this.connexion = ConnexionBD.getConnexion();
    }

    // ===== AJOUTER =====
    public boolean ajouter(TypeMateriel t) {
        String sql = "INSERT INTO type_materiel (libelle, id_departement) " +
                "VALUES (?, ?)";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setString(1, t.getLibelle());
            ps.setInt(2, t.getDepartement().getIdDepartement());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur ajouter type matériel : " + e.getMessage());
            return false;
        }
    }

    // ===== MODIFIER =====
    public boolean modifier(TypeMateriel t) {
        String sql = "UPDATE type_materiel SET libelle=?, id_departement=? " +
                "WHERE id_type=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setString(1, t.getLibelle());
            ps.setInt(2, t.getDepartement().getIdDepartement());
            ps.setInt(3, t.getIdType());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur modifier type matériel : " + e.getMessage());
            return false;
        }
    }

    // ===== SUPPRIMER =====
    public boolean supprimer(int id) {
        String sql = "DELETE FROM type_materiel WHERE id_type=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur supprimer type matériel : " + e.getMessage());
            return false;
        }
    }

    // ===== FIND BY ID =====
    public TypeMateriel findById(int id) {
        String sql = "SELECT tm.*, d.nom as dept_nom, d.responsable, d.description " +
                "FROM type_materiel tm " +
                "JOIN departement d ON tm.id_departement = d.id_departement " +
                "WHERE tm.id_type=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Departement dept = new Departement(
                        rs.getInt("id_departement"),
                        rs.getString("dept_nom"),
                        rs.getString("responsable"),
                        rs.getString("description")
                );
                return new TypeMateriel(
                        rs.getInt("id_type"),
                        rs.getString("libelle"),
                        dept
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur findById type matériel : " + e.getMessage());
        }
        return null;
    }

    // ===== FIND ALL =====
    public List<TypeMateriel> findAll() {
        List<TypeMateriel> liste = new ArrayList<>();
        String sql = "SELECT tm.*, d.nom as dept_nom, d.responsable, d.description " +
                "FROM type_materiel tm " +
                "JOIN departement d ON tm.id_departement = d.id_departement";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Departement dept = new Departement(
                        rs.getInt("id_departement"),
                        rs.getString("dept_nom"),
                        rs.getString("responsable"),
                        rs.getString("description")
                );
                liste.add(new TypeMateriel(
                        rs.getInt("id_type"),
                        rs.getString("libelle"),
                        dept
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur findAll type matériel : " + e.getMessage());
        }
        return liste;
    }

    // ===== FIND BY DEPARTEMENT =====
    public List<TypeMateriel> findByDepartement(int idDepartement) {
        List<TypeMateriel> liste = new ArrayList<>();
        String sql = "SELECT tm.*, d.nom as dept_nom, d.responsable, d.description " +
                "FROM type_materiel tm " +
                "JOIN departement d ON tm.id_departement = d.id_departement " +
                "WHERE tm.id_departement=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, idDepartement);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Departement dept = new Departement(
                        rs.getInt("id_departement"),
                        rs.getString("dept_nom"),
                        rs.getString("responsable"),
                        rs.getString("description")
                );
                liste.add(new TypeMateriel(
                        rs.getInt("id_type"),
                        rs.getString("libelle"),
                        dept
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur findByDepartement : " + e.getMessage());
        }
        return liste;
    }
}
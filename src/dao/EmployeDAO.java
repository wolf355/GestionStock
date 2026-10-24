package dao;

import connexion.ConnexionBD;
import modele.Departement;
import modele.Employe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeDAO {

    private Connection connexion;

    public EmployeDAO() {
        this.connexion = ConnexionBD.getConnexion();
    }

    // ===== AJOUTER =====
    public boolean ajouter(Employe e) {
        String sql = "INSERT INTO employe (nax, nom, prenom, " +
                "email, telephone, poste, " +
                "date_naissance, date_embauche, " +
                "id_departement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setString(1, e.getNax());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenom());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getTelephone());
            ps.setString(6, e.getPoste());
            ps.setDate(7, e.getDateNaissance() != null ?
                    new java.sql.Date(
                            e.getDateNaissance().getTime()) : null);
            ps.setDate(8, e.getDateEmbauche() != null ?
                    new java.sql.Date(
                            e.getDateEmbauche().getTime()) : null);
            ps.setInt(9,
                    e.getDepartement().getIdDepartement());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("Erreur ajouter employé : " +
                    ex.getMessage());
            return false;
        }
    }

    // ===== MODIFIER =====
    public boolean modifier(Employe e) {
        String sql = "UPDATE employe SET nax=?, nom=?, " +
                "prenom=?, email=?, telephone=?, " +
                "poste=?, date_naissance=?, " +
                "date_embauche=?, id_departement=? " +
                "WHERE id_employe=?";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setString(1, e.getNax());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenom());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getTelephone());
            ps.setString(6, e.getPoste());
            ps.setDate(7, e.getDateNaissance() != null ?
                    new java.sql.Date(
                            e.getDateNaissance().getTime()) : null);
            ps.setDate(8, e.getDateEmbauche() != null ?
                    new java.sql.Date(
                            e.getDateEmbauche().getTime()) : null);
            ps.setInt(9,
                    e.getDepartement().getIdDepartement());
            ps.setInt(10, e.getIdEmploye());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.out.println("Erreur modifier employé : " +
                    ex.getMessage());
            return false;
        }
    }

    // ===== SUPPRIMER =====
    public boolean supprimer(int id) {
        String sql = "DELETE FROM employe " +
                "WHERE id_employe=?";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur supprimer employé : " +
                    e.getMessage());
            return false;
        }
    }

    // ===== FIND BY ID =====
    public Employe findById(int id) {
        String sql = "SELECT e.*, d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM employe e " +
                "JOIN departement d " +
                "ON e.id_departement = d.id_departement " +
                "WHERE e.id_employe=?";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return buildEmploye(rs);
        } catch (SQLException e) {
            System.out.println("Erreur findById : " +
                    e.getMessage());
        }
        return null;
    }

    // ===== FIND BY NAX =====
    public Employe findByNax(String nax) {
        String sql = "SELECT e.*, d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM employe e " +
                "JOIN departement d " +
                "ON e.id_departement = d.id_departement " +
                "WHERE e.nax=?";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setString(1, nax);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return buildEmploye(rs);
        } catch (SQLException e) {
            System.out.println("Erreur findByNax : " +
                    e.getMessage());
        }
        return null;
    }

    // ===== FIND ALL =====
    public List<Employe> findAll() {
        List<Employe> liste = new ArrayList<>();
        String sql = "SELECT e.*, d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM employe e " +
                "JOIN departement d " +
                "ON e.id_departement = d.id_departement";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) liste.add(buildEmploye(rs));
        } catch (SQLException e) {
            System.out.println("Erreur findAll : " +
                    e.getMessage());
        }
        return liste;
    }

    // ===== FIND BY DEPARTEMENT =====
    public List<Employe> findByDepartement(int idDept) {
        List<Employe> liste = new ArrayList<>();
        String sql = "SELECT e.*, d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM employe e " +
                "JOIN departement d " +
                "ON e.id_departement = d.id_departement " +
                "WHERE e.id_departement=?";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            ps.setInt(1, idDept);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(buildEmploye(rs));
        } catch (SQLException e) {
            System.out.println("Erreur findByDept : " +
                    e.getMessage());
        }
        return liste;
    }

    // ===== RECHERCHER PAR NOM OU NAX =====
    public List<Employe> rechercherParNom(String terme) {
        List<Employe> liste = new ArrayList<>();
        String sql = "SELECT e.*, d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM employe e " +
                "JOIN departement d " +
                "ON e.id_departement = d.id_departement " +
                "WHERE e.nom LIKE ? " +
                "OR e.prenom LIKE ? " +
                "OR e.nax LIKE ?";
        try {
            PreparedStatement ps =
                    connexion.prepareStatement(sql);
            String t = "%" + terme + "%";
            ps.setString(1, t);
            ps.setString(2, t);
            ps.setString(3, t);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(buildEmploye(rs));
        } catch (SQLException e) {
            System.out.println("Erreur recherche : " +
                    e.getMessage());
        }
        return liste;
    }

    // ===== METHODE UTILITAIRE =====
    private Employe buildEmploye(ResultSet rs)
            throws SQLException {
        Departement dept = new Departement(
                rs.getInt("id_departement"),
                rs.getString("dept_nom"),
                rs.getString("responsable"),
                rs.getString("description")
        );
        return new Employe(
                rs.getInt("id_employe"),
                rs.getString("nax"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("telephone"),
                rs.getString("poste"),
                rs.getDate("date_naissance"),
                rs.getDate("date_embauche"),
                dept
        );
    }
}
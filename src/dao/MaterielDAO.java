package dao;

import connexion.ConnexionBD;
import modele.Departement;
import modele.Materiel;
import modele.TypeMateriel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterielDAO {

    private Connection connexion;

    public MaterielDAO() {
        this.connexion = ConnexionBD.getConnexion();
    }

    // ===== AJOUTER =====
    public boolean ajouter(Materiel m) {
        String sql = "INSERT INTO materiel (designation, marque, modele, " +
                "numero_serie, date_achat, etat, id_type, id_departement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setString(1, m.getDesignation());
            ps.setString(2, m.getMarque());
            ps.setString(3, m.getModele());
            ps.setString(4, m.getNumeroSerie());
            ps.setDate(5, m.getDateAchat() != null ?
                    new java.sql.Date(m.getDateAchat().getTime()) : null);
            ps.setString(6, m.getEtat());
            ps.setInt(7, m.getTypeMateriel().getIdType());
            ps.setInt(8, m.getDepartement().getIdDepartement());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur ajouter matériel : " + e.getMessage());
            return false;
        }
    }

    // ===== MODIFIER =====
    public boolean modifier(Materiel m) {
        String sql = "UPDATE materiel SET designation=?, marque=?, modele=?, " +
                "numero_serie=?, date_achat=?, etat=?, id_type=?, " +
                "id_departement=? WHERE id_materiel=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setString(1, m.getDesignation());
            ps.setString(2, m.getMarque());
            ps.setString(3, m.getModele());
            ps.setString(4, m.getNumeroSerie());
            ps.setDate(5, m.getDateAchat() != null ?
                    new java.sql.Date(m.getDateAchat().getTime()) : null);
            ps.setString(6, m.getEtat());
            ps.setInt(7, m.getTypeMateriel().getIdType());
            ps.setInt(8, m.getDepartement().getIdDepartement());
            ps.setInt(9, m.getIdMateriel());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur modifier matériel : " + e.getMessage());
            return false;
        }
    }

    // ===== SUPPRIMER =====
    public boolean supprimer(int id) {
        String sql = "DELETE FROM materiel WHERE id_materiel=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur supprimer matériel : " + e.getMessage());
            return false;
        }
    }

    // ===== FIND BY ID =====
    public Materiel findById(int id) {
        String sql = "SELECT m.*, " +
                "t.libelle as type_libelle, " +
                "d.nom as dept_nom, d.responsable, d.description " +
                "FROM materiel m " +
                "JOIN type_materiel t ON m.id_type = t.id_type " +
                "JOIN departement d ON m.id_departement = d.id_departement " +
                "WHERE m.id_materiel=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildMateriel(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur findById matériel : " + e.getMessage());
        }
        return null;
    }

    // ===== FIND ALL =====
    public List<Materiel> findAll() {
        List<Materiel> liste = new ArrayList<>();
        String sql = "SELECT m.*, " +
                "t.libelle as type_libelle, " +
                "d.nom as dept_nom, d.responsable, d.description " +
                "FROM materiel m " +
                "JOIN type_materiel t ON m.id_type = t.id_type " +
                "JOIN departement d ON m.id_departement = d.id_departement";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                try {
                    liste.add(buildMateriel(rs));
                } catch (Exception exLigne) {
                    int idProbable = -1;
                    try {
                        idProbable = rs.getInt("id_materiel");
                    } catch (Exception ignore) { }
                    System.out.println("Ligne materiel ignoree " +
                            "(id_materiel=" + idProbable + ") a " +
                            "cause de : " + exLigne);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur findAll matériel : " + e.getMessage());
        }
        return liste;
    }

    // ===== FIND DISPONIBLES =====
    public List<Materiel> findDisponibles() {
        List<Materiel> liste = new ArrayList<>();
        String sql = "SELECT m.*, " +
                "t.libelle as type_libelle, " +
                "d.nom as dept_nom, d.responsable, d.description " +
                "FROM materiel m " +
                "JOIN type_materiel t ON m.id_type = t.id_type " +
                "JOIN departement d ON m.id_departement = d.id_departement " +
                "WHERE m.etat = 'Disponible'";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                try {
                    liste.add(buildMateriel(rs));
                } catch (Exception exLigne) {
                    System.out.println("Ligne materiel ignoree " +
                            "(findDisponibles) : " + exLigne);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur findDisponibles : " + e.getMessage());
        }
        return liste;
    }

    // ===== FIND BY DEPARTEMENT =====
    public List<Materiel> findByDepartement(int idDepartement) {
        List<Materiel> liste = new ArrayList<>();
        String sql = "SELECT m.*, " +
                "t.libelle as type_libelle, " +
                "d.nom as dept_nom, d.responsable, d.description " +
                "FROM materiel m " +
                "JOIN type_materiel t ON m.id_type = t.id_type " +
                "JOIN departement d ON m.id_departement = d.id_departement " +
                "WHERE m.id_departement=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, idDepartement);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    liste.add(buildMateriel(rs));
                } catch (Exception exLigne) {
                    System.out.println("Ligne materiel ignoree " +
                            "(findByDepartement) : " + exLigne);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur findByDepartement matériel : " + e.getMessage());
        }
        return liste;
    }

    // ===== MODIFIER ETAT =====
    public boolean modifierEtat(int idMateriel, String etat) {
        String sql = "UPDATE materiel SET etat=? WHERE id_materiel=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setString(1, etat);
            ps.setInt(2, idMateriel);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur modifier état : " + e.getMessage());
            return false;
        }
    }

    // ===== METHODE UTILITAIRE =====
    private Materiel buildMateriel(ResultSet rs) throws SQLException {
        Departement dept = new Departement(
                rs.getInt("id_departement"),
                rs.getString("dept_nom"),
                rs.getString("responsable"),
                rs.getString("description")
        );
        TypeMateriel type = new TypeMateriel(
                rs.getInt("id_type"),
                rs.getString("type_libelle"),
                dept
        );
        return new Materiel(
                rs.getInt("id_materiel"),
                rs.getString("designation"),
                rs.getString("marque"),
                rs.getString("modele"),
                rs.getString("numero_serie"),
                rs.getDate("date_achat"),
                rs.getString("etat"),
                type,
                dept
        );
    }
}
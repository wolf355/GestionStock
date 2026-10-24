package dao;

import connexion.ConnexionBD;
import modele.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AffectationDAO {

    private Connection connexion;

    public AffectationDAO() {
        this.connexion = ConnexionBD.getConnexion();
    }

    // ===== AJOUTER =====
    public boolean ajouter(Affectation a) {
        String sql = "INSERT INTO affectation " +
                "(date_affectation, date_retour, remarque, " +
                "id_employe, id_materiel) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(a.getDateAffectation().getTime()));
            ps.setDate(2, a.getDateRetour() != null ?
                    new java.sql.Date(a.getDateRetour().getTime()) : null);
            ps.setString(3, a.getRemarque());
            ps.setInt(4, a.getEmploye().getIdEmploye());
            ps.setInt(5, a.getMateriel().getIdMateriel());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur ajouter affectation : " + e.getMessage());
            return false;
        }
    }

    // ===== RECUPERER (cloturer affectation) =====
    public boolean recuperer(int idAffectation, Date dateRetour) {
        String sql = "UPDATE affectation SET date_retour=? " +
                "WHERE id_affectation=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setDate(1, dateRetour);
            ps.setInt(2, idAffectation);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur récupérer matériel : " + e.getMessage());
            return false;
        }
    }

    // ===== FIND ALL =====
    public List<Affectation> findAll() {
        List<Affectation> liste = new ArrayList<>();
        String sql = "SELECT a.*, " +
                "e.nax, e.nom as emp_nom, e.prenom as emp_prenom, " +
                "e.email, e.telephone, e.poste, " +
                "e.date_naissance, e.date_embauche, " +
                "m.designation, m.marque, m.modele, " +
                "m.numero_serie, m.date_achat, m.etat, " +
                "t.id_type, t.libelle as type_libelle, " +
                "d.id_departement, d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM affectation a " +
                "JOIN employe e ON a.id_employe = e.id_employe " +
                "JOIN materiel m ON a.id_materiel = m.id_materiel " +
                "JOIN type_materiel t ON m.id_type = t.id_type " +
                "JOIN departement d ON m.id_departement = d.id_departement";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                liste.add(buildAffectation(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur findAll affectation : " + e.getMessage());
        }
        return liste;
    }

    // ===== FIND EN COURS =====
    public List<Affectation> findEnCours() {
        List<Affectation> liste = new ArrayList<>();
        String sql = "SELECT a.*, " +
                "e.nax, e.nom as emp_nom, e.prenom as emp_prenom, " +
                "e.email, e.telephone, e.poste, " +
                "e.date_naissance, e.date_embauche, " +
                "m.designation, m.marque, m.modele, " +
                "m.numero_serie, m.date_achat, m.etat, " +
                "t.id_type, t.libelle as type_libelle, " +
                "d.id_departement, d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM affectation a " +
                "JOIN employe e ON a.id_employe = e.id_employe " +
                "JOIN materiel m ON a.id_materiel = m.id_materiel " +
                "JOIN type_materiel t ON m.id_type = t.id_type " +
                "JOIN departement d ON m.id_departement = d.id_departement " +
                "WHERE a.date_retour IS NULL";
        try {
            Statement st = connexion.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                liste.add(buildAffectation(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur findEnCours : " + e.getMessage());
        }
        return liste;
    }

    // ===== FIND BY EMPLOYE =====
    public List<Affectation> findByEmploye(int idEmploye) {
        List<Affectation> liste = new ArrayList<>();
        String sql = "SELECT a.*, " +
                "e.nax, e.nom as emp_nom, e.prenom as emp_prenom, " +
                "e.email, e.telephone, e.poste, " +
                "e.date_naissance, e.date_embauche, " +
                "m.designation, m.marque, m.modele, " +
                "m.numero_serie, m.date_achat, m.etat, " +
                "t.id_type, t.libelle as type_libelle, " +
                "d.id_departement, d.nom as dept_nom, " +
                "d.responsable, d.description " +
                "FROM affectation a " +
                "JOIN employe e ON a.id_employe = e.id_employe " +
                "JOIN materiel m ON a.id_materiel = m.id_materiel " +
                "JOIN type_materiel t ON m.id_type = t.id_type " +
                "JOIN departement d ON m.id_departement = d.id_departement " +
                "WHERE a.id_employe=?";
        try {
            PreparedStatement ps = connexion.prepareStatement(sql);
            ps.setInt(1, idEmploye);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(buildAffectation(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur findByEmploye : " + e.getMessage());
        }
        return liste;
    }

    // ===== METHODE UTILITAIRE =====
    private Affectation buildAffectation(ResultSet rs) throws SQLException {
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
        Employe emp = new Employe(
                rs.getInt("id_employe"),
                rs.getString("nax"),
                rs.getString("emp_nom"),
                rs.getString("emp_prenom"),
                rs.getString("email"),
                rs.getString("telephone"),
                rs.getString("poste"),
                rs.getDate("date_naissance"),
                rs.getDate("date_embauche"),
                dept
        );
        Materiel mat = new Materiel(
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
        return new Affectation(
                rs.getInt("id_affectation"),
                rs.getDate("date_affectation"),
                rs.getDate("date_retour"),
                rs.getString("remarque"),
                emp,
                mat
        );
    }
}
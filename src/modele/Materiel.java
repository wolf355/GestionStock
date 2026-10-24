package modele;

import java.util.Date;

public class Materiel {

    private int idMateriel;
    private String designation;
    private String marque;
    private String modele;
    private String numeroSerie;
    private Date dateAchat;
    private String etat;
    private TypeMateriel typeMateriel;
    private Departement departement;

    // Constructeur vide
    public Materiel() {}

    // Constructeur avec paramètres
    public Materiel(int idMateriel, String designation, String marque,
                    String modele, String numeroSerie, Date dateAchat,
                    String etat, TypeMateriel typeMateriel,
                    Departement departement) {
        this.idMateriel   = idMateriel;
        this.designation  = designation;
        this.marque       = marque;
        this.modele       = modele;
        this.numeroSerie  = numeroSerie;
        this.dateAchat    = dateAchat;
        this.etat         = etat;
        this.typeMateriel = typeMateriel;
        this.departement  = departement;
    }

    // Getters et Setters
    public int getIdMateriel() { return idMateriel; }
    public void setIdMateriel(int idMateriel) {
        this.idMateriel = idMateriel;
    }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public Date getDateAchat() { return dateAchat; }
    public void setDateAchat(Date dateAchat) {
        this.dateAchat = dateAchat;
    }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public TypeMateriel getTypeMateriel() { return typeMateriel; }
    public void setTypeMateriel(TypeMateriel typeMateriel) {
        this.typeMateriel = typeMateriel;
    }

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public boolean estDisponible() {
        return "Disponible".equals(this.etat);
    }

    @Override
    public String toString() {
        return designation + " - " + marque + " (" + numeroSerie + ")";
    }
}
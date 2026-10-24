package modele;

import java.util.Date;

public class Affectation {

    private int idAffectation;
    private Date dateAffectation;
    private Date dateRetour;
    private String remarque;
    private Employe employe;
    private Materiel materiel;

    // Constructeur vide
    public Affectation() {}

    // Constructeur avec paramètres
    public Affectation(int idAffectation, Date dateAffectation,
                       Date dateRetour, String remarque,
                       Employe employe, Materiel materiel) {
        this.idAffectation   = idAffectation;
        this.dateAffectation = dateAffectation;
        this.dateRetour      = dateRetour;
        this.remarque        = remarque;
        this.employe         = employe;
        this.materiel        = materiel;
    }

    // Getters et Setters
    public int getIdAffectation() { return idAffectation; }
    public void setIdAffectation(int idAffectation) {
        this.idAffectation = idAffectation;
    }

    public Date getDateAffectation() { return dateAffectation; }
    public void setDateAffectation(Date dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public Date getDateRetour() { return dateRetour; }
    public void setDateRetour(Date dateRetour) {
        this.dateRetour = dateRetour;
    }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public boolean estEnCours() {
        return this.dateRetour == null;
    }

    @Override
    public String toString() {
        return "Affectation #" + idAffectation + " - "
                + employe + " -> " + materiel;
    }
}
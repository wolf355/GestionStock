package modele;

import java.util.Date;

public class Employe {

    private int idEmploye;
    private String nax;        // <-- NOUVEAU
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String poste;
    private Date dateNaissance;
    private Date dateEmbauche;
    private Departement departement;

    // Constructeur vide
    public Employe() {}

    // Constructeur avec paramètres
    public Employe(int idEmploye, String nax, String nom,
                   String prenom, String email, String telephone,
                   String poste, Date dateNaissance,
                   Date dateEmbauche, Departement departement) {
        this.idEmploye     = idEmploye;
        this.nax           = nax;
        this.nom           = nom;
        this.prenom        = prenom;
        this.email         = email;
        this.telephone     = telephone;
        this.poste         = poste;
        this.dateNaissance = dateNaissance;
        this.dateEmbauche  = dateEmbauche;
        this.departement   = departement;
    }

    // Getters et Setters
    public int getIdEmploye() { return idEmploye; }
    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getNax() { return nax; }
    public void setNax(String nax) { this.nax = nax; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Date getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(Date dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + nax + ")";
    }
}
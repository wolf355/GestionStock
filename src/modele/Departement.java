package modele;

public class Departement {

    private int idDepartement;
    private String nom;
    private String responsable;
    private String description;

    // Constructeur vide
    public Departement() {}

    // Constructeur avec paramètres
    public Departement(int idDepartement, String nom,
                       String responsable, String description) {
        this.idDepartement = idDepartement;
        this.nom           = nom;
        this.responsable   = responsable;
        this.description   = description;
    }

    // Getters et Setters
    public int getIdDepartement() { return idDepartement; }
    public void setIdDepartement(int idDepartement) {
        this.idDepartement = idDepartement;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return nom;
    }
}
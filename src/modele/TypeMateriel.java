package modele;

public class TypeMateriel {

    private int idType;
    private String libelle;
    private Departement departement;

    // Constructeur vide
    public TypeMateriel() {}

    // Constructeur avec paramètres
    public TypeMateriel(int idType, String libelle, Departement departement) {
        this.idType      = idType;
        this.libelle     = libelle;
        this.departement = departement;
    }

    // Getters et Setters
    public int getIdType() { return idType; }
    public void setIdType(int idType) { this.idType = idType; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
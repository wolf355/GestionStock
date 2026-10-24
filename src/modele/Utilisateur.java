package modele;

public class Utilisateur {

    private int idUtilisateur;
    private String login;
    private String motDePasse;
    private String role;
    private Departement departement;

    // Constructeur vide
    public Utilisateur() {}

    // Constructeur avec paramètres
    public Utilisateur(int idUtilisateur, String login,
                       String motDePasse, String role,
                       Departement departement) {
        this.idUtilisateur = idUtilisateur;
        this.login         = login;
        this.motDePasse    = motDePasse;
        this.role          = role;
        this.departement   = departement;
    }

    // Getters et Setters
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    // Méthodes utilitaires
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    public boolean isRespoInfo() {
        return "RESPO_INFO".equals(this.role);
    }

    public boolean isRespoRH() {
        return "RESPO_RH".equals(this.role);
    }

    @Override
    public String toString() {
        return login + " (" + role + ")";
    }
}
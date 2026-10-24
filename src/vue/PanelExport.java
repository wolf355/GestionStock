package vue;

import dao.AffectationDAO;
import dao.EmployeDAO;
import dao.MaterielDAO;
import modele.Affectation;
import modele.Employe;
import modele.Materiel;
import modele.Utilisateur;
import utils.ExportPDF;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PanelExport extends VBox {

    private final Utilisateur utilisateur;
    private final EmployeDAO daoE = new EmployeDAO();
    private final MaterielDAO daoM = new MaterielDAO();
    private final AffectationDAO daoA = new AffectationDAO();

    public PanelExport(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        setStyle("-fx-background-color: #F5F6FA;");
        setPadding(new Insets(30));
        setSpacing(20);
        VBox.setVgrow(this, Priority.ALWAYS);
        initComposants();
    }

    private void initComposants() {

        Label lblTitre = new Label("Exports PDF");
        lblTitre.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );

        Label lblSousTitre = new Label(
                utilisateur.isAdmin()
                        ? "Exporte les données de l'ensemble de l'entreprise"
                        : "Exporte les données de ton département");
        lblSousTitre.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #6B7280;"
        );

        VBox cartes = new VBox(16);
        cartes.getChildren().addAll(
                creerCarte("👤", "Liste des employés",
                        "Exporter la liste des employés" +
                                (utilisateur.isAdmin() ? "" :
                                        " (tous départements)"),
                        this::exporterEmployes),
                creerCarte("🖥️", "Liste du matériel",
                        "Exporter l'inventaire du matériel" +
                                (utilisateur.isAdmin() ? "" :
                                        " de ton département"),
                        this::exporterMateriel),
                creerCarte("🔄", "Historique des affectations",
                        "Exporter l'historique complet des affectations" +
                                (utilisateur.isAdmin() ? "" :
                                        " de ton département"),
                        this::exporterHistorique)
        );

        getChildren().addAll(lblTitre, lblSousTitre, cartes);
    }

    private HBox creerCarte(String icone, String titre, String description,
                            Runnable action) {
        HBox carte = new HBox(20);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setPadding(new Insets(20));
        carte.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        Label lblIcone = new Label(icone);
        lblIcone.setStyle("-fx-font-size: 28px;");

        VBox texte = new VBox(4);
        Label lblTitre = new Label(titre);
        lblTitre.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );
        Label lblDesc = new Label(description);
        lblDesc.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #6B7280;"
        );
        texte.getChildren().addAll(lblTitre, lblDesc);
        HBox.setHgrow(texte, Priority.ALWAYS);

        Button btn = new Button("Exporter en PDF");
        btn.setStyle(
                "-fx-background-color: #1E2840;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnAction(e -> action.run());

        carte.getChildren().addAll(lblIcone, texte, btn);
        return carte;
    }

    // ================================================================
    // Actions d'export (filtrage par département selon le rôle)
    // ================================================================

    private void exporterEmployes() {
        List<Employe> employes = daoE.findAll();
        sauvegarderPuisExporter("employes.pdf", fichier -> {
            ExportPDF.exporterEmployes(employes, fichier);
        });
    }

    private void exporterMateriel() {
        List<Materiel> materiels = utilisateur.isAdmin()
                ? daoM.findAll()
                : daoM.findByDepartement(
                utilisateur.getDepartement().getIdDepartement());
        sauvegarderPuisExporter("materiel.pdf", fichier -> {
            ExportPDF.exporterMateriel(materiels, fichier);
        });
    }

    private void exporterHistorique() {
        List<Affectation> affectations = daoA.findAll();
        if (!utilisateur.isAdmin()) {
            int idDept = utilisateur.getDepartement().getIdDepartement();
            affectations = affectations.stream()
                    .filter(a -> a.getMateriel() != null
                            && a.getMateriel().getDepartement() != null
                            && a.getMateriel().getDepartement()
                            .getIdDepartement() == idDept)
                    .collect(Collectors.toList());
        }
        List<Affectation> affectationsFinal = affectations;
        sauvegarderPuisExporter("historique_affectations.pdf", fichier -> {
            ExportPDF.exporterHistorique(affectationsFinal, fichier);
        });
    }

    private interface ActionExport {
        void executer(File fichier) throws IOException;
    }

    private void sauvegarderPuisExporter(String nomParDefaut,
                                         ActionExport action) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Enregistrer l'export PDF");
        chooser.setInitialFileName(nomParDefaut);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Fichier PDF", "*.pdf"));

        Window fenetre = getScene() != null
                ? getScene().getWindow() : null;
        File fichier = chooser.showSaveDialog(fenetre);
        if (fichier == null) {
            return; // annulé par l'utilisateur
        }

        try {
            action.executer(fichier);
            afficherAlerte(Alert.AlertType.INFORMATION, "Export réussi",
                    "Le fichier a été généré :\n" + fichier.getAbsolutePath());
        } catch (IOException ex) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur d'export",
                    "Impossible de générer le PDF : " + ex.getMessage());
        }
    }

    private void afficherAlerte(Alert.AlertType type, String titre,
                                String message) {
        Alert alert = new Alert(type);
        utils.IconUtil.appliquerIcone(alert);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
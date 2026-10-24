package vue;

import modele.Utilisateur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FenetrePrincipale {

    private Stage stage;
    private Utilisateur utilisateur;
    private VBox panelContenu;
    private Button btnActif = null;

    public FenetrePrincipale(Stage stage,
                             Utilisateur utilisateur) {
        this.stage       = stage;
        this.utilisateur = utilisateur;
    }

    public void afficher() {

        // ===== MENU LATERAL =====
        VBox menuLateral = creerMenuLateral();

        // ===== CONTENU PRINCIPAL =====
        panelContenu = new VBox();
        panelContenu.setStyle(
                "-fx-background-color: #F5F6FA;");
        HBox.setHgrow(panelContenu, Priority.ALWAYS);

        // Afficher le panel par défaut selon le rôle
        if (utilisateur.isAdmin()) {
            afficherPanel(new PanelDepartement());
        } else {
            // Respo Info ou RH -> panel matériel en lecture
            afficherPanel(new PanelMateriel(utilisateur));
        }

        // ===== LAYOUT PRINCIPAL =====
        HBox root = new HBox();
        root.getChildren().addAll(menuLateral, panelContenu);

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(1100);
        stage.setMinHeight(680);

// Forcer le rafraîchissement du plein écran : si le stage était déjà
// maximisé (venant de la page de login), JavaFX ne redimensionne pas
// toujours le contenu tant qu'on ne "retriggue" pas l'état.
        stage.setMaximized(false);
        stage.setMaximized(true);

        stage.show();
    }

    private VBox creerMenuLateral() {
        VBox menu = new VBox(5);
        menu.setStyle("-fx-background-color: #1E2840;");
        menu.setPrefWidth(230);
        menu.setMinWidth(230);
        menu.setPadding(new Insets(20, 15, 20, 15));

        // Logo
        VBox logoBox = new VBox(4);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(10, 0, 10, 0));

        javafx.scene.image.Image imageLogo = new javafx.scene.image.Image(
                getClass().getResourceAsStream("/images/logo_modep.png"));
        javafx.scene.image.ImageView vueLogo =
                new javafx.scene.image.ImageView(imageLogo);
        vueLogo.setFitWidth(50);
        vueLogo.setFitHeight(50);
        vueLogo.setPreserveRatio(true);
        vueLogo.setClip(new javafx.scene.shape.Circle(25, 25, 25));

        StackPane cadreLogo = new StackPane(vueLogo);
        cadreLogo.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-padding: 3;"
        );
        cadreLogo.setMaxSize(56, 56);

        Label lblModep = new Label("MODEP");
        lblModep.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        Label lblSous = new Label("Gestion du Stock");
        lblSous.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-text-fill: #8899BB;"
        );

        // Afficher le rôle de l'utilisateur connecté
        String roleAffiche = switch (utilisateur.getRole()) {
            case "ADMIN"      -> "Administrateur";
            case "RESPO_INFO" -> "Respo. Informatique";
            case "RESPO_RH"   -> "Respo. RH";
            default           -> utilisateur.getRole();
        };
        Label lblRole = new Label(
                utilisateur.getLogin() + " · " + roleAffiche);
        lblRole.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-text-fill: #F59E0B;" +
                        "-fx-font-weight: bold;"
        );

        logoBox.getChildren().addAll(
                cadreLogo, lblModep, lblSous, lblRole);

        // Séparateur
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2A3A5C;");
        VBox.setMargin(sep, new Insets(10, 0, 15, 0));

        menu.getChildren().addAll(logoBox, sep);

        // ===== BOUTONS SELON LE ROLE =====
        if (utilisateur.isAdmin()) {
            // Admin -> tous les modules
            Button btnDept =
                    creerBoutonMenu("🏢   Départements");
            Button btnType =
                    creerBoutonMenu("📦   Types Matériel");
            Button btnEmp =
                    creerBoutonMenu("👤   Employés");
            Button btnMat =
                    creerBoutonMenu("🖥️   Matériel");
            Button btnAff =
                    creerBoutonMenu("🔄   Affectations");
            Button btnExport =
                    creerBoutonMenu("📄   Exports");

            btnDept.setOnAction(e -> {
                activerBouton(btnDept);
                afficherPanel(new PanelDepartement());
            });
            btnType.setOnAction(e -> {
                activerBouton(btnType);
                afficherPanel(new PanelTypeMateriel());
            });
            btnEmp.setOnAction(e -> {
                activerBouton(btnEmp);
                afficherPanel(new PanelEmploye(utilisateur));
            });
            btnMat.setOnAction(e -> {
                activerBouton(btnMat);
                afficherPanel(new PanelMateriel(utilisateur));
            });
            btnAff.setOnAction(e -> {
                activerBouton(btnAff);
                afficherPanel(
                        new PanelAffectation(utilisateur));
            });
            btnExport.setOnAction(e -> {
                activerBouton(btnExport);
                afficherPanel(new PanelExport(utilisateur));
            });

            activerBouton(btnDept);
            menu.getChildren().addAll(
                    btnDept, btnType, btnEmp, btnMat, btnAff,
                    btnExport);

        } else {
            // Respo Info ou RH -> modules limités
            Button btnEmp =
                    creerBoutonMenu("👤   Employés");
            Button btnMat =
                    creerBoutonMenu("🖥️   Matériel");
            Button btnAff =
                    creerBoutonMenu("🔄   Affectations");
            Button btnExport =
                    creerBoutonMenu("📄   Exports");

            btnEmp.setOnAction(e -> {
                activerBouton(btnEmp);
                afficherPanel(new PanelEmploye(utilisateur));
            });
            btnMat.setOnAction(e -> {
                activerBouton(btnMat);
                afficherPanel(new PanelMateriel(utilisateur));
            });
            btnAff.setOnAction(e -> {
                activerBouton(btnAff);
                afficherPanel(
                        new PanelAffectation(utilisateur));
            });
            btnExport.setOnAction(e -> {
                activerBouton(btnExport);
                afficherPanel(new PanelExport(utilisateur));
            });

            activerBouton(btnMat);
            menu.getChildren().addAll(
                    btnEmp, btnMat, btnAff, btnExport);
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bouton déconnexion
        Button btnDecon = new Button("⬅   Déconnexion");
        btnDecon.setMaxWidth(Double.MAX_VALUE);
        btnDecon.setPrefHeight(42);
        btnDecon.setStyle(
                "-fx-background-color: #EF4444;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: CENTER-LEFT;" +
                        "-fx-padding: 0 0 0 16;"
        );
        btnDecon.setOnMouseEntered(e ->
                btnDecon.setStyle(
                        "-fx-background-color: #DC2626;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 13px;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER-LEFT;" +
                                "-fx-padding: 0 0 0 16;"
                )
        );
        btnDecon.setOnMouseExited(e ->
                btnDecon.setStyle(
                        "-fx-background-color: #EF4444;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 13px;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER-LEFT;" +
                                "-fx-padding: 0 0 0 16;"
                )
        );
        btnDecon.setOnAction(e ->
                new FenetreLogin(stage).afficher());

        menu.getChildren().addAll(spacer, btnDecon);
        VBox.setMargin(btnDecon, new Insets(0, 0, 10, 0));

        return menu;
    }

    private Button creerBoutonMenu(String texte) {
        Button btn = new Button(texte);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(42);
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #CCD6F6;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: CENTER-LEFT;" +
                        "-fx-padding: 0 0 0 16;"
        );
        btn.setOnMouseEntered(e -> {
            if (btn != btnActif) {
                btn.setStyle(
                        "-fx-background-color: #2A3A5C;" +
                                "-fx-text-fill: #CCD6F6;" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER-LEFT;" +
                                "-fx-padding: 0 0 0 16;"
                );
            }
        });
        btn.setOnMouseExited(e -> {
            if (btn != btnActif) {
                btn.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: #CCD6F6;" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 8;" +
                                "-fx-cursor: hand;" +
                                "-fx-alignment: CENTER-LEFT;" +
                                "-fx-padding: 0 0 0 16;"
                );
            }
        });
        return btn;
    }

    private void activerBouton(Button btn) {
        if (btnActif != null) {
            btnActif.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #CCD6F6;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-alignment: CENTER-LEFT;" +
                            "-fx-padding: 0 0 0 16;"
            );
        }
        btn.setStyle(
                "-fx-background-color: #F59E0B;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: CENTER-LEFT;" +
                        "-fx-padding: 0 0 0 16;"
        );
        btnActif = btn;
    }

    private void afficherPanel(VBox panel) {
        panelContenu.getChildren().clear();
        panelContenu.getChildren().add(panel);
        VBox.setVgrow(panel, Priority.ALWAYS);
    }
}
package vue;

import dao.UtilisateurDAO;
import modele.Utilisateur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class FenetreLogin {

    // ===== Palette Marsa Maroc (extraite de leur logo) =====
    private static final String BLEU_MARINE = "#113E6C";
    private static final String BLEU_MARINE_HOVER = "#1B4E82";
    private static final String BLEU_ACCENT = "#1881BC";

    private Stage stage;
    private TextField champLogin;
    private PasswordField champMotDePasse;
    private Label lblErreur;
    private UtilisateurDAO dao = new UtilisateurDAO();

    public FenetreLogin(Stage stage) {
        this.stage = stage;
    }

    public void afficher() {

        double largeurPanelGauche = 380;
        double largeurPanelDroit = 420;
        double hauteur = 500;

        // ===== PANEL GAUCHE (couleurs Marsa Maroc + logo) =====
        VBox panelGauche = new VBox(15);
        panelGauche.setAlignment(Pos.CENTER);
        panelGauche.setPrefWidth(largeurPanelGauche);
        panelGauche.setStyle(
                "-fx-background-color: linear-gradient(to bottom, " +
                        BLEU_MARINE + ", #0C2C4E);");
        panelGauche.setPadding(new Insets(60));

        // Logo officiel MODEP, découpé en cercle
        Image imageLogo = new Image(
                getClass().getResourceAsStream("/images/logo_modep.png"));
        ImageView vueLogo = new ImageView(imageLogo);
        vueLogo.setFitWidth(90);
        vueLogo.setFitHeight(90);
        vueLogo.setPreserveRatio(true);
        Circle decoupeCercle = new Circle(45, 45, 45);
        vueLogo.setClip(decoupeCercle);

        StackPane cadreLogo = new StackPane(vueLogo);
        cadreLogo.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-padding: 4;"
        );
        cadreLogo.setMaxSize(98, 98);

        Label lblModep = new Label("MODEP");
        lblModep.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        Label lblSousTitre = new Label(
                "Gestion du Stock Matériel");
        lblSousTitre.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: " + BLEU_ACCENT + ";" +
                        "-fx-font-weight: bold;"
        );

        Label lblDesc = new Label(
                "Gérez votre parc matériel\n" +
                        "en toute simplicité."
        );
        lblDesc.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #C7D6E5;" +
                        "-fx-text-alignment: center;"
        );
        lblDesc.setAlignment(Pos.CENTER);

        Separator separateur = new Separator();
        separateur.setMaxWidth(180);
        separateur.setStyle("-fx-background-color: " + BLEU_ACCENT + ";");

        panelGauche.getChildren().addAll(
                cadreLogo, lblModep, lblSousTitre,
                separateur, lblDesc
        );

        // ===== PANEL DROIT (photo du port en fond + formulaire) =====
        Image imagePort = new Image(
                getClass().getResourceAsStream("/images/port_background.jpg"));
        ImageView vuePort = new ImageView(imagePort);
        // Recadrage type "cover" : on garde les proportions et on
        // rogne l'excédent, au lieu d'étirer/déformer l'image.
        double ratioCible = largeurPanelDroit / hauteur;
        double largeurSource = imagePort.getWidth();
        double hauteurSource = imagePort.getHeight();
        double largeurRecadree = hauteurSource * ratioCible;
        double xDepart = (largeurSource - largeurRecadree) / 2;
        vuePort.setViewport(new javafx.geometry.Rectangle2D(
                Math.max(0, xDepart), 0, largeurRecadree, hauteurSource));
        vuePort.setPreserveRatio(false);

        // Voile blanc semi-transparent pour garder le formulaire lisible
        Region voileClair = new Region();
        voileClair.setStyle(
                "-fx-background-color: rgba(255,255,255,0.75);");

        VBox panelDroit = new VBox(20);
        panelDroit.setAlignment(Pos.CENTER);
        panelDroit.setPrefWidth(largeurPanelDroit);
        panelDroit.setPadding(new Insets(60));
        panelDroit.setMaxWidth(largeurPanelDroit);

        Label lblTitreForm = new Label("Connexion");
        lblTitreForm.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + BLEU_MARINE + ";"
        );

        Label lblSousTitreForm = new Label(
                "Entrez vos identifiants pour accéder\n" +
                        "à l'application."
        );
        lblSousTitreForm.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #4A5B6E;"
        );

        // Login
        VBox boxLogin = new VBox(6);
        Label lblLogin = new Label("Login");
        lblLogin.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1F2937;"
        );
        champLogin = new TextField();
        champLogin.setPromptText("Entrez votre login");
        champLogin.setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 14 12 14;" +
                        "-fx-font-size: 13px;"
        );
        champLogin.setPrefHeight(45);
        boxLogin.getChildren().addAll(lblLogin, champLogin);

        // Mot de passe
        VBox boxMdp = new VBox(6);
        Label lblMdp = new Label("Mot de passe");
        lblMdp.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1F2937;"
        );
        champMotDePasse = new PasswordField();
        champMotDePasse.setPromptText(
                "Entrez votre mot de passe");
        champMotDePasse.setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 14 12 14;" +
                        "-fx-font-size: 13px;"
        );
        champMotDePasse.setPrefHeight(45);
        boxMdp.getChildren().addAll(lblMdp, champMotDePasse);

        // Erreur
        lblErreur = new Label("");
        lblErreur.setStyle(
                "-fx-text-fill: #DC2626;" +
                        "-fx-font-size: 13px;"
        );

        // Bouton connexion
        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setMaxWidth(Double.MAX_VALUE);
        btnConnexion.setPrefHeight(45);
        String styleBoutonNormal =
                "-fx-background-color: " + BLEU_MARINE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;";
        String styleBoutonHover =
                "-fx-background-color: " + BLEU_ACCENT + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;";
        btnConnexion.setStyle(styleBoutonNormal);
        btnConnexion.setOnMouseEntered(e -> btnConnexion.setStyle(styleBoutonHover));
        btnConnexion.setOnMouseExited(e -> btnConnexion.setStyle(styleBoutonNormal));

        btnConnexion.setOnAction(e -> seConnecter());
        champMotDePasse.setOnAction(e -> seConnecter());

        panelDroit.getChildren().addAll(
                lblTitreForm, lblSousTitreForm,
                boxLogin, boxMdp,
                lblErreur, btnConnexion
        );

        StackPane panelDroitAvecFond = new StackPane();
        panelDroitAvecFond.setPrefWidth(largeurPanelDroit);
        panelDroitAvecFond.getChildren().addAll(
                vuePort, voileClair, panelDroit);
        HBox.setHgrow(panelDroitAvecFond, Priority.ALWAYS);

        // La photo et le voile suivent la taille réelle du panneau
        // (utile quand la fenêtre est maximisée / plein écran)
        vuePort.fitWidthProperty().bind(panelDroitAvecFond.widthProperty());
        vuePort.fitHeightProperty().bind(panelDroitAvecFond.heightProperty());
        voileClair.prefWidthProperty().bind(panelDroitAvecFond.widthProperty());
        voileClair.prefHeightProperty().bind(panelDroitAvecFond.heightProperty());

        // ===== LAYOUT PRINCIPAL =====
        HBox root = new HBox();
        root.getChildren().addAll(panelGauche, panelDroitAvecFond);

        Scene scene = new Scene(root, largeurPanelGauche + largeurPanelDroit, hauteur);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(900);
        stage.setMinHeight(560);
        stage.setMaximized(true);
        stage.show();
    }

    private void seConnecter() {
        String login = champLogin.getText().trim();
        String mdp   = champMotDePasse.getText();

        if (login.isEmpty() || mdp.isEmpty()) {
            lblErreur.setText(
                    "⚠ Veuillez remplir tous les champs !");
            return;
        }

        // Authentification depuis la base de données
        Utilisateur utilisateur = dao.authentifier(login, mdp);

        if (utilisateur != null) {
            // Connexion réussie -> ouvrir la bonne fenêtre
            // selon le rôle
            FenetrePrincipale principale =
                    new FenetrePrincipale(stage, utilisateur);
            principale.afficher();
        } else {
            lblErreur.setText(
                    "✗ Login ou mot de passe incorrect !");
            champMotDePasse.clear();
        }
    }
}
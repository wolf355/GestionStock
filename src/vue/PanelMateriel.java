package vue;

import dao.DepartementDAO;
import dao.MaterielDAO;
import dao.TypeMaterielDAO;
import modele.Departement;
import modele.Materiel;
import modele.TypeMateriel;
import modele.Utilisateur;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PanelMateriel extends VBox {

    private MaterielDAO     dao  = new MaterielDAO();
    private DepartementDAO  daoD = new DepartementDAO();
    private TypeMaterielDAO daoT = new TypeMaterielDAO();
    private Utilisateur utilisateur;

    // Classe interne pour la vue groupée
    public static class LigneMateriel {
        private String designation;
        private String marque;
        private String modele;
        private String type;
        private String departement;
        private int    qteTotal;
        private int    qteDispo;
        private List<Materiel> unites;

        public LigneMateriel(String designation, String marque,
                             String modele, String type,
                             String departement) {
            this.designation  = designation;
            this.marque       = marque;
            this.modele       = modele;
            this.type         = type;
            this.departement  = departement;
            this.unites       = new ArrayList<>();
        }

        public void ajouterUnite(Materiel m) {
            unites.add(m);
            qteTotal++;
            if ("Disponible".equals(m.getEtat())) qteDispo++;
        }

        public String getDesignation()  { return designation; }
        public String getMarque()       { return marque; }
        public String getModele()       { return modele; }
        public String getType()         { return type; }
        public String getDepartement()  { return departement; }
        public int    getQteTotal()     { return qteTotal; }
        public int    getQteDispo()     { return qteDispo; }
        public List<Materiel> getUnites(){ return unites; }
        public String getDisponibilite(){
            return qteDispo + "/" + qteTotal;
        }
    }

    private TableView<LigneMateriel> table;
    private ObservableList<LigneMateriel> donnees;
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy");

    public PanelMateriel(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        setStyle("-fx-background-color: #F5F6FA;");
        setPadding(new Insets(30));
        setSpacing(20);
        VBox.setVgrow(this, Priority.ALWAYS);
        initComposants();
        chargerDonnees();
    }

    private void initComposants() {

        // TITRE + FILTRES + BOUTON
        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitre = new Label("Gestion du Matériel");
        lblTitre.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );
        HBox.setHgrow(lblTitre, Priority.NEVER);

        TextField champRecherche = new TextField();
        champRecherche.setPromptText("🔍  Rechercher...");
        champRecherche.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 14 8 14;" +
                        "-fx-font-size: 13px;"
        );
        champRecherche.setPrefWidth(220);
        champRecherche.setPrefHeight(40);
        champRecherche.textProperty().addListener(
                (obs, old, nw) -> filtrerParRecherche(nw));

        Region espaceurTitre = new Region();
        HBox.setHgrow(espaceurTitre, Priority.ALWAYS);
        espaceurTitre.setMinWidth(40);
        topBar.getChildren().addAll(lblTitre, espaceurTitre, champRecherche);

        // Bouton ajouter uniquement pour Admin
        if (utilisateur.isAdmin()) {
            Button btnNouveau =
                    new Button("+ Ajouter un équipement");
            btnNouveau.setStyle(
                    "-fx-background-color: #1E2840;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-padding: 10 20 10 20;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;"
            );
            btnNouveau.setOnAction(
                    e -> afficherFormulaire(null));
            topBar.getChildren().add(btnNouveau);
        }

        // TABLEAU GROUPÉ
        table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, " +
                        "rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LigneMateriel, String> colDesig =
                new TableColumn<>("Désignation");
        colDesig.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getDesignation()));

        TableColumn<LigneMateriel, String> colMarque =
                new TableColumn<>("Marque");
        colMarque.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getMarque()));

        TableColumn<LigneMateriel, String> colModele =
                new TableColumn<>("Modèle");
        colModele.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getModele()));

        TableColumn<LigneMateriel, String> colType =
                new TableColumn<>("Type");
        colType.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getType()));

        TableColumn<LigneMateriel, String> colDept =
                new TableColumn<>("Département");
        colDept.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getDepartement()));

        // Colonne disponibilité avec badge X/Y
        TableColumn<LigneMateriel, String> colDispo =
                new TableColumn<>("Disponibilité");
        colDispo.setMaxWidth(130);
        colDispo.setMinWidth(130);
        colDispo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(
                    String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >=
                        table.getItems().size()) {
                    setGraphic(null);
                } else {
                    LigneMateriel l =
                            table.getItems().get(getIndex());
                    Label badge = new Label(
                            l.getDisponibilite());
                    String style;
                    if (l.getQteDispo() == 0) {
                        // Tout affecté -> rouge
                        style =
                                "-fx-background-color: #FEE2E2;" +
                                        "-fx-text-fill: #DC2626;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 12px;" +
                                        "-fx-background-radius: 20;" +
                                        "-fx-padding: 4 10 4 10;";
                    } else if (l.getQteDispo() ==
                            l.getQteTotal()) {
                        // Tout disponible -> vert
                        style =
                                "-fx-background-color: #DCFCE7;" +
                                        "-fx-text-fill: #16A34A;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 12px;" +
                                        "-fx-background-radius: 20;" +
                                        "-fx-padding: 4 10 4 10;";
                    } else {
                        // Partiellement disponible -> orange
                        style =
                                "-fx-background-color: #FEF3C7;" +
                                        "-fx-text-fill: #D97706;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 12px;" +
                                        "-fx-background-radius: 20;" +
                                        "-fx-padding: 4 10 4 10;";
                    }
                    badge.setStyle(style);
                    setGraphic(badge);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        // Colonne Actions
        TableColumn<LigneMateriel, Void> colActions =
                new TableColumn<>("Actions");
        colActions.setMaxWidth(160);
        colActions.setMinWidth(160);
        colActions.setCellFactory(col -> new TableCell<>() {
            final Button btnDetail =
                    new Button("👁  Détail");
            final Button btnAdd =
                    new Button("+ Unité");
            final HBox box = utilisateur.isAdmin() ?
                    new HBox(8, btnDetail, btnAdd) :
                    new HBox(8, btnDetail);
            {
                box.setAlignment(Pos.CENTER);
                btnDetail.setStyle(
                        "-fx-background-color: #EEF2FF;" +
                                "-fx-text-fill: #4F46E5;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-size: 11px;"
                );
                btnAdd.setStyle(
                        "-fx-background-color: #DCFCE7;" +
                                "-fx-text-fill: #16A34A;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-size: 11px;"
                );
                btnDetail.setOnAction(e -> {
                    LigneMateriel l = getTableView()
                            .getItems().get(getIndex());
                    afficherDetail(l);
                });
                btnAdd.setOnAction(e -> {
                    LigneMateriel l = getTableView()
                            .getItems().get(getIndex());
                    afficherFormulaireNouvelleUnite(l);
                });
            }
            @Override
            protected void updateItem(
                    Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
                setStyle("-fx-alignment: CENTER;");
            }
        });

        table.getColumns().addAll(
                colDesig, colMarque, colModele,
                colType, colDept, colDispo, colActions);
        donnees = FXCollections.observableArrayList();
        table.setItems(donnees);

        getChildren().addAll(topBar, table);
    }

    private void chargerDonnees() {
        donnees.clear();
        List<Materiel> tous;

        // Filtrer selon le rôle
        if (utilisateur.isAdmin()) {
            tous = dao.findAll();
        } else if (utilisateur.isRespoInfo()) {
            tous = dao.findByDepartement(
                    utilisateur.getDepartement()
                            .getIdDepartement());
        } else {
            tous = dao.findByDepartement(
                    utilisateur.getDepartement()
                            .getIdDepartement());
        }

        System.out.println("=== DEBUG MATERIEL ===");
        System.out.println("utilisateur.isAdmin() = " + utilisateur.isAdmin());
        System.out.println("dao renvoie " + tous.size() + " lignes brutes");

        try {
            java.sql.Statement stTest =
                    connexion.ConnexionBD.getConnexion()
                            .createStatement();
            java.sql.ResultSet rsTest = stTest.executeQuery(
                    "SELECT COUNT(*) AS n FROM materiel");
            if (rsTest.next()) {
                System.out.println("COUNT(*) brut sur materiel via " +
                        "la connexion de l'appli : " +
                        rsTest.getInt("n"));
            }
            java.sql.ResultSet rsInfo = stTest.executeQuery(
                    "SELECT DATABASE() AS db, @@hostname AS host, " +
                            "@@port AS port");
            if (rsInfo.next()) {
                System.out.println("Connecte a : base=" +
                        rsInfo.getString("db") + " host=" +
                        rsInfo.getString("host") + " port=" +
                        rsInfo.getInt("port"));
            }
        } catch (Exception ex) {
            System.out.println("Erreur test connexion : " + ex);
        }

        // Grouper par désignation + marque + modèle
        Map<String, LigneMateriel> groupes =
                new LinkedHashMap<>();
        int compteur = 0;
        for (Materiel m : tous) {
            try {
                String deptNom = m.getDepartement() != null ?
                        m.getDepartement().getNom() : "";
                String cle = m.getDesignation() + "|" +
                        m.getMarque() + "|" +
                        m.getModele() + "|" + deptNom;
                if (!groupes.containsKey(cle)) {
                    groupes.put(cle, new LigneMateriel(
                            m.getDesignation(),
                            m.getMarque(),
                            m.getModele(),
                            m.getTypeMateriel() != null ?
                                    m.getTypeMateriel().getLibelle() : "",
                            deptNom
                    ));
                }
                groupes.get(cle).ajouterUnite(m);
                compteur++;
            } catch (Exception ex) {
                System.out.println("ERREUR sur la ligne id_materiel="
                        + m.getIdMateriel() + " designation="
                        + m.getDesignation() + " -> " + ex);
                ex.printStackTrace();
            }
        }
        System.out.println("Lignes traitees sans erreur : " + compteur
                + " / " + tous.size());
        System.out.println("Nombre de groupes crees : " + groupes.size());
        donnees.addAll(groupes.values());
    }

    private void filtrerParRecherche(String terme) {
        if (terme == null || terme.trim().isEmpty()) {
            chargerDonnees();
            return;
        }
        String t = terme.toLowerCase();
        donnees.clear();
        List<Materiel> tous = utilisateur.isAdmin() ?
                dao.findAll() :
                dao.findByDepartement(
                        utilisateur.getDepartement()
                                .getIdDepartement());

        Map<String, LigneMateriel> groupes =
                new LinkedHashMap<>();
        for (Materiel m : tous) {
            boolean match =
                    m.getDesignation().toLowerCase().contains(t)
                            || (m.getMarque() != null &&
                            m.getMarque().toLowerCase().contains(t))
                            || (m.getModele() != null &&
                            m.getModele().toLowerCase().contains(t))
                            || (m.getNumeroSerie() != null &&
                            m.getNumeroSerie()
                                    .toLowerCase().contains(t));
            if (match) {
                String deptNom = m.getDepartement() != null ?
                        m.getDepartement().getNom() : "";
                String cle = m.getDesignation() + "|" +
                        m.getMarque() + "|" +
                        m.getModele() + "|" + deptNom;
                if (!groupes.containsKey(cle)) {
                    groupes.put(cle, new LigneMateriel(
                            m.getDesignation(),
                            m.getMarque(),
                            m.getModele(),
                            m.getTypeMateriel() != null ?
                                    m.getTypeMateriel()
                                            .getLibelle() : "",
                            deptNom
                    ));
                }
                groupes.get(cle).ajouterUnite(m);
            }
        }
        donnees.addAll(groupes.values());
    }

    // Afficher le détail des unités d'un groupe
    private void afficherDetail(LigneMateriel ligne) {
        Dialog<Void> dialog = new Dialog<>();
        utils.IconUtil.appliquerIcone(dialog);
        dialog.setTitle("Détail - " +
                ligne.getDesignation());
        dialog.setHeaderText(null);

        VBox contenu = new VBox(15);
        contenu.setPadding(new Insets(20));
        contenu.setMinWidth(600);

        Label lblTitre = new Label(
                ligne.getDesignation() + " — " +
                        ligne.getMarque() + " " + ligne.getModele());
        lblTitre.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );

        Label lblStats = new Label(
                "Disponibles : " + ligne.getQteDispo() +
                        " / Total : " + ligne.getQteTotal());
        lblStats.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #6B7280;"
        );

        // Tableau des unités
        TableView<Materiel> tableUnites =
                new TableView<>();
        tableUnites.setPrefHeight(300);
        tableUnites.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Materiel, String> colSerie =
                new TableColumn<>("N° Série / Code-barres");
        colSerie.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getNumeroSerie()));

        TableColumn<Materiel, String> colEtat =
                new TableColumn<>("État");
        colEtat.setMaxWidth(130);
        colEtat.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(
                    String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >=
                        tableUnites.getItems().size()) {
                    setGraphic(null);
                } else {
                    String etat = tableUnites.getItems()
                            .get(getIndex()).getEtat();
                    Label badge = new Label(etat);
                    badge.setStyle(getBadgeStyle(etat));
                    setGraphic(badge);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        // Actions sur les unités (admin seulement)
        if (utilisateur.isAdmin()) {
            TableColumn<Materiel, Void> colAct =
                    new TableColumn<>("Actions");
            colAct.setMaxWidth(120);
            colAct.setCellFactory(
                    col -> new TableCell<>() {
                        final Button btnEdit = new Button("✏");
                        final Button btnDel  = new Button("🗑");
                        final HBox box =
                                new HBox(8, btnEdit, btnDel);
                        {
                            box.setAlignment(Pos.CENTER);
                            btnEdit.setStyle(
                                    "-fx-background-color: #EEF2FF;" +
                                            "-fx-text-fill: #4F46E5;" +
                                            "-fx-background-radius: 6;" +
                                            "-fx-cursor: hand;"
                            );
                            btnDel.setStyle(
                                    "-fx-background-color: #FEE2E2;" +
                                            "-fx-text-fill: #EF4444;" +
                                            "-fx-background-radius: 6;" +
                                            "-fx-cursor: hand;"
                            );
                            btnEdit.setOnAction(e -> {
                                Materiel m = tableUnites
                                        .getItems().get(getIndex());
                                dialog.close();
                                Platform.runLater(() ->
                                        afficherFormulaire(m));
                            });
                            btnDel.setOnAction(e -> {
                                Materiel m = tableUnites
                                        .getItems().get(getIndex());
                                supprimerUnite(m, tableUnites,
                                        ligne);
                            });
                        }
                        @Override
                        protected void updateItem(
                                Void item, boolean empty) {
                            super.updateItem(item, empty);
                            setGraphic(empty ? null : box);
                        }
                    });
            tableUnites.getColumns().addAll(
                    colSerie, colEtat, colAct);
        } else {
            tableUnites.getColumns().addAll(
                    colSerie, colEtat);
        }

        ObservableList<Materiel> unites =
                FXCollections.observableArrayList(
                        ligne.getUnites());
        tableUnites.setItems(unites);

        contenu.getChildren().addAll(
                lblTitre, lblStats, tableUnites);
        dialog.getDialogPane().setContent(contenu);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: white;");

        ButtonType btnFermer = new ButtonType(
                "Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes()
                .add(btnFermer);

        dialog.showAndWait();
        chargerDonnees();
    }

    private void supprimerUnite(Materiel m,
                                TableView<Materiel> table,
                                LigneMateriel ligne) {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION);
        utils.IconUtil.appliquerIcone(confirm);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Supprimer l'unité " +
                        m.getNumeroSerie() + " ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (dao.supprimer(m.getIdMateriel())) {
                    table.getItems().remove(m);
                    ligne.getUnites().remove(m);
                    afficherSucces("Unité supprimée !");
                    chargerDonnees();
                }
            }
        });
    }

    private void afficherFormulaireNouvelleUnite(
            LigneMateriel ligne) {
        // Pré-remplir avec les infos du groupe
        Dialog<Materiel> dialog = new Dialog<>();
        utils.IconUtil.appliquerIcone(dialog);
        dialog.setTitle("Ajouter une unité");
        dialog.setHeaderText(null);

        VBox contenu = new VBox(12);
        contenu.setPadding(new Insets(20));
        contenu.setMinWidth(400);

        Label lblTitre = new Label(
                "Nouvelle unité — " + ligne.getDesignation());
        lblTitre.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );

        TextField champSerie =
                creerChamp("N° Série / Code-barres");
        TextField champDate = creerChamp("jj/MM/aaaa");

        contenu.getChildren().addAll(
                lblTitre,
                creerLigneChamp("N° Série *", champSerie),
                creerLigneChamp("Date Achat", champDate)
        );

        dialog.getDialogPane().setContent(contenu);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: white;");

        ButtonType btnSave = new ButtonType(
                "Ajouter", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType(
                "Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(
                btnSave, btnAnnuler);

        Button btnS = (Button) dialog.getDialogPane()
                .lookupButton(btnSave);
        btnS.setStyle(
                "-fx-background-color: #1E2840;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );

        // Récupérer le type et département
        // depuis la première unité du groupe
        Materiel modele = ligne.getUnites().isEmpty() ?
                null : ligne.getUnites().get(0);

        dialog.setResultConverter(bt -> {
            if (bt == btnSave) {
                if (champSerie.getText().trim().isEmpty()) {
                    afficherErreur(
                            "Le N° Série est obligatoire !");
                    return null;
                }
                try {
                    return new Materiel(0,
                            ligne.getDesignation(),
                            ligne.getMarque(),
                            ligne.getModele(),
                            champSerie.getText().trim(),
                            champDate.getText().trim().isEmpty()
                                    ? null : SDF.parse(
                                    champDate.getText().trim()),
                            "Disponible",
                            modele != null ?
                                    modele.getTypeMateriel() : null,
                            modele != null ?
                                    modele.getDepartement() : null
                    );
                } catch (ParseException ex) {
                    afficherErreur(
                            "Format date invalide ! (jj/MM/aaaa)");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(m -> {
            if (dao.ajouter(m)) {
                afficherSucces("Unité ajoutée !");
                chargerDonnees();
            }
        });
    }

    private void afficherFormulaire(Materiel mat) {
        Dialog<Materiel> dialog = new Dialog<>();
        utils.IconUtil.appliquerIcone(dialog);
        dialog.setTitle(mat == null ?
                "Nouveau matériel" : "Modifier matériel");
        dialog.setHeaderText(null);

        VBox contenu = new VBox(12);
        contenu.setPadding(new Insets(20));
        contenu.setMinWidth(500);

        Label lblTitre = new Label(mat == null ?
                "Nouveau matériel" : "Modifier matériel");
        lblTitre.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );

        TextField champDesig  =
                creerChamp("Désignation");
        TextField champMarque = creerChamp("Marque");
        TextField champModele = creerChamp("Modèle");
        TextField champSerie  =
                creerChamp("Numéro de série");
        TextField champDate   = creerChamp("jj/MM/aaaa");

        ComboBox<String> comboEtat = new ComboBox<>();
        comboEtat.getItems().addAll(
                "Disponible", "Affecté",
                "En panne", "Hors service");
        comboEtat.setValue("Disponible");
        comboEtat.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 13px;"
        );
        comboEtat.setMaxWidth(Double.MAX_VALUE);
        comboEtat.setPrefHeight(42);

        ComboBox<Departement> comboDept = new ComboBox<>();
        comboDept.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 13px;"
        );
        comboDept.setMaxWidth(Double.MAX_VALUE);
        comboDept.setPrefHeight(42);
        List<Departement> depts = daoD.findAll();
        comboDept.getItems().addAll(depts);

        ComboBox<TypeMateriel> comboType = new ComboBox<>();
        comboType.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 13px;"
        );
        comboType.setMaxWidth(Double.MAX_VALUE);
        comboType.setPrefHeight(42);

        comboDept.setOnAction(e -> {
            comboType.getItems().clear();
            comboType.setValue(null);
            if (comboDept.getValue() != null) {
                comboType.getItems().addAll(
                        daoT.findByDepartement(
                                comboDept.getValue()
                                        .getIdDepartement()));
            }
        });

        if (mat != null) {
            champDesig.setText(mat.getDesignation());
            champMarque.setText(mat.getMarque() != null ?
                    mat.getMarque() : "");
            champModele.setText(mat.getModele() != null ?
                    mat.getModele() : "");
            champSerie.setText(mat.getNumeroSerie());
            comboEtat.setValue(mat.getEtat());
            if (mat.getDateAchat() != null)
                champDate.setText(
                        SDF.format(mat.getDateAchat()));
            for (Departement d : depts) {
                if (mat.getDepartement() != null &&
                        d.getIdDepartement() ==
                                mat.getDepartement()
                                        .getIdDepartement()) {
                    comboDept.setValue(d);
                    break;
                }
            }
            if (comboDept.getValue() != null) {
                List<TypeMateriel> types =
                        daoT.findByDepartement(
                                comboDept.getValue()
                                        .getIdDepartement());
                comboType.getItems().setAll(types);
                for (TypeMateriel t : types) {
                    if (mat.getTypeMateriel() != null &&
                            t.getIdType() ==
                                    mat.getTypeMateriel()
                                            .getIdType()) {
                        final TypeMateriel typeASelectionner = t;
                        Platform.runLater(() ->
                                comboType.getSelectionModel()
                                        .select(typeASelectionner));
                        break;
                    }
                }
            }
        } else if (!depts.isEmpty()) {
            comboDept.setValue(depts.get(0));
        }

        HBox ligne1 = new HBox(12,
                creerLigneChamp("Désignation *", champDesig),
                creerLigneChamp("N° Série *", champSerie));
        HBox ligne2 = new HBox(12,
                creerLigneChamp("Marque", champMarque),
                creerLigneChamp("Modèle", champModele));

        VBox boxEtat = new VBox(6);
        Label lblEtat = new Label("État");
        lblEtat.setStyle("-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #374151;");
        boxEtat.getChildren().addAll(lblEtat, comboEtat);

        VBox boxDept = new VBox(6);
        Label lblDept = new Label("Département *");
        lblDept.setStyle("-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #374151;");
        boxDept.getChildren().addAll(lblDept, comboDept);
        HBox.setHgrow(boxDept, Priority.ALWAYS);

        VBox boxType = new VBox(6);
        Label lblType = new Label("Type *");
        lblType.setStyle("-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #374151;");
        boxType.getChildren().addAll(lblType, comboType);
        HBox.setHgrow(boxType, Priority.ALWAYS);

        HBox ligne3 = new HBox(12,
                creerLigneChamp("Date Achat", champDate),
                boxEtat);
        HBox ligne4 = new HBox(12, boxDept, boxType);

        contenu.getChildren().addAll(
                lblTitre, ligne1, ligne2, ligne3, ligne4);

        dialog.getDialogPane().setContent(contenu);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: white;");

        ButtonType btnSave = new ButtonType(
                "Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType(
                "Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(
                btnSave, btnAnnuler);

        Button btnS = (Button) dialog.getDialogPane()
                .lookupButton(btnSave);
        btnS.setStyle(
                "-fx-background-color: #1E2840;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );

        dialog.setResultConverter(bt -> {
            if (bt == btnSave) {
                if (champDesig.getText().trim().isEmpty()
                        || champSerie.getText().trim().isEmpty()){
                    afficherErreur(
                            "Désignation et N° Série " +
                                    "sont obligatoires !");
                    return null;
                }
                if (comboType.getValue() == null) {
                    afficherErreur(
                            "Choisissez un type !");
                    return null;
                }
                try {
                    return new Materiel(
                            mat != null ?
                                    mat.getIdMateriel() : 0,
                            champDesig.getText().trim(),
                            champMarque.getText().trim(),
                            champModele.getText().trim(),
                            champSerie.getText().trim(),
                            champDate.getText().trim().isEmpty()
                                    ? null : SDF.parse(
                                    champDate.getText().trim()),
                            comboEtat.getValue(),
                            comboType.getValue(),
                            comboDept.getValue()
                    );
                } catch (ParseException ex) {
                    afficherErreur(
                            "Format date invalide !" +
                                    " (jj/MM/aaaa)");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(m -> {
            if (mat == null) {
                if (dao.ajouter(m)) {
                    afficherSucces("Matériel ajouté !");
                    chargerDonnees();
                }
            } else {
                if (dao.modifier(m)) {
                    afficherSucces("Matériel modifié !");
                    chargerDonnees();
                }
            }
        });
    }

    private String getBadgeStyle(String etat) {
        return switch (etat) {
            case "Disponible" ->
                    "-fx-background-color: #DCFCE7;" +
                            "-fx-text-fill: #16A34A;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 11px;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 4 10 4 10;";
            case "Affecté" ->
                    "-fx-background-color: #DBEAFE;" +
                            "-fx-text-fill: #2563EB;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 11px;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 4 10 4 10;";
            case "En panne" ->
                    "-fx-background-color: #FEE2E2;" +
                            "-fx-text-fill: #DC2626;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 11px;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 4 10 4 10;";
            default ->
                    "-fx-background-color: #F3F4F6;" +
                            "-fx-text-fill: #6B7280;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 11px;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 4 10 4 10;";
        };
    }

    private TextField creerChamp(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-font-size: 13px;"
        );
        tf.setPrefHeight(42);
        return tf;
    }

    private VBox creerLigneChamp(String label,
                                 TextField champ) {
        VBox box = new VBox(6);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        Label lbl = new Label(label);
        lbl.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #374151;"
        );
        champ.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(lbl, champ);
        return box;
    }

    private void afficherSucces(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        utils.IconUtil.appliquerIcone(a);
        a.setTitle("Succès");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void afficherErreur(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        utils.IconUtil.appliquerIcone(a);
        a.setTitle("Erreur");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
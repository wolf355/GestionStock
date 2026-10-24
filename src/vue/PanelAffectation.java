package vue;

import dao.AffectationDAO;
import dao.EmployeDAO;
import dao.MaterielDAO;
import modele.Affectation;
import modele.Employe;
import modele.Materiel;
import modele.Utilisateur;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.StringConverter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PanelAffectation extends VBox {

    private AffectationDAO daoAff = new AffectationDAO();
    private EmployeDAO     daoEmp = new EmployeDAO();
    private MaterielDAO    daoMat = new MaterielDAO();
    private Utilisateur    utilisateur;

    private TableView<Affectation> tableEnCours;
    private TableView<Affectation> tableHistorique;
    private ObservableList<Affectation> donneesEnCours;
    private ObservableList<Affectation> donneesHistorique;
    private List<Affectation> masterEnCours = new ArrayList<>();
    private List<Affectation> masterHistorique = new ArrayList<>();
    private TextField champFiltreNaxEnCours;
    private TextField champFiltreNaxHistorique;
    private ComboBox<String> comboFiltreStatut;

    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy");

    public PanelAffectation(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        setStyle("-fx-background-color: #F5F6FA;");
        setPadding(new Insets(30));
        setSpacing(20);
        VBox.setVgrow(this, Priority.ALWAYS);
        initComposants();
        chargerDonnees();
    }

    private void initComposants() {

        // TITRE + BOUTON
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitre = new Label(
                "Gestion des Affectations");
        lblTitre.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );
        HBox.setHgrow(lblTitre, Priority.NEVER);

        Button btnNouveau =
                new Button("+ Nouvelle affectation");
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
                e -> afficherFormulaireAffectation());
        Region espaceurTitre = new Region();
        HBox.setHgrow(espaceurTitre, Priority.ALWAYS);
        espaceurTitre.setMinWidth(40);
        topBar.getChildren().addAll(lblTitre, espaceurTitre, btnNouveau);

        // TABS
        TabPane tabs = new TabPane();
        tabs.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(tabs, Priority.ALWAYS);

        // TAB 1 : EN COURS
        Tab tabEnCours =
                new Tab("📋  Affectations en cours");
        tabEnCours.setClosable(false);
        tableEnCours = creerTableau(true);
        donneesEnCours =
                FXCollections.observableArrayList();
        tableEnCours.setItems(donneesEnCours);

        champFiltreNaxEnCours = new TextField();
        champFiltreNaxEnCours.setPromptText(
                "Filtrer par NAX...");
        champFiltreNaxEnCours.setPrefWidth(250);
        champFiltreNaxEnCours.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-font-size: 13px;"
        );
        champFiltreNaxEnCours.textProperty().addListener(
                (obs, oldV, newV) -> appliquerFiltreEnCours());
        HBox barreFiltreEnCours = new HBox(10,
                new Label("🔍"), champFiltreNaxEnCours);
        barreFiltreEnCours.setAlignment(Pos.CENTER_LEFT);

        VBox contEnCours = new VBox(12,
                barreFiltreEnCours, tableEnCours);
        VBox.setVgrow(tableEnCours, Priority.ALWAYS);
        contEnCours.setPadding(new Insets(15, 0, 0, 0));
        tabEnCours.setContent(contEnCours);

        // TAB 2 : HISTORIQUE
        Tab tabHistorique =
                new Tab("📜  Historique complet");
        tabHistorique.setClosable(false);
        tableHistorique = creerTableau(false);
        donneesHistorique =
                FXCollections.observableArrayList();
        tableHistorique.setItems(donneesHistorique);

        champFiltreNaxHistorique = new TextField();
        champFiltreNaxHistorique.setPromptText(
                "Filtrer par NAX...");
        champFiltreNaxHistorique.setPrefWidth(220);
        champFiltreNaxHistorique.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-font-size: 13px;"
        );
        champFiltreNaxHistorique.textProperty().addListener(
                (obs, oldV, newV) -> appliquerFiltreHistorique());

        comboFiltreStatut = new ComboBox<>();
        comboFiltreStatut.getItems().addAll(
                "Tous les statuts", "En cours", "Clôturée");
        comboFiltreStatut.setValue("Tous les statuts");
        comboFiltreStatut.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;"
        );
        comboFiltreStatut.setOnAction(
                e -> appliquerFiltreHistorique());

        HBox barreFiltreHisto = new HBox(10,
                new Label("🔍"), champFiltreNaxHistorique,
                comboFiltreStatut);
        barreFiltreHisto.setAlignment(Pos.CENTER_LEFT);

        VBox contHisto =
                new VBox(12, barreFiltreHisto, tableHistorique);
        VBox.setVgrow(tableHistorique, Priority.ALWAYS);
        contHisto.setPadding(new Insets(15, 0, 0, 0));
        tabHistorique.setContent(contHisto);

        tabs.getTabs().addAll(tabEnCours, tabHistorique);
        getChildren().addAll(topBar, tabs);
    }

    private TableView<Affectation> creerTableau(
            boolean enCours) {
        TableView<Affectation> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, " +
                        "rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        // Colonne ID
        TableColumn<Affectation, String> colId =
                new TableColumn<>("ID");
        colId.setMaxWidth(70);
        colId.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(
                    String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >=
                        table.getItems().size()) {
                    setText(null);
                } else {
                    setText("#" + table.getItems()
                            .get(getIndex())
                            .getIdAffectation());
                }
            }
        });

        // Colonne Employé avec NAX
        TableColumn<Affectation, String> colEmp =
                new TableColumn<>("Employé (NAX)");
        colEmp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(
                    String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >=
                        table.getItems().size()) {
                    setGraphic(null);
                } else {
                    Affectation a =
                            table.getItems().get(getIndex());
                    Employe e = a.getEmploye();
                    if (e == null) {
                        setText(""); return;
                    }
                    String ini =
                            (e.getNom().isEmpty() ? "" :
                                    String.valueOf(e.getNom().charAt(0)))
                                    + (e.getPrenom().isEmpty() ? "" :
                                    String.valueOf(
                                            e.getPrenom().charAt(0)));
                    Label avatar =
                            new Label(ini.toUpperCase());
                    avatar.setStyle(
                            "-fx-background-color: #EEF2FF;" +
                                    "-fx-text-fill: #4F46E5;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-min-width: 32px;" +
                                    "-fx-min-height: 32px;" +
                                    "-fx-alignment: center;" +
                                    "-fx-background-radius: 50%;"
                    );
                    VBox info = new VBox(2);
                    Label nom = new Label(
                            e.getNom() + " " + e.getPrenom());
                    nom.setStyle(
                            "-fx-font-size: 12px;" +
                                    "-fx-font-weight: bold;"
                    );
                    Label nax = new Label(
                            "NAX: " + (e.getNax() != null ?
                                    e.getNax() : ""));
                    nax.setStyle(
                            "-fx-font-size: 11px;" +
                                    "-fx-text-fill: #6B7280;"
                    );
                    info.getChildren().addAll(nom, nax);
                    HBox box = new HBox(8, avatar, info);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        // Colonne Matériel
        TableColumn<Affectation, String> colMat =
                new TableColumn<>("Matériel");
        colMat.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(
                    String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >=
                        table.getItems().size()) {
                    setText(null);
                } else {
                    Materiel m = table.getItems()
                            .get(getIndex()).getMateriel();
                    if (m != null) {
                        setText(m.getDesignation() +
                                "\n" + m.getNumeroSerie());
                    }
                }
            }
        });

        // Date affectation
        TableColumn<Affectation, String> colDateAff =
                new TableColumn<>("Date affectation");
        colDateAff.setCellFactory(col -> new TableCell<>(){
            @Override
            protected void updateItem(
                    String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >=
                        table.getItems().size()) {
                    setText(null);
                } else {
                    Affectation a = table.getItems()
                            .get(getIndex());
                    setText(a.getDateAffectation() != null ?
                            SDF.format(
                                    a.getDateAffectation()) : "");
                }
            }
        });

        table.getColumns().addAll(
                colId, colEmp, colMat, colDateAff);

        if (!enCours) {
            // Date retour
            TableColumn<Affectation, String> colDateRet =
                    new TableColumn<>("Date retour");
            colDateRet.setCellFactory(
                    col -> new TableCell<>() {
                        @Override
                        protected void updateItem(
                                String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || getIndex() >=
                                    table.getItems().size()) {
                                setText(null);
                            } else {
                                Affectation a = table.getItems()
                                        .get(getIndex());
                                setText(a.getDateRetour() != null ?
                                        SDF.format(a.getDateRetour())
                                        : "-");
                            }
                        }
                    });

            // Badge statut
            TableColumn<Affectation, String> colStatut =
                    new TableColumn<>("Statut");
            colStatut.setMaxWidth(120);
            colStatut.setCellFactory(
                    col -> new TableCell<>() {
                        @Override
                        protected void updateItem(
                                String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || getIndex() >=
                                    table.getItems().size()) {
                                setGraphic(null);
                            } else {
                                Affectation a = table.getItems()
                                        .get(getIndex());
                                boolean ec = a.estEnCours();
                                Label badge = new Label(
                                        ec ? "En cours" : "Clôturée");
                                badge.setStyle(ec ?
                                        "-fx-background-color: #FEF3C7;" +
                                        "-fx-text-fill: #D97706;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 11px;" +
                                        "-fx-background-radius: 20;" +
                                        "-fx-padding: 4 10 4 10;" :
                                        "-fx-background-color: #F3F4F6;"+
                                        "-fx-text-fill: #6B7280;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 11px;" +
                                        "-fx-background-radius: 20;" +
                                        "-fx-padding: 4 10 4 10;"
                                );
                                setGraphic(badge);
                                setStyle("-fx-alignment: CENTER;");
                            }
                        }
                    });

            // Remarque
            TableColumn<Affectation, String> colRem =
                    new TableColumn<>("Remarque");
            colRem.setCellFactory(
                    col -> new TableCell<>() {
                        @Override
                        protected void updateItem(
                                String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || getIndex() >=
                                    table.getItems().size()) {
                                setText(null);
                            } else {
                                setText(table.getItems()
                                        .get(getIndex())
                                        .getRemarque());
                            }
                        }
                    });

            table.getColumns().addAll(
                    colDateRet, colStatut, colRem);

        } else {
            // Bouton Récupérer
            TableColumn<Affectation, Void> colActions =
                    new TableColumn<>("Actions");
            colActions.setMaxWidth(140);
            colActions.setMinWidth(140);
            colActions.setCellFactory(
                    col -> new TableCell<>() {
                        final Button btnRecup =
                                new Button("🔄  Récupérer");
                        {
                            btnRecup.setStyle(
                                    "-fx-background-color: #DBEAFE;" +
                                            "-fx-text-fill: #2563EB;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-font-size: 11px;" +
                                            "-fx-background-radius: 6;" +
                                            "-fx-cursor: hand;"
                            );
                            btnRecup.setOnAction(e -> {
                                Affectation a = getTableView()
                                        .getItems().get(getIndex());
                                recupererMateriel(a);
                            });
                        }
                        @Override
                        protected void updateItem(
                                Void item, boolean empty) {
                            super.updateItem(item, empty);
                            setGraphic(empty ? null : btnRecup);
                            setStyle("-fx-alignment: CENTER;");
                        }
                    });
            table.getColumns().add(colActions);
        }
        return table;
    }

    private void chargerDonnees() {
        List<Affectation> enCours =
                daoAff.findEnCours();
        List<Affectation> historique =
                daoAff.findAll();

        // Filtrer selon le rôle
        if (!utilisateur.isAdmin()) {
            int idDept = utilisateur.getDepartement()
                    .getIdDepartement();
            enCours.removeIf(a ->
                    a.getMateriel() == null ||
                            a.getMateriel().getDepartement() == null ||
                            a.getMateriel().getDepartement()
                                    .getIdDepartement() != idDept);
            historique.removeIf(a ->
                    a.getMateriel() == null ||
                            a.getMateriel().getDepartement() == null ||
                            a.getMateriel().getDepartement()
                                    .getIdDepartement() != idDept);
        }

        masterEnCours = enCours;
        masterHistorique = historique;

        appliquerFiltreEnCours();
        appliquerFiltreHistorique();
    }

    private void appliquerFiltreEnCours() {
        if (donneesEnCours == null) return;
        String filtre = champFiltreNaxEnCours == null ? "" :
                champFiltreNaxEnCours.getText();
        String f = filtre == null ? "" :
                filtre.trim().toLowerCase();

        List<Affectation> resultat = masterEnCours.stream()
                .filter(a -> f.isEmpty() ||
                        (a.getEmploye() != null &&
                                a.getEmploye().getNax() != null &&
                                a.getEmploye().getNax()
                                        .toLowerCase().contains(f)))
                .collect(Collectors.toList());

        donneesEnCours.setAll(resultat);
    }

    private void appliquerFiltreHistorique() {
        if (donneesHistorique == null) return;
        String filtre = champFiltreNaxHistorique == null ? "" :
                champFiltreNaxHistorique.getText();
        String f = filtre == null ? "" :
                filtre.trim().toLowerCase();
        String statut = comboFiltreStatut == null ? null :
                comboFiltreStatut.getValue();

        List<Affectation> resultat = masterHistorique.stream()
                .filter(a -> f.isEmpty() ||
                        (a.getEmploye() != null &&
                                a.getEmploye().getNax() != null &&
                                a.getEmploye().getNax()
                                        .toLowerCase().contains(f)))
                .filter(a -> statut == null ||
                        "Tous les statuts".equals(statut) ||
                        ("En cours".equals(statut) &&
                                a.estEnCours()) ||
                        ("Clôturée".equals(statut) &&
                                !a.estEnCours()))
                .collect(Collectors.toList());

        donneesHistorique.setAll(resultat);
    }

    private void afficherFormulaireAffectation() {
        Dialog<Boolean> dialog = new Dialog<>();
        utils.IconUtil.appliquerIcone(dialog);
        dialog.setTitle("Nouvelle affectation");
        dialog.setHeaderText(null);

        VBox contenu = new VBox(15);
        contenu.setPadding(new Insets(20));
        contenu.setMinWidth(550);

        Label lblTitre = new Label("Nouvelle affectation");
        lblTitre.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );

        // Employé - ComboBox editable avec recherche live par NAX/nom
        List<Employe> tousEmployes = daoEmp.findAll();
        ComboBox<Employe> comboEmp = new ComboBox<>();
        comboEmp.setEditable(true);
        comboEmp.setMaxWidth(Double.MAX_VALUE);
        comboEmp.setPrefHeight(42);
        comboEmp.setPromptText(
                "Tapez un NAX ou un nom pour chercher...");
        comboEmp.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 13px;"
        );
        comboEmp.getItems().addAll(tousEmployes);

        StringConverter<Employe> convertisseurEmp =
                new StringConverter<>() {
                    @Override
                    public String toString(Employe e) {
                        return e == null ? "" :
                                e.getNom() + " " + e.getPrenom() +
                                " (" + e.getNax() + ")";
                    }
                    @Override
                    public Employe fromString(String saisie) {
                        if (saisie == null) return null;
                        String s = saisie.trim().toLowerCase();
                        for (Employe e : tousEmployes) {
                            if (e.getNax() != null && e.getNax()
                                    .toLowerCase().equals(s)) {
                                return e;
                            }
                        }
                        return null; // pas de correspondance exacte -> non selectionne
                    }
                };
        comboEmp.setConverter(convertisseurEmp);

        // Trace fiable de l'employe reellement clique/valide, separee
        // de comboEmp.getValue() qui peut etre remis a null par
        // JavaFX quand le focus quitte le champ editable
        final Employe[] employeSelectionne = new Employe[1];

        comboEmp.setOnAction(e -> {
            if (comboEmp.getValue() != null) {
                employeSelectionne[0] = comboEmp.getValue();
            }
        });

        comboEmp.getEditor().textProperty().addListener(
                (obs, ancien, nouveau) -> {
                    // Evite de re-filtrer quand le texte vient d'une
                    // selection deja faite (le converter vient de l'ecrire)
                    if (comboEmp.getValue() != null &&
                            convertisseurEmp.toString(comboEmp.getValue())
                                    .equals(nouveau)) {
                        return;
                    }
                    String f = nouveau == null ? "" :
                            nouveau.trim().toLowerCase();
                    List<Employe> filtres = tousEmployes.stream()
                            .filter(e ->
                                    (e.getNax() != null && e.getNax()
                                            .toLowerCase().contains(f)) ||
                                            (e.getNom() != null && e.getNom()
                                                    .toLowerCase().contains(f)) ||
                                            (e.getPrenom() != null && e.getPrenom()
                                                    .toLowerCase().contains(f)))
                            .collect(Collectors.toList());
                    Platform.runLater(() -> {
                        comboEmp.hide();
                        comboEmp.getItems().setAll(filtres);
                        comboEmp.getEditor().setText(nouveau);
                        comboEmp.getEditor().positionCaret(
                                nouveau == null ? 0 : nouveau.length());
                        if (!f.isEmpty() && !filtres.isEmpty()) {
                            comboEmp.show();
                        }
                    });
                });

        // Quand le focus quitte le champ (ex: clic sur la liste de
        // materiel juste en dessous), JavaFX tente de "valider" le
        // texte affiche et peut remettre la valeur a null si le
        // texte ne correspond pas exactement a un NAX. On restaure
        // alors l'affichage correct a partir de employeSelectionne.
        comboEmp.getEditor().focusedProperty().addListener(
                (obs, avaitFocus, aFocus) -> {
                    if (!aFocus && employeSelectionne[0] != null) {
                        Platform.runLater(() -> {
                            comboEmp.setValue(employeSelectionne[0]);
                            comboEmp.getEditor().setText(
                                    convertisseurEmp.toString(
                                            employeSelectionne[0]));
                        });
                    }
                });

        if (!comboEmp.getItems().isEmpty()) {
            comboEmp.setValue(comboEmp.getItems().get(0));
            employeSelectionne[0] = comboEmp.getItems().get(0);
        }

        // Liste matériels disponibles avec cases à cocher
        List<Materiel> dispos;
        if (utilisateur.isAdmin()) {
            dispos = daoMat.findDisponibles();
        } else {
            // Filtrer par département du responsable
            dispos = daoMat.findDisponibles().stream()
                    .filter(m ->
                            m.getDepartement() != null &&
                                    m.getDepartement().getIdDepartement()
                                            == utilisateur.getDepartement()
                                            .getIdDepartement())
                    .toList();
        }

        // ListView avec cases à cocher
        ListView<Materiel> listeMat = new ListView<>();
        listeMat.setPrefHeight(200);
        listeMat.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-font-size: 13px;"
        );

        // Map pour suivre les sélections
        java.util.Map<Materiel, SimpleBooleanProperty>
                selections = new java.util.LinkedHashMap<>();
        for (Materiel m : dispos) {
            selections.put(m,
                    new SimpleBooleanProperty(false));
        }

        listeMat.setCellFactory(
                CheckBoxListCell.forListView(
                        item -> selections.get(item)));
        listeMat.getItems().addAll(dispos);

        TextField champRechercheMat = new TextField();
        champRechercheMat.setPromptText(
                "Rechercher un matériel (ex: bureau)...");
        champRechercheMat.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 12 8 12;" +
                        "-fx-font-size: 13px;"
        );
        champRechercheMat.textProperty().addListener(
                (obs, ancien, nouveau) -> {
                    String f = nouveau == null ? "" :
                            nouveau.trim().toLowerCase();
                    List<Materiel> filtres = dispos.stream()
                            .filter(m ->
                                    (m.getDesignation() != null &&
                                            m.getDesignation().toLowerCase()
                                                    .contains(f)) ||
                                            (m.getMarque() != null &&
                                                    m.getMarque().toLowerCase()
                                                            .contains(f)) ||
                                            (m.getModele() != null &&
                                                    m.getModele().toLowerCase()
                                                            .contains(f)) ||
                                            (m.getNumeroSerie() != null &&
                                                    m.getNumeroSerie().toLowerCase()
                                                            .contains(f)))
                            .collect(Collectors.toList());
                    listeMat.getItems().setAll(filtres);
                });

        // Date
        TextField champDate = new TextField(
                SDF.format(new java.util.Date()));
        champDate.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-font-size: 13px;"
        );
        champDate.setPrefHeight(42);

        // Remarque
        TextField champRem = new TextField();
        champRem.setPromptText("Note interne...");
        champRem.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-font-size: 13px;"
        );
        champRem.setPrefHeight(42);

        VBox boxEmp = creerBoxLabel(
                "Employé *", comboEmp);

        VBox boxMat = new VBox(6);
        Label lblMat = new Label(
                "Matériels disponibles * " +
                        "(cochez pour sélectionner)");
        lblMat.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #374151;"
        );
        boxMat.getChildren().addAll(
                lblMat, champRechercheMat, listeMat);

        HBox ligne1 = new HBox(12, boxEmp);
        HBox.setHgrow(boxEmp, Priority.ALWAYS);

        HBox ligne2 = new HBox(12,
                creerLigneChamp(
                        "Date d'affectation *", champDate),
                creerLigneChamp("Remarque", champRem));

        contenu.getChildren().addAll(
                lblTitre, ligne1, boxMat, ligne2);

        dialog.getDialogPane().setContent(contenu);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: white;");

        ButtonType btnAff = new ButtonType(
                "Affecter les matériels",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType(
                "Annuler",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(
                btnAff, btnAnnuler);

        Button btnA = (Button) dialog.getDialogPane()
                .lookupButton(btnAff);
        btnA.setStyle(
                "-fx-background-color: #1E2840;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 20 10 20;"
        );

        dialog.setResultConverter(bt -> {
            if (bt == btnAff) {
                Employe empChoisi = employeSelectionne[0];
                if (empChoisi == null) {
                    afficherErreur(
                            "Sélectionnez un employé dans la " +
                                    "liste (cliquez dessus ou " +
                                    "tapez son NAX exact) !");
                    return null;
                }

                // Récupérer les matériels cochés
                List<Materiel> selectionnes =
                        new ArrayList<>();
                for (Materiel m : dispos) {
                    if (selections.get(m).get()) {
                        selectionnes.add(m);
                    }
                }

                if (selectionnes.isEmpty()) {
                    afficherErreur(
                            "Cochez au moins un matériel !");
                    return null;
                }

                try {
                    java.util.Date date = SDF.parse(
                            champDate.getText().trim());
                    int nbOk = 0;

                    // Créer une affectation par matériel
                    for (Materiel m : selectionnes) {
                        Affectation a = new Affectation(
                                0, date, null,
                                champRem.getText().trim(),
                                empChoisi, m);
                        if (daoAff.ajouter(a)) {
                            daoMat.modifierEtat(
                                    m.getIdMateriel(),
                                    "Affecté");
                            nbOk++;
                        }
                    }

                    if (nbOk > 0) {
                        afficherSucces(nbOk +
                                " matériel(s) affecté(s)" +
                                " avec succès !");
                        return true;
                    }
                } catch (Exception ex) {
                    afficherErreur(
                            "Format date invalide !" +
                                    " (jj/MM/aaaa)");
                }
                return null;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result) chargerDonnees();
        });
    }

    private void recupererMateriel(Affectation a) {
        TextInputDialog inputDialog =
                new TextInputDialog(
                        SDF.format(new java.util.Date()));
        inputDialog.setTitle("Récupérer le matériel");
        inputDialog.setHeaderText(null);
        inputDialog.setContentText(
                "Date de retour (jj/MM/aaaa) :");

        Optional<String> result =
                inputDialog.showAndWait();
        result.ifPresent(dateStr -> {
            try {
                java.util.Date date =
                        SDF.parse(dateStr.trim());
                Date sqlDate = new Date(date.getTime());
                if (daoAff.recuperer(
                        a.getIdAffectation(), sqlDate)) {
                    daoMat.modifierEtat(
                            a.getMateriel().getIdMateriel(),
                            "Disponible");
                    afficherSucces(
                            "Matériel récupéré avec succès !");
                    chargerDonnees();
                }
            } catch (Exception ex) {
                afficherErreur(
                        "Format date invalide ! (jj/MM/aaaa)");
            }
        });
    }

    private VBox creerBoxLabel(String label,
                               Control control) {
        VBox box = new VBox(6);
        box.setMaxWidth(Double.MAX_VALUE);
        Label lbl = new Label(label);
        lbl.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #374151;"
        );
        control.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(lbl, control);
        return box;
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
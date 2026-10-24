package vue;

import dao.DepartementDAO;
import dao.EmployeDAO;
import modele.Departement;
import modele.Employe;
import modele.Utilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

public class PanelEmploye extends VBox {

    private EmployeDAO     dao  = new EmployeDAO();
    private DepartementDAO daoD = new DepartementDAO();
    private TableView<Employe> table;
    private ObservableList<Employe> donnees;
    private TextField champRecherche;
    private Utilisateur utilisateur;
    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd/MM/yyyy");

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@modep\\.ma$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern TEL_PATTERN =
            Pattern.compile("^\\d{1,10}$");

    public PanelEmploye(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        setStyle("-fx-background-color: #F5F6FA;");
        setPadding(new Insets(30));
        setSpacing(20);
        VBox.setVgrow(this, Priority.ALWAYS);
        initComposants();
        chargerDonnees();
    }

    private void initComposants() {

        // TITRE + RECHERCHE + BOUTON
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitre = new Label("Gestion des Employés");
        lblTitre.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );
        HBox.setHgrow(lblTitre, Priority.NEVER);

        champRecherche = new TextField();
        champRecherche.setPromptText("🔍  Rechercher...");
        champRecherche.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 14 8 14;" +
                        "-fx-font-size: 13px;"
        );
        champRecherche.setPrefWidth(250);
        champRecherche.setPrefHeight(40);
        champRecherche.textProperty().addListener(
                (obs, old, nw) -> rechercher(nw));

        Region espaceurTitre = new Region();
        HBox.setHgrow(espaceurTitre, Priority.ALWAYS);
        espaceurTitre.setMinWidth(40);
        topBar.getChildren().addAll(lblTitre, espaceurTitre, champRecherche);

        // Bouton ajouter uniquement pour Admin
        if (utilisateur.isAdmin()) {
            Button btnNouveau =
                    new Button("+ Ajouter un employé");
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

        // TABLEAU
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

        // Colonne NAX
        TableColumn<Employe, String> colNax =
                new TableColumn<>("NAX");
        colNax.setMaxWidth(100);
        colNax.setMinWidth(100);
        colNax.setCellValueFactory(
                new PropertyValueFactory<>("nax"));

        // Colonne Nom avec avatar
        TableColumn<Employe, String> colNom =
                new TableColumn<>("Nom complet");
        colNom.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(
                    String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >=
                        table.getItems().size()) {
                    setGraphic(null);
                } else {
                    Employe e =
                            table.getItems().get(getIndex());
                    String ini =
                            (e.getNom().isEmpty() ? "" :
                                    String.valueOf(e.getNom().charAt(0)))
                                    + (e.getPrenom().isEmpty() ? "" :
                                    String.valueOf(
                                            e.getPrenom().charAt(0)));
                    Label avatar = new Label(
                            ini.toUpperCase());
                    avatar.setStyle(
                            "-fx-background-color: #EEF2FF;" +
                                    "-fx-text-fill: #4F46E5;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-min-width: 36px;" +
                                    "-fx-min-height: 36px;" +
                                    "-fx-alignment: center;" +
                                    "-fx-background-radius: 50%;"
                    );
                    Label nom = new Label(
                            e.getNom() + " " + e.getPrenom());
                    nom.setStyle(
                            "-fx-font-weight: bold;" +
                                    "-fx-font-size: 13px;"
                    );
                    HBox box = new HBox(10, avatar, nom);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        TableColumn<Employe, String> colDept =
                new TableColumn<>("Département");
        colDept.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(
                    String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >=
                        table.getItems().size()) {
                    setText(null);
                } else {
                    Employe e =
                            table.getItems().get(getIndex());
                    setText(e.getDepartement() != null ?
                            e.getDepartement().getNom() : "");
                }
            }
        });

        TableColumn<Employe, String> colPoste =
                new TableColumn<>("Poste");
        colPoste.setCellValueFactory(
                new PropertyValueFactory<>("poste"));

        TableColumn<Employe, String> colEmail =
                new TableColumn<>("Email");
        colEmail.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        TableColumn<Employe, String> colTel =
                new TableColumn<>("Téléphone");
        colTel.setCellValueFactory(
                new PropertyValueFactory<>("telephone"));

        table.getColumns().addAll(
                colNax, colNom, colDept,
                colPoste, colEmail, colTel);

        // Colonne actions uniquement pour Admin
        if (utilisateur.isAdmin()) {
            TableColumn<Employe, Void> colActions =
                    new TableColumn<>("Actions");
            colActions.setMaxWidth(120);
            colActions.setMinWidth(120);
            colActions.setCellFactory(
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
                                Employe emp = getTableView()
                                        .getItems().get(getIndex());
                                afficherFormulaire(emp);
                            });
                            btnDel.setOnAction(e -> {
                                Employe emp = getTableView()
                                        .getItems().get(getIndex());
                                supprimer(emp);
                            });
                        }
                        @Override
                        protected void updateItem(
                                Void item, boolean empty) {
                            super.updateItem(item, empty);
                            setGraphic(empty ? null : box);
                        }
                    });
            table.getColumns().add(colActions);
        }

        donnees = FXCollections.observableArrayList();
        table.setItems(donnees);

        getChildren().addAll(topBar, table);
    }

    private void chargerDonnees() {
        donnees.clear();
        donnees.addAll(dao.findAll());
    }

    private void rechercher(String terme) {
        if (terme == null || terme.trim().isEmpty()) {
            chargerDonnees();
            return;
        }
        donnees.clear();
        donnees.addAll(dao.rechercherParNom(terme.trim()));
    }

    private void afficherFormulaire(Employe emp) {
        Dialog<Employe> dialog = new Dialog<>();
        utils.IconUtil.appliquerIcone(dialog);
        dialog.setTitle(emp == null ?
                "Nouvel employé" : "Modifier employé");
        dialog.setHeaderText(null);

        VBox contenu = new VBox(12);
        contenu.setPadding(new Insets(20));
        contenu.setMinWidth(480);

        Label lblTitre = new Label(emp == null ?
                "Nouvel employé" : "Modifier employé");
        lblTitre.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );

        TextField champNax    = creerChamp("NAX");
        TextField champNom    = creerChamp("Nom");
        TextField champPrenom = creerChamp("Prénom");
        TextField champEmail  = creerChamp("Email");
        TextField champTel    = creerChamp("Téléphone");
        TextField champPoste  = creerChamp("Poste");
        TextField champDateN  = creerChamp("jj/MM/aaaa");
        TextField champDateE  = creerChamp("jj/MM/aaaa");

        HBox ligneNax = new HBox(12,
                creerLigneChamp("NAX *", champNax),
                creerLigneChamp("Poste", champPoste));
        HBox ligneNom = new HBox(12,
                creerLigneChamp("Nom *", champNom),
                creerLigneChamp("Prénom *", champPrenom));
        HBox ligneContact = new HBox(12,
                creerLigneChamp("Email (@modep.ma)", champEmail),
                creerLigneChamp("Téléphone (10 chiffres max)", champTel));
        HBox ligneDates = new HBox(12,
                creerLigneChamp("Date Naissance", champDateN),
                creerLigneChamp("Date Embauche", champDateE));

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

        if (emp != null) {
            champNax.setText(
                    emp.getNax() != null ? emp.getNax() : "");
            champNom.setText(emp.getNom());
            champPrenom.setText(emp.getPrenom());
            champEmail.setText(
                    emp.getEmail() != null ? emp.getEmail() : "");
            champTel.setText(emp.getTelephone() != null ?
                    emp.getTelephone() : "");
            champPoste.setText(
                    emp.getPoste() != null ? emp.getPoste() : "");
            if (emp.getDateNaissance() != null)
                champDateN.setText(
                        SDF.format(emp.getDateNaissance()));
            if (emp.getDateEmbauche() != null)
                champDateE.setText(
                        SDF.format(emp.getDateEmbauche()));
            for (Departement d : depts) {
                if (emp.getDepartement() != null &&
                        d.getIdDepartement() ==
                                emp.getDepartement().getIdDepartement()) {
                    comboDept.setValue(d);
                    break;
                }
            }
        } else if (!depts.isEmpty()) {
            comboDept.setValue(depts.get(0));
        }

        VBox boxDept = new VBox(6);
        Label lblDept = new Label("Département *");
        lblDept.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #374151;"
        );
        boxDept.getChildren().addAll(lblDept, comboDept);

        contenu.getChildren().addAll(
                lblTitre, ligneNax, ligneNom,
                ligneContact, ligneDates, boxDept);

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
                String nax = champNax.getText().trim();
                if (nax.isEmpty() ||
                        champNom.getText().trim().isEmpty() ||
                        champPrenom.getText().trim().isEmpty()) {
                    afficherErreur(
                            "NAX, Nom et Prénom sont obligatoires !");
                    return null;
                }

                String email = champEmail.getText().trim();
                if (!email.isEmpty() &&
                        !EMAIL_PATTERN.matcher(email).matches()) {
                    afficherErreur(
                            "L'email doit être de la forme " +
                                    "prenom.nom@modep.ma !");
                    return null;
                }

                String tel = champTel.getText().trim();
                if (!tel.isEmpty() &&
                        !TEL_PATTERN.matcher(tel).matches()) {
                    afficherErreur(
                            "Le téléphone ne doit contenir que " +
                                    "des chiffres (10 maximum) !");
                    return null;
                }

                // Unicité du NAX (on exclut l'employe en cours
                // de modification, sinon il se bloquerait lui-meme)
                Employe existant = dao.findByNax(nax);
                if (existant != null &&
                        (emp == null ||
                                existant.getIdEmploye() !=
                                        emp.getIdEmploye())) {
                    afficherErreur(
                            "Ce NAX (" + nax + ") est déjà " +
                                    "utilisé par un autre employé !");
                    return null;
                }

                try {
                    return new Employe(
                            emp != null ? emp.getIdEmploye() : 0,
                            nax,
                            champNom.getText().trim(),
                            champPrenom.getText().trim(),
                            email,
                            tel,
                            champPoste.getText().trim(),
                            champDateN.getText().trim().isEmpty() ?
                                    null : SDF.parse(
                                    champDateN.getText().trim()),
                            champDateE.getText().trim().isEmpty() ?
                                    null : SDF.parse(
                                    champDateE.getText().trim()),
                            comboDept.getValue()
                    );
                } catch (ParseException ex) {
                    afficherErreur(
                            "Format date invalide ! (jj/MM/aaaa)");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(e -> {
            if (emp == null) {
                if (dao.ajouter(e)) {
                    afficherSucces("Employé ajouté !");
                    chargerDonnees();
                } else {
                    afficherErreur(
                            "Impossible d'ajouter l'employé " +
                                    "(vérifie que le NAX est bien " +
                                    "unique).");
                }
            } else {
                if (dao.modifier(e)) {
                    afficherSucces("Employé modifié !");
                    chargerDonnees();
                } else {
                    afficherErreur(
                            "Impossible de modifier l'employé " +
                                    "(vérifie que le NAX est bien " +
                                    "unique).");
                }
            }
        });
    }

    private void supprimer(Employe e) {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION);
        utils.IconUtil.appliquerIcone(confirm);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Supprimer l'employé \"" +
                        e.getNom() + " " + e.getPrenom() + "\" ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (dao.supprimer(e.getIdEmploye())) {
                    afficherSucces("Employé supprimé !");
                    chargerDonnees();
                }
            }
        });
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
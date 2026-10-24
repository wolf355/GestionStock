package vue;

import dao.DepartementDAO;
import dao.EmployeDAO;
import modele.Departement;
import modele.Employe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.List;

public class PanelDepartement extends VBox {

    private DepartementDAO dao = new DepartementDAO();
    private EmployeDAO employeDao = new EmployeDAO();
    private TableView<Departement> table;
    private ObservableList<Departement> donnees;
    private TextField champNom, champDescription;
    private ComboBox<Employe> comboResponsable;
    private Departement departementSelectionne = null;

    public PanelDepartement() {
        setStyle("-fx-background-color: #F5F6FA;");
        setPadding(new Insets(30));
        setSpacing(20);
        VBox.setVgrow(this, Priority.ALWAYS);
        initComposants();
        chargerDonnees();
    }

    private void initComposants() {

        // ===== TITRE + BOUTON AJOUTER =====
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(20);

        Label lblTitre = new Label("Gestion des Départements");
        lblTitre.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );
        HBox.setHgrow(lblTitre, Priority.NEVER);

        Button btnNouveau = new Button("+ Ajouter un département");
        btnNouveau.setStyle(
                "-fx-background-color: #1E2840;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        btnNouveau.setOnAction(e -> afficherFormulaire(null));

        Region espaceurTitre = new Region();
        HBox.setHgrow(espaceurTitre, Priority.ALWAYS);
        espaceurTitre.setMinWidth(40);
        topBar.getChildren().addAll(lblTitre, espaceurTitre, btnNouveau);

        // ===== TABLEAU =====
        table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Departement, Integer> colId =
                new TableColumn<>("ID");
        colId.setCellValueFactory(
                new PropertyValueFactory<>("idDepartement"));
        colId.setMaxWidth(60);
        colId.setMinWidth(60);

        TableColumn<Departement, String> colNom =
                new TableColumn<>("Nom");
        colNom.setCellValueFactory(
                new PropertyValueFactory<>("nom"));

        TableColumn<Departement, String> colResp =
                new TableColumn<>("Responsable");
        colResp.setCellValueFactory(cellData -> {
            String nax = cellData.getValue().getResponsable();
            String affichage = "";
            if (nax != null && !nax.isBlank()) {
                Employe emp = employeDao.findByNax(nax);
                affichage = emp != null ?
                        emp.getNom() + " " + emp.getPrenom() + " (" + nax + ")" :
                        nax;
            }
            return new javafx.beans.property.SimpleStringProperty(affichage);
        });

        TableColumn<Departement, String> colDesc =
                new TableColumn<>("Description");
        colDesc.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        // Colonne Actions
        TableColumn<Departement, Void> colActions =
                new TableColumn<>("Actions");
        colActions.setMaxWidth(120);
        colActions.setMinWidth(120);
        colActions.setCellFactory(col -> new TableCell<>() {
            final Button btnEdit = new Button("✏");
            final Button btnDel  = new Button("🗑");
            final HBox box = new HBox(8, btnEdit, btnDel);

            {
                box.setAlignment(Pos.CENTER);
                btnEdit.setStyle(
                        "-fx-background-color: #EEF2FF;" +
                                "-fx-text-fill: #4F46E5;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-size: 14px;"
                );
                btnDel.setStyle(
                        "-fx-background-color: #FEE2E2;" +
                                "-fx-text-fill: #EF4444;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-size: 14px;"
                );
                btnEdit.setOnAction(e -> {
                    Departement d = getTableView()
                            .getItems().get(getIndex());
                    afficherFormulaire(d);
                });
                btnDel.setOnAction(e -> {
                    Departement d = getTableView()
                            .getItems().get(getIndex());
                    supprimer(d);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        styleColonnes(colId, colNom, colResp, colDesc, colActions);
        table.getColumns().addAll(
                colId, colNom, colResp, colDesc, colActions);

        donnees = FXCollections.observableArrayList();
        table.setItems(donnees);

        getChildren().addAll(topBar, table);
    }

    private void styleColonnes(TableColumn<?, ?>... cols) {
        for (TableColumn<?, ?> col : cols) {
            col.setStyle(
                    "-fx-alignment: CENTER-LEFT;" +
                            "-fx-font-size: 13px;"
            );
        }
    }

    private void chargerDonnees() {
        donnees.clear();
        donnees.addAll(dao.findAll());
    }

    private void afficherFormulaire(Departement dept) {
        Dialog<Departement> dialog = new Dialog<>();
        utils.IconUtil.appliquerIcone(dialog);
        dialog.setTitle(dept == null ?
                "Nouveau département" : "Modifier département");
        dialog.setHeaderText(null);

        // Contenu
        VBox contenu = new VBox(15);
        contenu.setPadding(new Insets(20));
        contenu.setMinWidth(400);

        Label lblTitre = new Label(dept == null ?
                "Nouveau département" : "Modifier département");
        lblTitre.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );

        champNom = creerChamp("Nom du département");
        champDescription = creerChamp("Description");

        // ComboBox du responsable : uniquement des employés existants
        comboResponsable = new ComboBox<>();
        comboResponsable.setPromptText("Choisir un responsable");
        comboResponsable.setMaxWidth(Double.MAX_VALUE);
        comboResponsable.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
        List<Employe> employes = employeDao.findAll();
        comboResponsable.setItems(FXCollections.observableArrayList(employes));
        comboResponsable.setConverter(new StringConverter<>() {
            @Override
            public String toString(Employe e) {
                return e == null ? "" :
                        e.getNom() + " " + e.getPrenom() + " (" + e.getNax() + ")";
            }
            @Override
            public Employe fromString(String s) { return null; }
        });

        if (dept != null) {
            champNom.setText(dept.getNom());
            champDescription.setText(dept.getDescription());
            if (dept.getResponsable() != null && !dept.getResponsable().isBlank()) {
                Employe respActuel = employeDao.findByNax(dept.getResponsable());
                comboResponsable.setValue(respActuel);
            }
        }

        contenu.getChildren().addAll(
                lblTitre,
                creerLigneChamp("Nom *", champNom),
                creerLigneChamp("Responsable", comboResponsable),
                creerLigneChamp("Description", champDescription)
        );

        dialog.getDialogPane().setContent(contenu);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;"
        );

        ButtonType btnSauvegarder = new ButtonType(
                "Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnuler = new ButtonType(
                "Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(
                btnSauvegarder, btnAnnuler);

        // Style bouton sauvegarder
        Button btnSave = (Button) dialog.getDialogPane()
                .lookupButton(btnSauvegarder);
        btnSave.setStyle(
                "-fx-background-color: #1E2840;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;"
        );

        dialog.setResultConverter(bt -> {
            if (bt == btnSauvegarder) {
                if (champNom.getText().trim().isEmpty()) {
                    afficherErreur("Le nom est obligatoire !");
                    return null;
                }
                Employe respChoisi = comboResponsable.getValue();
                return new Departement(
                        dept != null ? dept.getIdDepartement() : 0,
                        champNom.getText().trim(),
                        respChoisi != null ? respChoisi.getNax() : null,
                        champDescription.getText().trim()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(d -> {
            if (dept == null) {
                if (dao.ajouter(d)) {
                    afficherSucces("Département ajouté !");
                    chargerDonnees();
                }
            } else {
                if (dao.modifier(d)) {
                    afficherSucces("Département modifié !");
                    chargerDonnees();
                }
            }
        });
    }

    private void supprimer(Departement d) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        utils.IconUtil.appliquerIcone(confirm);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Voulez-vous supprimer le département \"" +
                        d.getNom() + "\" ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (dao.supprimer(d.getIdDepartement())) {
                    afficherSucces("Département supprimé !");
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

    private VBox creerLigneChamp(String label, Control champ) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #374151;"
        );
        box.getChildren().addAll(lbl, champ);
        return box;
    }

    private void afficherSucces(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        utils.IconUtil.appliquerIcone(alert);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void afficherErreur(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        utils.IconUtil.appliquerIcone(alert);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
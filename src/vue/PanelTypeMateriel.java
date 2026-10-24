package vue;

import dao.DepartementDAO;
import dao.TypeMaterielDAO;
import modele.Departement;
import modele.TypeMateriel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

public class PanelTypeMateriel extends VBox {

    private TypeMaterielDAO dao     = new TypeMaterielDAO();
    private DepartementDAO  daoD   = new DepartementDAO();
    private TableView<TypeMateriel> table;
    private ObservableList<TypeMateriel> donnees;

    public PanelTypeMateriel() {
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
        topBar.setSpacing(20);

        Label lblTitre = new Label("Gestion des Types de Matériel");
        lblTitre.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );
        HBox.setHgrow(lblTitre, Priority.NEVER);

        Button btnNouveau = new Button("+ Ajouter un type");
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

        // TABLEAU
        table = new TableView<>();
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TypeMateriel, Integer> colId =
                new TableColumn<>("ID");
        colId.setCellValueFactory(
                new PropertyValueFactory<>("idType"));
        colId.setMaxWidth(60);

        TableColumn<TypeMateriel, String> colLibelle =
                new TableColumn<>("Libellé");
        colLibelle.setCellValueFactory(
                new PropertyValueFactory<>("libelle"));

        TableColumn<TypeMateriel, String> colDept =
                new TableColumn<>("Département");
        colDept.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= table.getItems().size()) {
                    setText(null);
                } else {
                    TypeMateriel t = table.getItems().get(getIndex());
                    setText(t.getDepartement() != null ?
                            t.getDepartement().getNom() : "");
                }
            }
        });

        TableColumn<TypeMateriel, Void> colActions =
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
                                "-fx-cursor: hand;"
                );
                btnDel.setStyle(
                        "-fx-background-color: #FEE2E2;" +
                                "-fx-text-fill: #EF4444;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;"
                );
                btnEdit.setOnAction(e -> {
                    TypeMateriel t = getTableView()
                            .getItems().get(getIndex());
                    afficherFormulaire(t);
                });
                btnDel.setOnAction(e -> {
                    TypeMateriel t = getTableView()
                            .getItems().get(getIndex());
                    supprimer(t);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(
                colId, colLibelle, colDept, colActions);
        donnees = FXCollections.observableArrayList();
        table.setItems(donnees);

        getChildren().addAll(topBar, table);
    }

    private void chargerDonnees() {
        donnees.clear();
        donnees.addAll(dao.findAll());
    }

    private void afficherFormulaire(TypeMateriel type) {
        Dialog<TypeMateriel> dialog = new Dialog<>();
        utils.IconUtil.appliquerIcone(dialog);
        dialog.setTitle(type == null ?
                "Nouveau type" : "Modifier type");
        dialog.setHeaderText(null);

        VBox contenu = new VBox(15);
        contenu.setPadding(new Insets(20));
        contenu.setMinWidth(400);

        Label lblTitre = new Label(type == null ?
                "Nouveau type de matériel" : "Modifier type de matériel");
        lblTitre.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E2840;"
        );

        TextField champLibelle = creerChamp("Libellé du type");
        if (type != null) champLibelle.setText(type.getLibelle());

        ComboBox<Departement> comboDept = new ComboBox<>();
        comboDept.setStyle(
                "-fx-background-color: #F9FAFB;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13px;"
        );
        comboDept.setPrefHeight(42);
        comboDept.setMaxWidth(Double.MAX_VALUE);
        List<Departement> depts = daoD.findAll();
        comboDept.getItems().addAll(depts);
        if (type != null && type.getDepartement() != null) {
            for (Departement d : depts) {
                if (d.getIdDepartement() ==
                        type.getDepartement().getIdDepartement()) {
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
                lblTitre,
                creerLigneChamp("Libellé *", champLibelle),
                boxDept
        );

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
                if (champLibelle.getText().trim().isEmpty()) {
                    afficherErreur("Le libellé est obligatoire !");
                    return null;
                }
                if (comboDept.getValue() == null) {
                    afficherErreur("Choisissez un département !");
                    return null;
                }
                return new TypeMateriel(
                        type != null ? type.getIdType() : 0,
                        champLibelle.getText().trim(),
                        comboDept.getValue()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(t -> {
            if (type == null) {
                if (dao.ajouter(t)) {
                    afficherSucces("Type ajouté !");
                    chargerDonnees();
                }
            } else {
                if (dao.modifier(t)) {
                    afficherSucces("Type modifié !");
                    chargerDonnees();
                }
            }
        });
    }

    private void supprimer(TypeMateriel t) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        utils.IconUtil.appliquerIcone(confirm);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Supprimer le type \"" + t.getLibelle() + "\" ?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (dao.supprimer(t.getIdType())) {
                    afficherSucces("Type supprimé !");
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

    private VBox creerLigneChamp(String label, TextField champ) {
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
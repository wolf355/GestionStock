package utils;

import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Applique le logo MODEP comme icône de fenêtre à n'importe quelle
 * Dialog ou Alert JavaFX (par défaut, ces fenêtres affichent l'icône
 * générique de Java au lieu d'hériter de celle de la fenêtre
 * principale).
 */
public class IconUtil {

    private static Image logo;

    private static Image getLogo() {
        if (logo == null) {
            logo = new Image(
                    IconUtil.class.getResourceAsStream(
                            "/images/logo_modep.png"));
        }
        return logo;
    }

    /**
     * À appeler juste après la création d'un Dialog/Alert
     * (ex: new Alert(...), new Dialog<>()), avant showAndWait().
     */
    public static void appliquerIcone(Dialog<?> dialog) {
        try {
            Stage stage = (Stage) dialog.getDialogPane()
                    .getScene().getWindow();
            stage.getIcons().add(getLogo());
        } catch (Exception ignore) {
            // Si la fenetre n'est pas encore prete, on ignore
            // simplement - l'icone par defaut restera affichee
            // pour cette fenetre precise, sans bloquer l'appli.
        }
    }
}
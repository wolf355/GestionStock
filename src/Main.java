import dao.DepartementDAO;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import vue.FenetreLogin;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Gestion du Stock - MODEP");
        stage.getIcons().add(new Image(
                getClass().getResourceAsStream(
                        "/images/logo_modep.png")));

        // Lancer la fenêtre de login
        FenetreLogin login = new FenetreLogin(stage);
        login.afficher();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
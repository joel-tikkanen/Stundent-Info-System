package view;

import javafx.application.Application;
import javafx.stage.Stage;
import util.NavigationManager;

public class UserInterface extends Application {

    public void start(Stage stage) {
        NavigationManager.getInstance().setInitialView("/login.fxml", stage);
    }
    public static void main(String[] args) {
        launch(args);
    }
}

package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.KirjautunutKayttaja;
import model.Opettaja;
import service.OpettajaService;
import util.NavigationManager;
import util.ResourceBundleManager;

import java.io.IOException;
import java.util.ResourceBundle;

public class LoginController {

    @FXML
    private TextField loginPassword;

    @FXML
    private TextField loginUsername;

    private final OpettajaService opettajaService = new OpettajaService();

    @FXML
    void attemptLogin(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        ResourceBundle bundle = ResourceBundleManager.getResourceBundle();

        // Check if username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(bundle.getString("login_failed.title"),
                    bundle.getString("empty_fields.header"),
                    bundle.getString("empty_fields.content"));
            return;
        }

        // Validate the user using OpettajaService
        Opettaja opettaja = opettajaService.login(username, password);

        if (opettaja != null) {
            KirjautunutKayttaja.getInstance().setOpettaja(opettaja);
            NavigationManager.getInstance().navigateTo("/mainMenu.fxml", event);
        } else {
            showAlert(bundle.getString("login_failed.title"),
                    bundle.getString("invalid_credentials.header"),
                    bundle.getString("invalid_credentials.content"));
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Load and apply the custom stylesheet
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Scene scene = alert.getDialogPane().getScene();
        scene.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());

        stage.getScene().getRoot().setStyle("-fx-border-color: #060606; -fx-border-width: 3px; -fx-border-radius: 0 0 5 5px; -fx-background-radius: 15px;");
        stage.setHeight(250);

        alert.setGraphic(null);

        alert.showAndWait();
    }
}
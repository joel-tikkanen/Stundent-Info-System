package controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.KirjautunutKayttaja;
import util.NavigationManager;
import util.ResourceBundleManager;

import java.util.Locale;

public class HeaderComponentController {

    @FXML
    private Button navigateBack;

    @FXML
    private Button navigateForward;

    @FXML
    void navigateBack(ActionEvent event) {
        NavigationManager.getInstance().goBack(event);
    }

    @FXML
    void navigateForward(ActionEvent event) {
        NavigationManager.getInstance().goForward(event);
    }

    @FXML
    void openProfiiliPage(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/profiili.fxml", event);
    }

    @FXML
    void logOut(ActionEvent event) {
        KirjautunutKayttaja.getInstance().clearOpettaja();
        NavigationManager.getInstance().navigateTo("/login.fxml", event);
    }

    // Language Selection Methods
    @FXML
    public void setFI(ActionEvent actionEvent) {
        changeLanguage(new Locale("fi", "FI"), actionEvent);
    }

    @FXML
    public void setEN(ActionEvent actionEvent) {
        changeLanguage(new Locale("en", "US"), actionEvent);
    }

    @FXML
    public void setJP(ActionEvent actionEvent) {
        changeLanguage(new Locale("ja", "JP"), actionEvent);
    }

    @FXML
    public void setIR(ActionEvent actionEvent) {
        changeLanguage(new Locale("fa", "IR"), actionEvent);
    }

    private void changeLanguage(Locale locale, ActionEvent event) {
        NavigationManager navManager = NavigationManager.getInstance();
        navManager.setLocale(locale);
        ResourceBundleManager.setLocale(locale);

        navManager.reloadCurrentView(event);
    }
}

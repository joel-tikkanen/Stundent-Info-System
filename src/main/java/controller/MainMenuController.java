//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.KirjautunutKayttaja;
import model.Kurssi;
import model.Oppitunti;
import service.KurssiService;
import util.NavigationManager;
import util.ResourceBundleManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MainMenuController {
    @FXML
    private Button KalenteriButton;
    @FXML
    private Button KurssitButton;
    @FXML
    private ListView<String> ListViewMainMenu;
    @FXML
    private Button LogOutButton;
    @FXML
    private Button OppilaatButton;
    @FXML
    private Button ProfiiliButton;
    @FXML
    private Button TapahtumatButton;

    @FXML
    private Button LasnaolotButton;


    private KurssiService kurssiService;

    public MainMenuController() {
        this.kurssiService = new KurssiService();
    }

    @FXML
    public void initialize() {
        KalenteriController kalenteriController = new KalenteriController();
        List<Oppitunti> todayCourses = kalenteriController.getTodayCourses();

        ObservableList<String> items = FXCollections.observableArrayList();
        for (Oppitunti oppitunti : todayCourses) {
            items.add(oppitunti.getKurssi().getNimi());
        }

        ListViewMainMenu.setItems(items);
    }

    @FXML
    void CloseProgram(ActionEvent event) {
        // Clear the logged-in user
        KirjautunutKayttaja.getInstance().clearOpettaja();

        // Navigate to the login page using NavigationManager
        NavigationManager.getInstance().navigateTo("/login.fxml", event);
    }

    @FXML
    void openKalenteriPage(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/kalenteri.fxml", event);
    }

    @FXML
    void openKurssitPage(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/kurssit.fxml", event);
    }

    @FXML
    void openOppilaatPage(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/studentInfo.fxml", event);
    }

    @FXML
    void openProfiiliPage(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/profiili.fxml", event);
    }

    @FXML
    void openTapahtumatPage(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/tapahtumat.fxml", event);
    }

    @FXML
    void openLasnaoloPage(ActionEvent event) {
        NavigationManager.getInstance().navigateTo("/poissaolot.fxml", event);
    }



    /* NÄIN LISÄTÄÄN LISTVIEW KOHTAAN SISÄLTÖÄ
    @FXML
    private ListView<String> listViewMainMenu;

    @FXML
    public void initialize() {
        // Create an observable list of items
        ObservableList<String> items = FXCollections.observableArrayList(
            "Item 1", "Item 2", "Item 3"
        );

        // Add items to the ListView
        listViewMainMenu.setItems(items);
    }
     */
}

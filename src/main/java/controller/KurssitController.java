package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.KirjautunutKayttaja;
import model.Kurssi;
import service.KurssiService;
import util.NavigationManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class KurssitController {

    @FXML private Button deleteCourseButton;
    @FXML private Button ProfiiliButton;
    @FXML private Button LogOutButton;
    @FXML private TableView<Kurssi> CourseTableView;
    @FXML private TableColumn<Kurssi, String> courseNameColumn;
    @FXML private TableColumn<Kurssi, String> courseInstructorColumn;
    @FXML private TextField SearchTextField;
    @FXML private Button TakaisinButton;
    @FXML private Text tunnusField;
    @FXML private Text nimiField;
    @FXML private Text opettajaField;
    @FXML private Text aloitusPvmField;
    @FXML private Text lopetusPvmField;
    @FXML private TextArea kuvausTextArea;
    @FXML private Button editSaveButton;
    @FXML private Button createNewCourseButton;

    private KurssiService kurssiService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @FXML
    private void initialize() {
        kurssiService = new KurssiService();
        loadAllCourses();

        CourseTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayCourseInfo(newValue);
            }
        });

        kuvausTextArea.setEditable(false);

        // Set up the columns in the table
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("nimi"));
        courseInstructorColumn.setCellValueFactory(new PropertyValueFactory<>("opettaja"));

        // Add a listener to the SearchTextField to filter the list based on the input
        SearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchCourse();
        });
    }

    public void loadAllCourses() {
        System.out.println("loading courses");
        List<Kurssi> kurssit = kurssiService.getAllKurssit();
        ObservableList<Kurssi> observableKurssit = FXCollections.observableArrayList(kurssit);
        CourseTableView.setItems(observableKurssit);
    }

    @FXML
    private void handleEditSave(ActionEvent event) {
        Kurssi selectedKurssi = CourseTableView.getSelectionModel().getSelectedItem();
        if (selectedKurssi != null) {
            openUusiKurssiWindow(event, selectedKurssi);
        } else {
            showAlert("Valitse kurssi ensin");
        }
    }



    @FXML
    public void searchCourse() {
        String searchTerm = SearchTextField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadAllCourses();
        } else {
            List<Kurssi> allKurssit = kurssiService.getAllKurssit();
            List<Kurssi> filteredKurssit = allKurssit.stream()
                    .filter(kurssi -> kurssi.getNimi().toLowerCase().contains(searchTerm) ||
                            kurssi.getOpettaja().getNimi().toLowerCase().contains(searchTerm))
                    .toList();
            CourseTableView.setItems(FXCollections.observableArrayList(filteredKurssit));
        }
    }

    private void openUusiKurssiWindow(ActionEvent event, Kurssi kurssi) {
        NavigationManager.getInstance().navigateTo("/lisaaKurssi.fxml", event, kurssi);
    }

    public void refreshCourses(Kurssi updatedKurssi) {
        loadAllCourses();
        if (updatedKurssi != null) {
            for (Kurssi kurssi : CourseTableView.getItems()) {
                if (kurssi.getKurssi_id().equals(updatedKurssi.getKurssi_id())) {
                    CourseTableView.getSelectionModel().select(kurssi);
                    displayCourseInfo(kurssi);
                    break;
                }
            }
        }
        CourseTableView.refresh();
    }

    @FXML
    private void handleCreateNewCourse(ActionEvent event) {
        openUusiKurssiWindow(event, null); // Passing null indicates creating a new course
    }

    @FXML
    private void navigateBackwards(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayCourseInfo(Kurssi kurssi) {
        tunnusField.setText(String.valueOf(kurssi.getKurssi_id()));
        nimiField.setText(kurssi.getNimi());
        opettajaField.setText(kurssi.getOpettaja().getNimi());
        aloitusPvmField.setText(dateFormat.format(kurssi.getAlkupvm()));
        lopetusPvmField.setText(dateFormat.format(kurssi.getLoppupvm()));
        kuvausTextArea.setText(kurssi.getKuvaus());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Tapahtui virhe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleDeleteCourse(ActionEvent actionEvent) {
        Kurssi selectedKurssi = CourseTableView.getSelectionModel().getSelectedItem();
        kurssiService.deleteKurssi(selectedKurssi.getKurssi_id());
        refreshCourses(selectedKurssi);
    }
}
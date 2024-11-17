package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import model.Opiskelija;
import model.Kurssi;
import model.Opintosuoritus;
import service.OpiskelijaService;
import service.KurssiService;
import service.OpintosuoritusService;
import view.OpiskelijaKurssiItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentInfoController {
    @FXML
    private Button naytaKurssitButton;
    @FXML
    private Button takaisinButton;
    @FXML
    private ListView<OpiskelijaKurssiItem> opiskelijanKurssitList;
    @FXML
    private Text studentName;
    @FXML
    private Text studentPhone;
    @FXML
    private Text studentEmail;
    @FXML
    private Text studentGuardian;

    @FXML
    private TableView<Opiskelija> StudentTableView;
    @FXML
    private TableColumn<Opiskelija, Long> idColumn;
    @FXML
    private TableColumn<Opiskelija, String> firstNameColumn;
    @FXML
    private TableColumn<Opiskelija, String> lastNameColumn;
    @FXML
    private TextField SearchTextField;

    private final OpiskelijaService opiskelijaService;
    private final KurssiService kurssiService;
    private final OpintosuoritusService opintosuoritusService;
    private FilteredList<Opiskelija> filteredData;
    private boolean isShowingCourses = false;

    public StudentInfoController() {
        this.opiskelijaService = new OpiskelijaService();
        this.kurssiService = new KurssiService();
        this.opintosuoritusService = new OpintosuoritusService();
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("opiskelija_id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("etunimi"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("sukunimi"));
        takaisinButton.setVisible(false);

        loadOpiskelijat();

        filteredData = new FilteredList<>(StudentTableView.getItems(), p -> true);
        StudentTableView.setItems(filteredData);

        SearchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterStudentList(newValue));

        StudentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showStudentDetails(newValue));
    }

    private void loadOpiskelijat() {
        List<Opiskelija> opiskelijat = opiskelijaService.getAllOpiskelijat();
        ObservableList<Opiskelija> opiskelijaObservableList = FXCollections.observableArrayList(opiskelijat);
        StudentTableView.setItems(opiskelijaObservableList);
    }

    @FXML
    void CloseProgram(ActionEvent event) {
        KirjautunutKayttaja.getInstance().clearOpettaja();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void filterStudentList(String searchText) {
        filteredData.setPredicate(opiskelija -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            if (opiskelija.getEtunimi().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return opiskelija.getSukunimi().toLowerCase().contains(lowerCaseFilter);
        });
    }

    private void showStudentDetails(Opiskelija opiskelija) {
        if (opiskelija != null) {
            studentName.setText(opiskelija.getEtunimi() + " " + opiskelija.getSukunimi());
            studentEmail.setText(opiskelija.getSahkoposti());
            studentPhone.setText(opiskelija.getPuhelinnumero());
            if (opiskelija.getHuoltaja() != null) {
                studentGuardian.setText(opiskelija.getHuoltaja().getEtunimi() + " " + opiskelija.getHuoltaja().getSukunimi());
            } else {
                studentGuardian.setText("...");
            }
        } else {
            studentName.setText("");
            studentEmail.setText("");
            studentPhone.setText("");
            studentGuardian.setText("");
        }
    }

    @FXML
    public void showCourses(ActionEvent actionEvent) {
        Opiskelija selectedOpiskelija = StudentTableView.getSelectionModel().getSelectedItem();
        if (selectedOpiskelija == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ei valittua opiskelijaa");
            alert.setHeaderText(null);
            alert.setContentText("Valitse ensin opiskelija listasta.");
            alert.showAndWait();
            return;
        }

        if (!isShowingCourses) {
            // Show courses
            List<Kurssi> opiskelijanKurssit = kurssiService.getKurssitByOpiskelija(selectedOpiskelija);
            List<OpiskelijaKurssiItem> kurssiItems = new ArrayList<>();

            for (Kurssi kurssi : opiskelijanKurssit) {
                Opintosuoritus opintosuoritus = opintosuoritusService.getOpintosuoritusByOpiskelijaAndKurssi(selectedOpiskelija, kurssi);
                kurssiItems.add(new OpiskelijaKurssiItem(kurssi, opintosuoritus, opintosuoritusService, selectedOpiskelija));
            }

            opiskelijanKurssitList.setItems(FXCollections.observableArrayList(kurssiItems));
            StudentTableView.setVisible(false);
            opiskelijanKurssitList.setVisible(true);
            naytaKurssitButton.setVisible(false);
            takaisinButton.setVisible(true);
        } else {
            // Show student list
            StudentTableView.setVisible(true);
            opiskelijanKurssitList.setVisible(false);
            naytaKurssitButton.setText("Näytä oppilaan kurssit");
            naytaKurssitButton.setVisible(true);
            takaisinButton.setVisible(false);
        }

        isShowingCourses = !isShowingCourses;
    }
}
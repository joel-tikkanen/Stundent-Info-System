package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.KirjautunutKayttaja;
import model.Kurssi;
import model.Opiskelija;
import service.KurssiService;
import service.OpettajaService;
import service.KurssiIlmoittautuminenService;
import service.OpiskelijaService;
import util.DataReceiver;
import util.NavigationManager;
import view.OpiskelijaIlmoittautuminenItem;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class UusiKurssiController implements DataReceiver {
    @FXML private TextField tunnusField;
    @FXML private TextField nimiField;
    @FXML private TextField opettajaField;
    @FXML private DatePicker aloitusPvmField;
    @FXML private DatePicker lopetusPvmField;
    @FXML private TextArea kuvausTextArea;
    @FXML private Button editSaveButton;
    @FXML private ListView<Opiskelija> enrollmentListView;
    @FXML private TextField searchStudentField;

    private KurssiService kurssiService;
    private OpettajaService opettajaService;
    private KurssiIlmoittautuminenService ilmoittautuminenService;
    private OpiskelijaService opiskelijaService;
    private Kurssi currentKurssi;
    private ObservableList<Opiskelija> enrollmentItems = FXCollections.observableArrayList();

    private KurssitController kurssitController;

    @FXML
    private void initialize() {
        kurssiService = new KurssiService();
        opettajaService = new OpettajaService();
        ilmoittautuminenService = new KurssiIlmoittautuminenService();
        opiskelijaService = new OpiskelijaService();

        if (enrollmentListView != null) {
            enrollmentListView.setItems(enrollmentItems);
            enrollmentListView.setCellFactory(lv -> new OpiskelijaIlmoittautuminenItem(
                    ilmoittautuminenService,
                    currentKurssi,
                    ilmoittautuminenService.getIlmoittautumisetByKurssiId(currentKurssi.getKurssi_id())
            ));
        }
    }

    public void setKurssitController(KurssitController kurssitController) {
        this.kurssitController = kurssitController;
    }

    @Override
    public void initData(Object data) {
        if (data == null) {
            // Handle the case when data is null (creating new course)
            this.currentKurssi = new Kurssi();
            currentKurssi.setOpettaja(KirjautunutKayttaja.getInstance().getOpettaja());
            System.out.println("OPE:");
            System.out.println(KirjautunutKayttaja.getInstance().getOpettaja());
            opettajaField.setText(currentKurssi.getOpettaja().getNimi());
        } else if (data instanceof Kurssi) {
            Kurssi kurssi = (Kurssi) data;
            this.currentKurssi = kurssi;
            populateFields();
            loadEnrollments();
        } else {
            // Handle unexpected data types if necessary
            System.err.println("Unexpected data type in initData: " + data.getClass());
        }
    }


    private void populateFields() {
        nimiField.setText(currentKurssi.getNimi());
        System.out.println("OPETTAJA::");


        System.out.println(KirjautunutKayttaja.getInstance().getOpettaja().getNimi());
        opettajaField.setText(KirjautunutKayttaja.getInstance().getOpettaja().getNimi());
        aloitusPvmField.setValue(convertToLocalDate(currentKurssi.getAlkupvm()));
        lopetusPvmField.setValue(convertToLocalDate(currentKurssi.getLoppupvm()));
        kuvausTextArea.setText(currentKurssi.getKuvaus());
    }

    private void clearFields() {
        nimiField.clear();

        aloitusPvmField.setValue(null);
        lopetusPvmField.setValue(null);
        kuvausTextArea.clear();
        enrollmentItems.clear();
    }

    private void loadEnrollments() {
        enrollmentItems.clear();
        enrollmentItems.addAll(opiskelijaService.getAllOpiskelijat());
        enrollmentListView.refresh();
    }

    @FXML
    private void handleSearchStudent() {
        String searchTerm = searchStudentField.getText().toLowerCase();
        enrollmentItems.clear();
        for (Opiskelija student : opiskelijaService.getAllOpiskelijat()) {
            if (student.getEtunimi().toLowerCase().contains(searchTerm) ||
                    student.getSukunimi().toLowerCase().contains(searchTerm)) {
                enrollmentItems.add(student);
            }
        }
        enrollmentListView.refresh();
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) {
            return;
        }

        if (currentKurssi == null) {
            currentKurssi = new Kurssi();
        }

        currentKurssi.setOpettaja(KirjautunutKayttaja.getInstance().getOpettaja());
        currentKurssi.setNimi(nimiField.getText());
        currentKurssi.setAlkupvm(convertToDate(aloitusPvmField.getValue()));
        currentKurssi.setLoppupvm(convertToDate(lopetusPvmField.getValue()));
        currentKurssi.setKuvaus(kuvausTextArea.getText());

        try {
            if (currentKurssi.getKurssi_id() == null) {
                kurssiService.createKurssi(currentKurssi);
            } else {
                kurssiService.updateKurssi(currentKurssi.getKurssi_id(), currentKurssi);
            }

            if (kurssitController != null) {
                kurssitController.refreshCourses(currentKurssi);
            }

            navigateBackwards(event);
        } catch (Exception e) {
            showAlert("Virhe tallennettaessa kurssia: " + e.getMessage());
        }
    }


    private boolean validateInputs() {
        if (nimiField.getText().isEmpty()) {
            showAlert("Nimi on pakollinen kenttä");
            return false;
        }
        System.out.println(opettajaField.getText());
        if (opettajaField.getText().isEmpty()) {
            showAlert("Opettaja on pakollinen kenttä");
            return false;
        }
        if (aloitusPvmField.getValue() == null) {
            showAlert("Aloituspäivämäärä on pakollinen");
            return false;
        }
        if (lopetusPvmField.getValue() == null) {
            showAlert("Lopetuspäivämäärä on pakollinen");
            return false;
        }
        if (aloitusPvmField.getValue().isAfter(lopetusPvmField.getValue())) {
            showAlert("Aloituspäivämäärä ei voi olla lopetuspäivämäärän jälkeen");
            return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Virhe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private LocalDate convertToLocalDate(Date dateToConvert) {
        if (dateToConvert == null) return null;
        return dateToConvert.toLocalDate();
    }

    private Date convertToDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.valueOf(localDate);
    }


    public void handleEditSave(ActionEvent actionEvent) {
        handleSave(actionEvent);
    }

    public void navigateBackwards(ActionEvent actionEvent) {
        NavigationManager.getInstance().navigateTo("/kurssit.fxml", actionEvent);
    }

}

package view;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import model.Kurssi;
import model.KurssiIlmoittautuminen;
import model.Opiskelija;
import service.KurssiIlmoittautuminenService;
import util.ResourceBundleManager;

import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class OpiskelijaIlmoittautuminenItem extends ListCell<Opiskelija> {

    private final HBox hBox = new HBox(10);
    private final Text text = new Text();

    private final Button lisaaButton = new Button(ResourceBundleManager.getLocalizedText("add"));
    private final Button poistaButton = new Button(ResourceBundleManager.getLocalizedText("delete"));

    private Opiskelija opiskelija;
    private final KurssiIlmoittautuminenService kis;
    private final Kurssi kurssi;
    private final List<KurssiIlmoittautuminen> ilmoittautumiset;

    public OpiskelijaIlmoittautuminenItem(KurssiIlmoittautuminenService kis, Kurssi kurssi, List<KurssiIlmoittautuminen> ilmoittautumiset) {
        super();
        this.kis = kis;
        this.kurssi = kurssi;
        this.ilmoittautumiset = ilmoittautumiset;


        lisaaButton.setOnAction(event -> handleLisaa());
        poistaButton.setOnAction(event -> handlePoista());


        hBox.getChildren().addAll(text, lisaaButton, poistaButton);
    }

    @Override
    protected void updateItem(Opiskelija item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            this.opiskelija = item;
            text.setText(opiskelija.getEtunimi() + " " + opiskelija.getSukunimi());
            updateButtons();
            setGraphic(hBox);
        }
    }

    private void updateButtons() {
        boolean isEnrolled = ilmoittautumiset.stream()
                .anyMatch(kurssiIlmoittautuminen ->
                        kurssiIlmoittautuminen.getOpiskelija().getOpiskelija_id().equals(opiskelija.getOpiskelija_id())
                );
        lisaaButton.setVisible(!isEnrolled);
        poistaButton.setVisible(isEnrolled);
    }

    private void handleLisaa() {
        KurssiIlmoittautuminen newEnrollment = new KurssiIlmoittautuminen(opiskelija, kurssi, new Date(System.currentTimeMillis()));
        kis.createIlmoittautuminen(newEnrollment);
        ilmoittautumiset.add(newEnrollment);
        updateButtons();
    }

    private void handlePoista() {
        KurssiIlmoittautuminen enrollment = ilmoittautumiset.stream()
                .filter(kurssiIlmoittautuminen ->
                        kurssiIlmoittautuminen.getOpiskelija().getOpiskelija_id().equals(opiskelija.getOpiskelija_id())
                )
                .findFirst()
                .orElse(null);
        if (enrollment != null) {
            kis.deleteIlmoittautuminen(enrollment.getIlmoittautuminen_id());
            ilmoittautumiset.remove(enrollment);
            updateButtons();
        }
    }
}

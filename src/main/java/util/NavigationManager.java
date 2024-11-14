package util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class NavigationManager {
    private static final NavigationManager instance = new NavigationManager();

    private final Stack<String> backStack = new Stack<>();
    private final Stack<String> forwardStack = new Stack<>();
    private final StringProperty currentView = new SimpleStringProperty();
    private final StringProperty currentTitle = new SimpleStringProperty();

    private final Map<String, String> viewTitleMap;

    private NavigationManager() {
        // Initialize the map between FXML paths and title keys
        viewTitleMap = new HashMap<>();
        viewTitleMap.put("/profiili.fxml", "header.profile");
        viewTitleMap.put("/mainMenu.fxml", "header.home");
        viewTitleMap.put("/kurssit.fxml", "header.courses");
        viewTitleMap.put("/login.fxml", "header.login");
        viewTitleMap.put("/kalenteri.fxml", "header.calendar");
        viewTitleMap.put("/studentInfo.fxml", "header.students");
        viewTitleMap.put("/poissaolot.fxml", "header.attendance");
        // Add other mappings as needed
    }

    public static NavigationManager getInstance() {
        return instance;
    }

    public void navigateTo(String fxmlPath, ActionEvent event) {
        if (currentView.get() != null) {
            backStack.push(currentView.get());
        }
        currentView.set(fxmlPath);
        forwardStack.clear(); // Clear forward history on new navigation

        // Set the current title
        updateCurrentTitle(fxmlPath);

        loadView(fxmlPath, event);
    }

    public void goBack(ActionEvent event) {
        if (!backStack.isEmpty()) {
            forwardStack.push(currentView.get());
            String previousView = backStack.pop();
            currentView.set(previousView);

            // Set the current title
            updateCurrentTitle(previousView);

            loadView(previousView, event);
        }
    }

    public void goForward(ActionEvent event) {
        if (!forwardStack.isEmpty()) {
            backStack.push(currentView.get());
            String nextView = forwardStack.pop();
            currentView.set(nextView);

            // Set the current title
            updateCurrentTitle(nextView);

            loadView(nextView, event);
        }
    }

    public boolean isBackButtonVisible() {
        return !backStack.isEmpty();
    }

    public boolean isForwardButtonVisible() {
        return !forwardStack.isEmpty();
    }

    public StringProperty currentViewProperty() {
        return currentView;
    }

    public String getCurrentView() {
        return currentView.get();
    }

    public StringProperty currentTitleProperty() {
        return currentTitle;
    }

    public String getCurrentTitle() {
        return currentTitle.get();
    }

    private void updateCurrentTitle(String fxmlPath) {
        String titleKey = viewTitleMap.get(fxmlPath);
        if (titleKey != null) {
            ResourceBundle bundle = ResourceBundleManager.getResourceBundle();
            String title = bundle.getString(titleKey);
            currentTitle.set(title);
        } else {
            currentTitle.set("");
        }
    }

    // Update the locale in ResourceBundleManager
    public void setLocale(Locale locale) {
        ResourceBundleManager.setLocale(locale);
        // Update the current title with the new locale
        updateCurrentTitle(currentView.get());
    }

    private void loadView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            ResourceBundle bundle = ResourceBundleManager.getResourceBundle();
            loader.setResources(bundle);

            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            // Apply the stylesheet if needed
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/app.css")).toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to reload current view with updated locale
    public void reloadCurrentView(ActionEvent event) {
        // Update the current title with the new locale
        updateCurrentTitle(currentView.get());
        loadView(currentView.get(), event);
    }
}

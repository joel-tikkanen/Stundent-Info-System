package util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

public class NavigationManager {
    private static NavigationManager instance = new NavigationManager();

    private Stack<String> backStack = new Stack<>();
    private Stack<String> forwardStack = new Stack<>();
    private StringProperty currentView = new SimpleStringProperty();

    private NavigationManager() {
        // Private constructor to enforce singleton pattern
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
        loadView(fxmlPath, event);
    }

    public void goBack(ActionEvent event) {
        if (!backStack.isEmpty()) {
            forwardStack.push(currentView.get());
            String previousView = backStack.pop();
            currentView.set(previousView);
            loadView(previousView, event);
        }
    }

    public void goForward(ActionEvent event) {
        if (!forwardStack.isEmpty()) {
            backStack.push(currentView.get());
            String nextView = forwardStack.pop();
            currentView.set(nextView);
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

    // Update the locale in ResourceBundleManager
    public void setLocale(Locale locale) {
        ResourceBundleManager.setLocale(locale);
    }

    private void loadView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            ResourceBundle bundle = ResourceBundleManager.getResourceBundle();
            loader.setResources(bundle);

            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to reload current view with updated locale
    public void reloadCurrentView(ActionEvent event) {
        loadView(currentView.get(), event);
    }
}

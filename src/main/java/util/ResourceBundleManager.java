package util;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleManager {

    private static Locale currentLocale = Locale.getDefault();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("kaannokset", currentLocale);

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        resourceBundle = ResourceBundle.getBundle("kaannokset", currentLocale);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static String getLocalizedText(String key) {
        return resourceBundle.getString(key);
    }
}

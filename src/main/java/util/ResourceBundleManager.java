package util;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleManager {

    private static Locale currentLocale = Locale.getDefault();

    public static void setLocale(Locale locale) {
        currentLocale = locale;
    }

    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("kaannokset", currentLocale);
    }

    public static ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle("kaannokset", locale);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
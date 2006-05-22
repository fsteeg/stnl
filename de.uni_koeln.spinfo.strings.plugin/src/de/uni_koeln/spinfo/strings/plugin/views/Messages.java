package de.uni_koeln.spinfo.strings.plugin.views;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Class to retrieve externalized strings, found in the file
 * "messages.properties".
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class Messages {
    private static final String BUNDLE_NAME = "de.uni_koeln.spinfo.strings.plugin.views.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    private Messages() {
    }

    /**
     * @param key
     *            The key
     * @return The message for the key
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}

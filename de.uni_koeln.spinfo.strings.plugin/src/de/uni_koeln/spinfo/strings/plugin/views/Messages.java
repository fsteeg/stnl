/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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

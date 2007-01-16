/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Wildcard matching check, as described in Gusfield 1999:199 f.
 * 
 * <p/>TODO: uses a naive computation of longest common extension, for that, a
 * suffix tree should be used, see {@link LCE}
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class Wildcards {
    /**
     * Wildcard matching check, as described in Gusfield 1999:199 f.
     * 
     * @param t
     *            The text
     * @param p
     *            The pattern, containing one or many wildcards: '*'
     * 
     * @return Returns a Collection of Strings containing the wildcard-matches
     *         of p found in t
     */
    public static Collection<String> getMatches(String t, String p) {
        Collection<String> result = new ArrayList<String>();
        for (int i = 0; i < t.length(); i++) {
            // step 1
            int j = 0;
            // h ist the i-bar in Gusfield
            int h = i;
            int n = p.length();
            int L;
            while (true) {
                // step 2
                L = LCE.longestCommonExtension(p, j, t, h);
                // step 3
                if (j + 1 + L == n + 1) {
                    result.add(t.substring(i, i + n));
                    break;
                }
                // step 4
                if (((j + L) < p.length() && p.charAt(j + L) == '*')
                        || ((h + L) < t.length() && t.charAt(h + L) == '*')) {
                    j = j + L + 1;
                    h = h + L + 1;
                } else
                    break;
            }
        }
        return result;
    }

    /**
     * Wildcard matching check as described in Gusfield 1999:199 f., modified
     * for words and sentences
     * 
     * @param t
     *            The text, containing multiple blankspace-separated words
     * @param p
     *            The pattern, containing multiple blankspace-separated words
     *            and one or many wildcards: '*'
     * 
     * @return Returns a Collection of Strings containing the wildcard-matches
     *         of p found in t
     */
    public static Collection<String> getWordBasedMatches(String t, String p) {
        String[] tTokens = t.split(" ");
        String[] pTokens = p.split(" ");
        Collection<String> result = new ArrayList<String>();
        for (int i = 0; i < tTokens.length; i++) {
            // step 1
            int j = 0;
            // h ist the i-bar in Gusfield
            int h = i;
            int n = pTokens.length;
            int L;
            while (true) {
                // step 2
                L = LCE.longestCommonExtensionWordBased(pTokens, j, tTokens, h);
                // step 3
                if (j + 1 + L == n + 1) {
                    String r = "";
                    for (int k = i; k < i + n; k++) {
                        r = r + tTokens[k] + " ";
                    }
                    result.add(r.trim());
                    // result.add(t.substring(i, i + n));
                    break;
                }
                // step 4
                if (((j + L) < pTokens.length && pTokens[j + L].equals("*"))
                        || ((h + L) < tTokens.length && tTokens[h + L]
                                .equals("*"))) {
                    j = j + L + 1;
                    h = h + L + 1;
                } else
                    break;
            }
        }
        return result;
    }
}

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
 * 
 * K-Mismatch check as described in Gusfield 1999:200 f. and modified for
 * words/sentences.
 * 
 * <p/>TODO: uses a naive computation of longest common extension, for that, a
 * suffix tree should be used, see {@link LCE}
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class KMismatch {
    /**
     * K-Mismatch check as described in Gusfield 1999:200 f.
     * 
     * @param t
     *            The text
     * @param p
     *            The pattern
     * @param k
     *            the number of allowed mismatches
     * @return Returns a Collection of Strings containing the k-mismatch-matches
     *         of p found in t
     */
    public static Collection<String> getMatches(String t, String p, int k) {
        Collection<String> result = new ArrayList<String>();
        for (int i = 0; i < t.length(); i++) {
            // step 1
            int j = 0;
            int h = i;
            int count = 0;
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
                else if (count < k) {
                    count++;
                    j = j + L + 1;
                    h = h + L + 1;
                    // back to step 2
                } else if (count == k) {
                    // System.out.println("no k-mismatch starting at: " + i);
                    break;
                }
            }

        }
        return result;
    }

    /**
     * K-Mismatch check as described in Gusfield 1999:200 f., modified for words
     * and sentences
     * 
     * @param t
     *            The text, containing multiple blankspace-separeted words
     * @param p
     *            The pattern, containing multiple blankspace-separeted words
     * @param k
     *            the number of allowed mismatches
     * @return Returns a Collection of Strings containing the matches with
     *         k-mismatches of p found in t
     */
    public static Collection<String> getWordBasedMatches(String t, String p,
            int k) {
        String[] tTokens = t.split(" ");
        String[] pTokens = p.split(" ");
        Collection<String> result = new ArrayList<String>();
        for (int i = 0; i < tTokens.length; i++) {
            // step 1
            int j = 0;
            int h = i;
            int count = 0;
            int n = pTokens.length;
            int L;
            while (true) {
                // step 2
                L = LCE.longestCommonExtensionWordBased(pTokens, j, tTokens, h);
                // step 3
                if (j + 1 + L == n + 1) {
                    String r = "";
                    for (int f = i; f < i + n && f < tTokens.length; f++) {
                        r = r + tTokens[f] + " ";
                    }
                    result.add(r.trim());
                    // result.add(t.substring(i, i + n));
                    break;
                }
                // step 4
                else if (count < k) {
                    count++;
                    j = j + L + 1;
                    h = h + L + 1;
                    // back to step 2
                } else if (count == k) {
                    // System.out.println("no k-mismatch starting at: " + i);
                    break;
                }
            }

        }
        return result;
    }

}

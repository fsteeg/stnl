/**
 * @author Fabian Steeg
 * Created on 06.01.2006
 *
 */
package de.uni_koeln.spinfo.strings.algo.tests;

import java.util.Collection;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.KMismatch;
import de.uni_koeln.spinfo.strings.algo.LCE;

/**
 * Tests for {@link KMismatch}
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class TestKMismatch extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test for {@link KMismatch}
     */
    public void testGetLongestCommonExtension() {
        String a = "abcdezas";
        String b = "abcdefghj";
        int res = LCE.longestCommonExtension(a, 1, b, 0);
        assertEquals(0, res);

        a = "abcdezas";
        b = "abcdefghj";
        res = LCE.longestCommonExtension(a, 0, b, 0);
        assertEquals(5, res);

        a = "abcdezas";
        b = "zsdabcdefghj";
        res = LCE.longestCommonExtension(a, 0, b, 3);
        assertEquals(5, res);
    }

    /**
     * Test for {@link KMismatch}
     */
    public void testGetMismatches() {
        String t = "abentbananaend";
        int k = 2;
        String p = "bend";
        Collection<String> results = KMismatch.getMatches(t, p, k);
        StringBuilder builder = new StringBuilder();
        assertEquals(3, results.size());
        for (String string : results) {
            System.out.println(string);
            builder.append(string + " ");
        }
        assertEquals("bent bana aend", builder.toString().trim());
    }

    /**
     * Test for {@link KMismatch}
     */
    public void testGetMatchesWordBased() {
        String t = "ich kaufe ein auto. ich kaufe ein boot. ich kaufe zwei bier. ich klaue ein haus.";
        String p = "ich sehe ein haus";
        Collection<String> results = KMismatch.getWordBasedMatches(t, p, 2);
        StringBuilder builder = new StringBuilder();
        for (String string : results) {
            System.out.println(string);
            builder.append(string + " ");
        }
        assertEquals(3, results.size());
        assertEquals(
                "ich kaufe ein auto. ich kaufe ein boot. ich klaue ein haus.",
                builder.toString().trim());
    }

}

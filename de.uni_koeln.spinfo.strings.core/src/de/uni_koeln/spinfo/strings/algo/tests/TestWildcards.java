/**
 * @author Fabian Steeg
 * Created on 06.01.2006
 *
 * @version $Revision: 1.2 $
 *
 */
package de.uni_koeln.spinfo.strings.algo.tests;

import java.util.Collection;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.LCE;
import de.uni_koeln.spinfo.strings.algo.Wildcards;

/**
 * Tests for {@link Wildcards}
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class TestWildcards extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test for {@link Wildcards}
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
     * Test for {@link Wildcards}
     */
    public void testGetMatches() {
        String t = "abentbananaend bend";
        String p = "ben*";
        Collection<String> results = Wildcards.getMatches(t, p);
        StringBuilder builder = new StringBuilder();
        assertEquals(2, results.size());
        for (String string : results) {
            System.out.println(string);
            builder.append(string + " ");
        }
        assertEquals("bent bend", builder.toString().trim());
    }

    /**
     * Test for {@link Wildcards}
     */
    public void testGetMatchesWordBased() {
        String t = "ich kaufe ein auto. ich kaufe ein boot. ich kaufe zwei bier. ich klaue ein schiff.";
        String p = "ich kaufe * *";
        Collection<String> results = Wildcards.getWordBasedMatches(t, p);
        StringBuilder builder = new StringBuilder();
        assertEquals(3, results.size());
        for (String string : results) {
            System.out.println(string);
            builder.append(string + " ");
        }
        assertEquals(
                "ich kaufe ein auto. ich kaufe ein boot. ich kaufe zwei bier.",
                builder.toString().trim());
    }

}

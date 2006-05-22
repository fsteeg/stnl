/**
 * @author Fabian Steeg
 * Created on 13.02.2006
 *
 * @version $Revision: 1.2 $
 *
 */
package de.uni_koeln.spinfo.strings.algo.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for all tests
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class AllTests {

    /**
     * @return The TestSuite contaiing all tests
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(
                "Test for de.uni_koeln.spinfo.strings.plugin.algo.tests");
        // $JUnit-BEGIN$
        suite.addTestSuite(TestWildcards.class);
        suite.addTestSuite(TestSuffixTree.class);
        suite.addTestSuite(TestKMismatch.class);
        suite.addTestSuite(TestUkkonenSuffixTree.class);
        suite.addTestSuite(TestWordSuffixTreeComplexity.class);
        // $JUnit-END$
        return suite;
    }

}

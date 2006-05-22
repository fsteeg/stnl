/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
//        suite.addTestSuite(TestWordSuffixTreeComplexity.class);
        // $JUnit-END$
        return suite;
    }

}

/**
 * @author Fabian Steeg
 * Created on 19.02.2006
 *
 * @version $Revision: 1.1 $
 * 
*/
package de.uni_koeln.spinfo.strings.algo.tests;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.UkkonenSuffixTree;

/**
 * Test for {@link UkkonenSuffixTree}
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class TestUkkonenSuffixTree extends TestCase {

    /**
     * Test method for 'de.uni_koeln.spinfo.strings.plugin.algo.suffixtrees.UkkonenSuffixTree.printTree(int)'
     */
    public void testPrintTree() {
        UkkonenSuffixTree tree = new UkkonenSuffixTree();
        tree.addSequence("abcabd", 1, false);
        tree.addSequence("abcabd", 2, false);
    }

}

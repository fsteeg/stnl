/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo.tests;

import java.util.ArrayList;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.SimpleSequenceAccessor;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.UkkonenSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SimpleNodeAccessor;

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
        UkkonenSuffixTree tree = new UkkonenSuffixTree(new SimpleNodeAccessor(), new SimpleSequenceAccessor());
        ArrayList<Long> l1 = new ArrayList<Long>();
        l1.add(1l);
        l1.add(2l);
        l1.add(3l);
        l1.add(1l);
        l1.add(2l);
        l1.add(4l);
        ArrayList<Long> l2 = new ArrayList<Long>();
        l2.add(1l);
        l2.add(2l);
        l2.add(3l);
        l2.add(1l);
        l2.add(2l);
        l2.add(4l);
        tree.addSequence(l1, 1, false);
        tree.addSequence(l2, 2, false);
        tree.printTree();
    }

}

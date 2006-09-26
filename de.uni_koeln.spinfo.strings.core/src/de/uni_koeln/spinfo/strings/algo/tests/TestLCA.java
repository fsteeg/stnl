/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo.tests;

import java.io.File;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.Util;
import de.uni_koeln.spinfo.strings.algo.lca.LCA;
import de.uni_koeln.spinfo.strings.algo.lca.TreeNode;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SimpleNodeAccessor;

/**
 * Tests for {@link LCA}
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class TestLCA extends TestCase {
    /**
     * Test for {@link LCA}
     */
    public void testLCA() {
        lca(Util.getText(new File("texts/text1.txt")));
        lca(Util.getText(new File("texts/text2.txt")));
        lca(Util.getText(new File("texts/text3.txt")));
        lca(Util.getText(new File("texts/text4.txt")));
        lca(Util.getText(new File("texts/text5.txt")));
        lca(Util.getText(new File("texts/text6.txt")));
        lca(Util.getText(new File("texts/text7.txt")));
        lca(Util.getText(new File("texts/text8.txt")));
    }

    private void lca(String text) {
        long start;
        long current;
        start = System.currentTimeMillis();
        WordSuffixTree tree = new WordSuffixTree(text, false, false, new SimpleNodeAccessor());
        current = System.currentTimeMillis();
        System.out.print("Tree took: " + (current - start) + " ms. ");
        start = System.currentTimeMillis();
        try {
            // preprocess the tree for lca queries:
            LCA lca = new LCA((TreeNode) tree.getRoot());
            TreeNode node = null;
            // do many lca request, they are in constant runtime:
            for (int i = 0; i < 1000000; i++) {
                node = lca.lca(65, 70);
            }
            System.out.print("lca: " + node.getDFSNum() + ", ");
        } catch (Exception x) {
            x.printStackTrace();
        }
        current = System.currentTimeMillis();
        System.out.println("LCA took: " + (current - start) + " ms. ");
    }

}

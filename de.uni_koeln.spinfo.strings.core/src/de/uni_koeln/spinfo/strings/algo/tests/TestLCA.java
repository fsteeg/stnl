/**
 * @author Fabian Steeg
 * Created on 08.03.2006
 *
 * @version $Revision: 1.1 $
 * 
 */
package de.uni_koeln.spinfo.strings.algo.tests;

import java.io.File;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.Util;
import de.uni_koeln.spinfo.strings.algo.lca.LCA;
import de.uni_koeln.spinfo.strings.algo.lca.TreeNode;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;

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
        WordSuffixTree tree = new WordSuffixTree(text, false, false, null);
        current = System.currentTimeMillis();
        System.out.print("Tree took: " + (current - start) + " ms. ");
        start = System.currentTimeMillis();
        try {
            // preprocess the tree for lca queries:
            LCA lca = new LCA((TreeNode) tree.getRoot());
            TreeNode node = null;
            // do many lca request, they are in constant runtime:
            for (int i = 0; i < 1000000; i++) {
                node = lca.LCA(65, 70);
            }
            System.out.print("lca: " + node.getDFSNum() + ", ");
        } catch (Exception x) {
            x.printStackTrace();
        }
        current = System.currentTimeMillis();
        System.out.println("LCA took: " + (current - start) + " ms. ");
    }

}

/**
 * @author Fabian Steeg
 * Created on 21.02.2006
 *
 */
package de.uni_koeln.spinfo.strings.algo;

import java.util.ArrayList;

import de.uni_koeln.spinfo.strings.algo.lca.LCA;
import de.uni_koeln.spinfo.strings.algo.lca.TreeNode;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.SuffixNode;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;

/**
 * This class contains a sketch of what to do to get the LCE through the
 * {@link LCA} in constant time. Such a constant time LCE-computation can be
 * used for linear-time matching with wild cards and k-mismatch (see Gusfield),
 * instead of the used naive computation of LCE in {@link KMismatch} and
 * {@link Wildcards}.
 * 
 * <p/> TODO: Add proper way to the {@link WordSuffixTree} to get the leaf for a
 * specific suffix, then the constant time LCE computation would basically work
 * 
 * <p/> This class also contains static methods for naive computations of the
 * LCE.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class LCE {

    private String s2;

    private String s1;

    private WordSuffixTree tree;

    private LCA lca;

    /**
     * Instantiates an LCE with the two texts to find the longest common
     * extension for.
     * 
     * <p/> Attention: This doesn't work properly, it's a sketch. Use the
     * static, naive implementations in this class.
     * 
     * @param s1
     *            Text 1
     * @param s2
     *            Text 2
     */
    public LCE(String s1, String s2) {
        this.s1 = s1;
        this.s2 = s2;
        // build a generalized suffix tree for the two texts
        tree = new WordSuffixTree(s1 + "." + s2, false, true, null);
        tree.exportDot("export.dot");
        lca = new LCA((TreeNode) tree.getRoot());
    }

    /**
     * <p/> Attention: This doesn't work properly, it's a sketch. Use the
     * static, naive implementations in this class.
     * 
     * @param i
     *            The start-index in text 1
     * @param j
     *            The start-index in text 2
     * @return Returns the longest common extension of the two strings in this
     *         LCE beginning at i and j for text1 and 2 respectively.
     */
    public int getLongestCommonExtension(int i, int j) {
        // the length of the lce is the string depth of the lca of the nodes
        // representing the suffixes
        TreeNode treeNode = (TreeNode) tree.getNodeForSuffix(1, i);
        TreeNode treeNode2 = (TreeNode) tree.getNodeForSuffix(2, j);
        return tree.getPathLength(((SuffixNode) lca.LCA(treeNode.getDFSNum(),
                treeNode2.getDFSNum())));

    }

    private int getNodeIdForSuffix(String s, int j) {
        ArrayList<SuffixNode> leafs = tree.getAllNodes(tree.getRoot(), null,
                true);
        for (int i = 0; i < leafs.size(); i++) {
            CharSequence edgeLabel = tree.getEdgeLabel(leafs.get(i));
            if (edgeLabel.toString().replaceAll("\\$", "").trim().equals(
                    s.substring(j))) {
                System.out.println(edgeLabel);
                return ((TreeNode) leafs.get(i)).getDFSNum();

            }
        }
        return 0;
    }

    /**
     * Naive computation of the longest common extension
     * 
     * @param t1
     *            Text 1
     * @param i1
     *            The index in text 1
     * @param t2
     *            Text 2
     * @param i2
     *            The index in text 2
     * @return The length of the longest common extension starting at x1 in t1
     *         and at x2 int t2
     */
    public static int longestCommonExtension(String t1, int i1, String t2,
            int i2) {
        // TODO implement solution using suffix trees for p
        int res = 0;
        for (int i = i1; i < t1.length() && i2 < t2.length(); i++, i2++) {
            if (t1.charAt(i) == t2.charAt(i2))
                res++;
            else
                return res;
        }
        return res;
    }

    /**
     * Naive computation of the longest common extension for words/sentences
     * 
     * @param t1
     *            Text 1
     * @param i1
     *            The index in text 1
     * @param t2
     *            Text 2
     * @param i2
     *            The index in text 2
     * @return The length of the longest common extension starting at x1 in t1
     *         and at x2 int t2
     */
    public static int longestCommonExtensionWordBased(String[] t1, int i1,
            String[] t2, int i2) {
        // TODO implement solution using suffix trees for p
        int res = 0;
        for (int i = i1; i < t1.length && i2 < t2.length; i++, i2++) {
            if (t1[i].equals(t2[i2]))
                res++;
            else
                return res;
        }
        return res;
    }

    /**
     * Experimental runs of constant time LCE computation
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {
        LCE lce = new LCE("a b b a", "c b b a");
        int e = lce.getLongestCommonExtension(1, 1);
        System.out.println("LCE-length: " + e);
        // System.out.println(lce.getLabelForNode(e));
    }

}

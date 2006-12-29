/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees.naive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.uni_koeln.spinfo.strings.algo.Util;

/**
 * Construction of a simple (i.e. not compact, a suffix trie and with quadratic
 * runtime) suffix tree.
 * 
 * (Algorithmus 4.7, Seite 57 in Boeckenhauer & Bongartz)
 * 
 * @author Fabian Steeg
 * 
 */
public class SimpleSuffixTree extends AbstractSuffixTree {
    private boolean forChars;

    private boolean reverse;

    /**
     * @param text
     *            The text to be represented by the suffix tree, a terminating
     *            "$" is appended if not already present.
     * @param reverse
     *            Whether the elements should be reversed ("prefix tree")
     */
    public SimpleSuffixTree(String text, boolean forChars, boolean reverse) {
        super(text, forChars, reverse, true);
        this.forChars = forChars;
        this.reverse = reverse;
        constructTree();
    }

    /**
     * @param file
     *            The file to load the text from
     * @param forChars
     *            If true, the tree will be a char-based, traditional tree, else
     *            a word based tree fro sentences
     * @param reverse
     *            If true the tree will be build reversed
     */
    public SimpleSuffixTree(File file, boolean forChars, boolean reverse) {
        this(Util.getText(file), forChars, reverse);
    }

    /**
     * Creates the root node and inserts all suffixes into this tree.
     */
    private void constructTree() {
        super.root = new SuffixTreeNode();
        int pathCounter = 0;
        for (int j = 0; j < super.text.size(); j++) {
            String sentence = super.text.get(j);
            String[] s;
            if (!forChars)
                s = sentence.split(" ");
            else {
                s = sentence.split("", 0);
                String[] b = new String[s.length - 1];
                for (int i = 1; i < s.length; i++) {
                    b[i - 1] = s[i];
                }
                s = b;
            }
            if (reverse && !forChars) {
                String[] n = new String[s.length];
                for (int i = 0; i < n.length; i++) {
                    n[i] = s[(s.length - 1) - i];
                }
                s = n;
            }

            for (int i = 0; i < s.length; i++) {
                List<String> suffixList = new ArrayList<String>();
                for (int k = i; k < s.length; k++) {
                    suffixList.add(s[k]);
                }
                pathCounter++;
                super.root.addSuffix(suffixList, pathCounter);
                System.out.println();
            }

        }

    }

    /**
     * Constructs a simple suffix tree for a given text and prints some
     * information about the constructed tree to the console.
     * 
     * @param args
     *            A String[] with one element, the text to be represented by
     *            this tree, or an empty String[] for execution without
     *            arguments (see inline comment).
     */
    public static void main(String[] args) {
        // if a text is specified use that given text, else use the one
        // hard-coded here:
        String text = args.length == 1 ? args[0] : "dabdac";
        long start = System.currentTimeMillis();
        // instantiate the tree:
        SimpleSuffixTree tree = new SimpleSuffixTree(text, false, false);
        // print info:
        System.out
                .println("\nConstructed simple (not compact) suffix tree for '"
                        + tree.text + "' in "
                        + (System.currentTimeMillis() - start) + " ms:\n");
        start = System.currentTimeMillis();
        System.out.println(tree.root.toString());
        System.out.println(tree.root.toDotString());
        System.out.println("Output took "
                + (System.currentTimeMillis() - start) + " ms. ("
                + tree.root.getNodeCount() + " nodes)\n");
    }

    public List<String> getText() {
        return text;
    }

    public SuffixTreeNode getRoot() {
        return root;
    }

}

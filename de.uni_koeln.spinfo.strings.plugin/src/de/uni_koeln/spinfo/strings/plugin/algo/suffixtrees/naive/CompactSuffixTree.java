/**
 * 
 */
package de.uni_koeln.spinfo.strings.plugin.algo.suffixtrees.naive;

import de.uni_koeln.spinfo.strings.algo.Util;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.naive.AbstractSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.naive.SimpleSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.naive.SuffixTreeNode;

/**
 * A compact suffix tree, constructed from a simple suffix tree (a suffix trie),
 * with a runtime complexity of O(n^2).
 * 
 * (Algorithmus 4.8, Seite 62 in Boeckenhauer & Bongartz)
 * 
 * @author Fabian Steeg
 * 
 */
public class CompactSuffixTree extends AbstractSuffixTree {

    public static int c;

    /**
     * @param simpleSuffixTree
     *            The simple suffix tree that should be made compact.
     * @param verbose
     *            Flag to indicate if verbose output should be displayed.
     */
    public CompactSuffixTree(SimpleSuffixTree simpleSuffixTree, boolean verbose) {
        // both trees represent the same text
        super(simpleSuffixTree.getText());
        // but this one has compact nodes and compact labels
        // if (!verbose)
        // super.root = compactLabels(compactNodes(simpleSuffixTree.root, 0));
        // else {
        /*
         * verbose output displays the simple tree, the compact tree with long
         * labels and the compact tree with compact labels:
         */
        // System.out.println("Simple suffix tree:\n" + simpleSuffixTree.root);
        super.root = compactNodes(simpleSuffixTree.getRoot(), 0);

        // exportAsDot("output.dot");

        // System.out.println(simpleSuffixTree.root.toDotString(0)+"}");
        System.out.println("Compact suffix tree, long labels:\n"
                + simpleSuffixTree.getRoot());
        // System.out.println("Compact suffix tree, long labels, as
        // dot-graph:\n"
        // + simpleSuffixTree.root.toDotString(0));
        // super.root = compactLabels(super.root);
        // System.out.println("Compact suffix tree, compact labels:\n"
        // + simpleSuffixTree.root);
        // }

    }

    /**
     * Exports the tree as dot to the given location
     * 
     * @param location
     *            The location to export to
     */
    public void exportAsDot(String location) {
        Util.saveString(location, root.toDotString() + "}");
    }

    /**
     * Makes a simple suffix tree compact by removing inner nodes with exactly
     * one child node.
     * 
     * @param node
     *            The root node of the simple suffix tree to make compact.
     * @param nodeDepth
     *            The current node depth.
     * @return The root node of the compact suffix tree.
     */
    private SuffixTreeNode compactNodes(SuffixTreeNode node, int nodeDepth) {
        // adjust the node depth while making the tree compact
        node.setNodeDepth(nodeDepth);
        for (SuffixTreeNode child : node.getChildren()) {
            // remove all inner nodes with exactly one child node
            while (child.getChildren().size() == 1) {
                SuffixTreeNode grandchild = child.getChildren().iterator()
                        .next();
                // set the new longer label
                child.getIncomingEdge().setLabel(
                        child.getIncomingEdge().getLabel() + ", "
                                + grandchild.getIncomingEdge().getLabel());
                // set the new string depth
                child.setStringDepth(child.getStringDepth()
                        + grandchild.getIncomingEdge().getLabel().length());
                // skip the grandchild by setting the grandchild's children as
                // the
                // child's children
                child.setChildren(grandchild.getChildren());
            }
            // for the others, continue
            child = compactNodes(child, nodeDepth + 1);
        }
        return node;
    }

    /**
     * Makes the long labels compact, if required.
     * 
     * @param node
     *            The root node of the simple suffix tree with long labels to
     *            make compact.
     * @return The root node of the compact suffix tree with compact labels.
     */
    @SuppressWarnings("unused")
    private SuffixTreeNode compactLabels(SuffixTreeNode node) {
        if (!node.isRoot()) {
            // make the label compact
            node.getIncomingEdge().makeLabelCompact(inputAlphabetSize,
                    text.size(), node.getParent().getStringDepth());
        }
        for (SuffixTreeNode child : node.getChildren()) {
            // for the others, continue
            child = compactLabels(child);
        }
        return node;
    }

    /**
     * Constructs a compact suffix tree for a given text by first constructing a
     * simple suffix tree and then making it compact. Prints some information
     * about the constructed tree to the console.
     * 
     * @param args
     *            A String[] with one or two elements, the text to be
     *            represented by this tree, with an optional argument for
     *            verbose output (alone of before the text), or an empty
     *            String[] for execution without arguments (see inline comment).
     */
    public static void main(String[] args) {
        // if a text is specified use that given text, else use the one
        // hard-coded here:
        String text = "aaabbbc";
        final String verboseFlag = "v";
        final String wordsFlag = "w";
        final String reverseFlag = "r";
        boolean forWords = false;
        boolean verbose = false;
        boolean reverse = false;
        // one argument: -v, -vw, -w or text
        if (args.length == 1) {
            if (args[0].contains(wordsFlag)) {
                forWords = true;
            }
            if (args[0].contains(reverseFlag)) {
                reverse = true;
            }
            if (args[0].contains(verboseFlag)) {
                verbose = true;
            } else
                text = args[0];
        }
        // two arguments: eg. -v text
        else if (args.length == 2) {
            if (args[0].contains(wordsFlag)) {
                forWords = true;
            }
            if (args[0].contains(reverseFlag)) {
                reverse = true;
            }
            if (args[0].contains(verboseFlag)) {
                verbose = true;
                text = args[1];
            } else
                text = args[1];
        }
        long start = System.currentTimeMillis();
        // instantiate the simple suffix tree:
        SimpleSuffixTree simpleSuffixTree = new SimpleSuffixTree(text,
                !forWords, reverse);
        // System.out.println("\nConstructed simple suffix tree for '"
        // + simpleSuffixTree.text + "' in "
        // + (System.currentTimeMillis() - start) + " ms. ("
        // + simpleSuffixTree.root.getNodeCount() + " nodes).");
        // System.out.println();
        start = System.currentTimeMillis();
        // instantiate the compact suffix tree:
        CompactSuffixTree compactSuffixTree = new CompactSuffixTree(
                simpleSuffixTree, verbose);
        System.out
                .println("Constructed compact suffix tree from simple suffix tree in "
                        + (System.currentTimeMillis() - start)
                        + " ms. ("
                        + compactSuffixTree.root.getNodeCount() + " nodes).\n");
        // System.out
        // .println("Edges are labeled with strings in single quotes (') or
        // start and end index "
        // + "\n(1-based) in the format [start..end].\n");
        // if (verbose) {
        // // the input text with indices, for reference:
        // System.out.println("1-based indices of input text '" + text
        // + "':\n");
        // for (int i = 0; i < text.length(); i++) {
        // System.out.println(text.charAt(i) + " --> " + (i + 1));
        // }
        // System.out.println();
        // }
        // print the constructed compact tree with compact labels:
        // System.out
        // .println("Nodes are labeled with their string depth (sum of the
        // string lengths"
        // + "\nof edge labels on a path from the root node to the labeled
        // node):\n");
        // start = System.currentTimeMillis();
        // System.out.println(compactSuffixTree.root.toString());
        // System.out.println("Output took "
        // + (System.currentTimeMillis() - start) + " ms.\n");
    }

    /**
     * @return The Tree size
     */
    public int getTreeSize() {
        return root.getSize();
    }

    @Override
    public String toString() {
        return root.toString();
    }
}

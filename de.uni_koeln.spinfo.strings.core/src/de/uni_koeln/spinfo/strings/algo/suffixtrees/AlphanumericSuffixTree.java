/**
 * 
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees;

import java.util.ArrayList;

import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor;

/**
 * @author fsteeg
 * 
 */
public abstract class AlphanumericSuffixTree extends NumericSuffixTree {
    String text;

    boolean reverse;

    boolean generalized;

    public Mapper mapper;

    SimpleSequenceAccessor sentenceAccessor;

    /**
     * @param text
     *            The text to add to the tree, termination is handeled
     *            internally
     * @param reverse
     *            If true a tree is built for the reveresed word order
     * @param generalized
     *            If true the text is splittet into sentences, which are
     *            inserted separately (<b>Attention</b>: In the current
     *            Implementation, for generalized trees the construction runtime
     *            seems to grow quadratic)
     */
    public AlphanumericSuffixTree(String text, boolean reverse,
            boolean generalized, NodeAccessor accessor) {
        super(accessor, new SimpleSequenceAccessor());
        this.reverse = reverse;
        this.generalized = generalized;
        setText(text);
    }

    /**
     * Sets the text and constructs the tree. TODO implement adding of multiple
     * texts from outside (public API)
     * 
     * @param text
     *            The text to add, termination is handled internally
     * 
     */
    private void setText(String text) {
        this.text = text;
        construct();
    }

    abstract void construct();

    /**
     * <b>Experimental</b>
     * 
     * @param textNo
     *            The text number to get the suffix from
     * @param suffixIndex
     *            The index at which the suffix starts
     * @return The leaf representing the suffix starting at suffixIndex in
     *         textNo
     */
    public Node getNodeForSuffix(int textNo, int suffixIndex) {
        ArrayList<Node> leafs = this.getAllNodes(getRoot(), null, true);
        for (int i = 0; i < leafs.size(); i++) {
            Node node = (Node) leafs.get(i);
            if (node.getTextNumber() == textNo
                    && node.getSuffixIndex() == suffixIndex)
                return node;
        }
        return null;
    }

    /**
     * <b>Experimental</b>
     * 
     * @param id
     *            The id of the node to get
     * @return The Node with id e
     */
    public Node getNodeForId(int id) {
        ArrayList<Node> leafs = this.getAllNodes(getRoot(), null, false);
        for (int i = 0; i < leafs.size(); i++) {
            Node node = (Node) leafs.get(i);
            if (node.getDfs() == id)
                return node;
        }
        return null;
    }

    /**
     * Prints all nodes
     */
    public void printNodes() {
        Node root = super.getRoot();
        ArrayList<Node> all = new ArrayList<Node>();
        all.addAll(super.getAllNodes(root, null, false));
        for (Node node : all) {
            System.out.println(node.toString());
        }

    }

    /**
     * @param string
     *            The location to store the dot file
     */
    public void exportDot(String string, boolean num) {
        if (num)
            super.exportDot(string);
        else
            mapper.exportDot(string);
    }
}

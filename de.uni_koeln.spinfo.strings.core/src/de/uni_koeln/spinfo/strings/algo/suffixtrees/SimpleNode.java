/**
 * @author Fabian Steeg
 * Created on 21.02.2006
 *
 * @version $Revision: 1.2 $
 * 
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees;

import java.util.HashMap;
import java.util.Iterator;

import de.uni_koeln.spinfo.strings.algo.lca.TreeNode;

/**
 * Node Class for the suffix tree, implementing an Interface for {@link LCA}-queries
 * 
 * @author Francois Pepin (original version from BioJava API)
 * @author Fabian Steeg (fsteeg)
 */
public class SimpleNode extends SuffixNode implements TreeNode {

    int dfs = 0;

    int id = 0;

    public int suffixIndex;

    /**
     * Creates a root
     */
    public SimpleNode() {
        parent = null;
        suffixLink = null;
        labelStart = 0;
        labelEnd = 0;
        children = new HashMap();
        additionalLabels = null;
        textNumber = 0;
        suffixIndex = 0;
    }

    /**
     * creates a leaf
     * 
     * @param parent
     *            the parent node
     * @param position
     *            the starting value of the suffix
     */
    public SimpleNode(SuffixNode parent, int position, int textNumber,
            int suffixIndex) {
        this();
        this.parent = parent;
        labelStart = position;
        labelEnd = A_LEAF;
        children = null;
        this.textNumber = textNumber;
        this.suffixIndex = suffixIndex;
        // checkParent(this);
    }

    /**
     * creates an internal node
     * 
     * @param parent
     *            the parent of this node
     * @param labelStart
     *            the starting point of the path label
     * @param labelStop
     *            the ending point of the path label
     */
    public SimpleNode(SuffixNode parent, int labelStart, int labelStop,
            int textNumber, int suffixIndex) {
        this();
        this.parent = parent;
        this.labelStart = labelStart;
        this.labelEnd = labelStop;
        this.textNumber = 0;
        this.suffixIndex = 0;
        // checkParent(this);
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.SuffixNode#isTerminal()
     */
    public boolean isTerminal() {
        return children == null;
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.SuffixNode#hasChild(java.lang.Character)
     */
    public boolean hasChild(Character x) {
        return getChild(x) != null;
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.SuffixNode#getChild(java.lang.Character)
     */
    public SuffixNode getChild(Character x) {
        return (children == null) ? null : (SuffixNode) children.get(x);
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.SuffixNode#getParent()
     */
    public SuffixNode getParent() {
        return parent;
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.lca.TreeNode#numChildren()
     */
    public int numChildren() {
        return children == null ? 0 : children.keySet().size();
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.lca.TreeNode#getChild(int)
     */
    public TreeNode getChild(int num) {
        Iterator iter = children.keySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            TreeNode node = (TreeNode) children.get(iter.next());
            if (i == num)
                return node;
            i++;
        }
        return null;
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.lca.TreeNode#acceptDFSNum(int)
     */
    public void acceptDFSNum(int x) {
        this.dfs = x;
        this.id = dfs;

    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.lca.TreeNode#getDFSNum()
     */
    public int getDFSNum() {
        return this.dfs;
    }

}
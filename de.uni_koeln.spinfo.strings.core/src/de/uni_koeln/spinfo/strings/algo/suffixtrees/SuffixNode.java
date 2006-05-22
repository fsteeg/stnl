/**
 * @author Fabian Steeg
 * Created on 21.02.2006
 *
 * @version $Revision: 1.2 $
 * 
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees;

import java.util.HashMap;

/**
 * Abstract superclass for tree nodes
 * 
 * @author Francois Pepin (original version from BioJava API)
 * @author Fabian Steeg (fsteeg)
 */
public abstract class SuffixNode {

    public static final int A_LEAF = -1;

    protected SuffixNode parent;

    protected SuffixNode suffixLink;

    protected int labelStart, labelEnd;

    protected HashMap children;

    protected int[] additionalLabels;

    public int id;

    protected int textNumber;

    /**
     * Determine is this node is terminal (has no children).
     * <p>
     * Note that this only happens at the terminated node (if the sequences have
     * been terminated.
     * 
     * @return <code>true</code> if and only if this node has no children.
     */

    abstract public boolean isTerminal();

    /**
     * Determine if this node has a child corresponding to a given character
     * 
     * @param i
     *            the first <code>Character</code> of the edge coming down
     *            this node.
     * @return <code>true</code> if the node has a child going down from that
     *         character, false otherwise
     */
    abstract public boolean hasChild(Character i);

    /**
     * Gets the child of of a node that is coming down from that particular
     * node. It returns null if no child exists or if no child exists starting
     * on that particular character.
     * 
     * @param i
     *            the first <code>Character</code> of the edge coming down
     *            this node
     * @return the appropriate child <code>SuffixNode</code>, or null if it
     *         doesn't exists.
     */
    public abstract SuffixNode getChild(Character i);

    // abstract void addChild(SuffixTree tree, int i, SuffixNode n);

    /**
     * Returns the parent of this node, null if it's the root.
     * 
     * @return the parent of this node, null if it's the root.
     */
    public abstract SuffixNode getParent();

    /**
     * @return Returns the children
     */
    public HashMap getChildren() {
        return children;
    }

}
package de.uni_koeln.spinfo.strings.algo.lca;

/**
 * This interface is used to pass a tree to the LCA algorithm. It's based on
 * numbers rather than iterators partly because I wanted to make the core of the
 * algorithm work without the 1.2 collection classes (or anything much else for
 * that matter) and partly because this makes it really easy to implement this
 * interface with an array of child pointers, without stopping you using an List
 * (ArrayList for efficiency, I guess - this does make using LinkedList a bad
 * idea). Without the excuse of 1.1 compatibility, I'd be forced to make this
 * use Iterators - I don't need to access children at random, only in sequence,
 * so I shouldn't require an interfaces that implies that I do.
 * 
 * @author A. G. McDowell (original version)
 * @author Fabian Steeg (fsteeg)
 */
public interface TreeNode {
    /**
     * return the number of children this node has
     * 
     * @return the number of children this node has
     */
    int numChildren();

    /**
     * return a selected child of this node.
     * 
     * @param num
     *            the number of child wanted, counting from 0
     * @return a child of this node
     */
    TreeNode getChild(int num /* from 0 */);

    /**
     * Accept a number by which this node is to be identified to the LCA
     * algorithm when we want to find an LCA. This is actually the number of
     * this node in depth first search order, starting at 1. This callback is
     * only made when the tree is fed to the LCA algorithm.
     * 
     * @param x
     *            the number used to identify this node to the LCA algorithm
     *            when looking for ancestors.
     */
    void acceptDFSNum(int x);

    int getDFSNum();
}
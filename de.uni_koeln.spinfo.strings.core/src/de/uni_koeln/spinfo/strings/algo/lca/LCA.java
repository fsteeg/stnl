package de.uni_koeln.spinfo.strings.algo.lca;

import de.uni_koeln.spinfo.strings.algo.lca.CycleDetector;

/**
 * The Lowest Common Ancestor algorithm of Schieber and Vishkin (sequential
 * version only), roughly as described in "On finding lowest common ancestors:
 * simplification and parallelization", by Baruch Schieber and Uzi Vishkin, SIAM
 * J. Comput. Vol 17, No 6, December 1988 <p/> Allows constant-time LCA queries
 * after a linear-time preprocessing.
 * 
 * @author A. G. McDowell (original version)
 */

public class LCA {
    /** table of pointers to our nodes, addressed by dfs # */
    private OurNode[] on;

    /** table of pointers to our nodes, addressed by inLabel */
    private OurNode[] head;

    /** next dfs number to be allocated */
    private int nextNum;

    /**
     * detect all infinite loops due to cycles in graphs and perhaps some
     * repeats due to acyclic graphs that have multiple pointers leading to the
     * same node.
     */
    private CycleDetector cd;

    /**
     * Create a structure used to answer Lowest Common Ancestor queries, from a
     * tree of objects implementing the TreeNode interface, passed by a
     * reference to its root. Each query takes constant time, independent of the
     * size of the tree. Building this structure takes time linear in the size
     * of the tree.
     * 
     * @param n
     *            the root of the tree
     * @exception IllegalArgumentException
     *                if we find that we have revisited a node on a depth first
     *                search. This will never happen if our tree really is a
     *                tree, because we will never revisit nodes in the same
     *                search. It is sure to happen if our 'tree' is a cyclic
     *                graph. Unintended revisits will happen in acyclic graphs,
     *                but we may very well not detect them. We could detect them
     *                for sure at small worst case constant cost, but I can only
     *                see how to do it if we are allowed to mark TreeNodes,
     *                which I'd rather not do.
     */
    public LCA(TreeNode n) {
        // Use depth first search to set up OurNode info. Very
        // convenient to start at 1: for one thing, it means that
        // every node number has a leftmost 1 bit.
        if (n == null)
            return;
        nextNum = 1;
        cd = new CycleDetector();
        on = new OurNode[10];
        head = new OurNode[10];
        // Build most of the structure: a copy of the tree, with depth
        // first search numbers, inLabel, and parent pointers
        dfs(n, 0);
        // use second dfs to set up true ancestor bitmaps
        dfs2(on[1], 0);
    }

    /**
     * Use depth first search to set up OurNodes and assign numbers.
     * <p>
     * We assign inLabels here. The inLabel of a node is the node number of that
     * node or any of its descendants with the largest number of contiguous
     * zeros ending in the lsb. The inLabels give rise to a binary tree
     * structure where it is easy to work out LCAs. A node ending in e.g.
     * abcd1000 has children abcd0100 and abcd1100, and descendants
     * abcd0001..abcd1111. The level of each node in this tree depends on the
     * position of its rightmost set bit. The ancestor at some level of any node
     * can be found by setting the bit for that level and clearing all bits to
     * its right. Each parent-child link in the real tree either corresponds to
     * a parent-descendant path in the tree of inLabels, or to two nodes with
     * the same inLabel. All sets of nodes with the same inLabel are single
     * lines of parent-child links. Some parent-child links in the tree of
     * inLabels are not ancestor-descendant links in the real tree, though.
     * </p>
     * 
     * @param n
     *            the TreeNode to work on
     * @param depth
     *            the current recursion depth (starts depth = 0)
     * @return the inLabel of TreeNode n.
     * @exception IllegalStateException
     *                if the input tree is in fact a more general graph and we
     *                detect a repeated visit to a node in the tree because of
     *                this.
     */
    private int dfs(TreeNode n, int depth) {
        cd.check(n); // Cycle detection
        if (nextNum >= on.length) {
            OurNode[] on2 = new OurNode[nextNum * 2 + 1];
            System.arraycopy(on, 0, on2, 0, on.length);
            on = on2;
        }
        OurNode here = new OurNode();
        on[nextNum] = here;
        here.depth = depth;
        int numHere = nextNum++;
        here.dfsNum = numHere;
        here.tn = n;
        here.firstChild = null;
        here.next = null;
        int todo = n.numChildren();
        int inLabel = numHere;
        int prevNum = numHere;
        int zeros = Log2.lowZeros(inLabel);
        for (int i = 0; i < todo; i++) {
            int childNum = nextNum;
            int thatLabel = dfs(n.getChild(i), depth + 1);
            if (Log2.lowZeros(thatLabel) > zeros) {
                inLabel = thatLabel;
                zeros = Log2.lowZeros(inLabel);
            }
            on[childNum].parent = here;
            if (prevNum == numHere)
                here.firstChild = on[childNum];
            else
                on[prevNum].next = on[childNum];
            prevNum = childNum;
        }
        here.inLabel = inLabel;
        if (inLabel >= head.length) {
            OurNode[] newHead = new OurNode[inLabel * 2 + 1];
            System.arraycopy(head, 0, newHead, 0, head.length);
            head = newHead;
        }
        head[inLabel] = here;
        // Return the assigned number to the treeNode
        n.acceptDFSNum(numHere);
        return inLabel;
    }

    /**
     * set up bitmap of true ancestors in each OurNode. These are the ancestors
     * according to inLabel which are also ancestors according to the original
     * links. Each ancestor is marked by the position of its lowest set bit.
     * Count each node as its own ancestor, too
     */
    private void dfs2(OurNode target, int bits) {
        bits |= 1 << Log2.lowZeros(target.inLabel);
        target.trueAncestors = bits;
        for (target = target.firstChild; target != null; target = target.next) {
            dfs2(target, bits);
        }
    }

    /**
     * Work out the lowest common ancestor of two nodes identified by their
     * acceptDFSNum number, in constant time.
     * 
     * @param id1
     *            the acceptDFSNum of a node requiring an ancestor
     * @param id2
     *            the acceptDFSNum of a node requiring an ancestor
     * @return their lowest common ancestor
     * @exception IllegalArgumentException
     *                if rubbish numbers
     * @exception IllegalStateException
     *                if fails an internal check
     */
    public TreeNode LCA(int id1, int id2) {
        if (id1 <= 0 || id2 <= 0 || id1 >= nextNum || id2 >= nextNum)
            throw new IllegalArgumentException("numbers out of range");
        OurNode ida = on[id1];
        OurNode idb = on[id2];
        if (ida.inLabel == idb.inLabel) { // On a path down the tree. Return
            // the highest node
            if (ida.depth <= idb.depth)
                return ida.tn;
            return idb.tn;
        }
        // Find the inlabel of the LCA in the binary tree of inlabels.
        // This has bits set as far right as possible such that the
        // bits to the left of its rightmost set bit are identical to
        // those of both inLabels, and that bit is at least as far left as
        // the rightmost bit of both inLabels, so it is the same as either
        // inLabel, or an ancestor of them. (the original paper does this
        // with a boolean expression and a single lg2()).
        int x = ida.inLabel ^ idb.inLabel; // not zero - special case above
        int diffBit = Log2.lg2(x);
        int rightA = Log2.lowZeros(ida.inLabel);
        int rightB = Log2.lowZeros(idb.inLabel);
        int pos = diffBit;
        if (pos < rightA)
            pos = rightA;
        if (pos < rightB)
            pos = rightB;
        int targetLabel = (ida.inLabel & ~((1 << pos) - 1)) | (1 << pos);
        // System.err.println("Tree ancestor " +
        // Integer.toHexString(targetLabel));
        // Now find the inLabel of the true ancestor. This might be higher
        // up in the inLabel tree, because not every parent-child in the
        // inLabel tree is a ancestor-descendant link in the real tree
        pos = Log2.lowZeros(ida.trueAncestors & idb.trueAncestors
                & ~((1 << pos) - 1));
        targetLabel = (targetLabel & ~((1 << pos) - 1)) | (1 << pos);
        // System.err.println("pos " + pos + " targetLabel " +
        // Integer.toHexString(targetLabel));
        // Now we have narrowed our target down to a path of nodes with the
        // same inLabel. Our head array can find the top of this path, but
        // we need the points on this path where the links to ida and idb
        // feed in. We work this out by finding the heads of the paths
        // to ida and idb and moving up, unless of course ida or idb is on
        // the target path already.
        if (ida.inLabel != targetLabel) { // The inLabel we seek is the
            // highest true ancestor lower than
            // the target label. The node itself counts as an ancestor here
            // System.err.println("ainLabel " +
            // Integer.toHexString(ida.inLabel) + " target " +
            // Integer.toHexString(targetLabel) + " ancestors " +
            // ida.trueAncestors);
            // System.err.println("Mask " + Integer.toHexString(
            // (1 << pos) - 1));
            int pos2 = Log2.lg2(ida.trueAncestors & ((1 << pos) - 1));
            int target2 = (ida.inLabel & ~((1 << pos2) - 1)) | (1 << pos2);
            // System.err.println("inLabel " +
            // Integer.toHexString(ida.inLabel) + " ancestors " +
            // Integer.toHexString(ida.trueAncestors) + " target " +
            // Integer.toHexString(target2));
            ida = head[target2].parent;
            // System.err.println("ida " + ida);
        }
        if (idb.inLabel != targetLabel) { // The inLabel we seek is the
            // highest true ancestor lower than
            // the target label
            // System.err.println("binLabel " +
            // Integer.toHexString(idb.inLabel) + " target " +
            // Integer.toHexString(targetLabel));
            // System.err.println("Mask " + Integer.toHexString(
            // (1 << pos) - 1));
            int pos2 = Log2.lg2(idb.trueAncestors & ((1 << pos) - 1));
            int target2 = (idb.inLabel & ~((1 << pos2) - 1)) | (1 << pos2);
            // System.err.println("inLabel " +
            // Integer.toHexString(idb.inLabel) + " ancestors " +
            // Integer.toHexString(idb.trueAncestors) + " target " +
            // Integer.toHexString(targetLabel) + " got " +
            // Integer.toHexString(target2));
            idb = head[target2].parent;
            // System.err.println("idb " + idb);
        }
        if (ida.inLabel != idb.inLabel)
            throw new IllegalStateException("Failed to reach path a = "
                    + Integer.toHexString(ida.inLabel) + " b = "
                    + Integer.toHexString(idb.inLabel));
        if (ida.depth <= idb.depth)
            return ida.tn;
        return idb.tn;
    }
}

class OurNode {
    public int depth;

    public int dfsNum;

    public TreeNode tn;

    public int inLabel;

    public int trueAncestors;

    public OurNode firstChild;

    public OurNode next;

    public OurNode parent;
}
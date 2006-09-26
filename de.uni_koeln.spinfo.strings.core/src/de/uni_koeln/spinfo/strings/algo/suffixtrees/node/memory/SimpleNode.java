/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory;

import java.util.HashMap;
import java.util.Iterator;

import de.uni_koeln.spinfo.strings.algo.lca.TreeNode;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;

/**
 * Node Class for the suffix tree, implementing an Interface for {@link LCA}-queries
 * 
 * @author Francois Pepin (original version from BioJava API)
 * @author Fabian Steeg (fsteeg)
 */
public class SimpleNode extends SuffixNode implements TreeNode {

    private int dfs = 0;

    private int id = 0;

    private int suffixIndex;

    /**
     * Creates a root
     */
    public SimpleNode() {
        parent = null;
        suffixLink = null;
        labelStart = 0;
        labelEnd = 0;
        children = new HashMap<Long, Node>();
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
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SuffixNode#isTerminal()
     */
    public boolean isTerminal() {
        return children == null;
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SuffixNode#hasChild(java.lang.Character)
     */
    public boolean hasChild(Character x) {
        return getChild(x) != null;
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SuffixNode#getChild(java.lang.Character)
     */
    public Node getChild(Character x) {
        return (children == null) ? null : children.get(x);
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SuffixNode#getParent()
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

	public int getDfs() {
		return dfs;
	}
	
	public int getSuffixIndex() {
		return suffixIndex;
	}

	public Node getSuffixLink() {
		return this.suffixLink;
	}

	public void setId(int id) {
		this.id = id;
		
	}


}
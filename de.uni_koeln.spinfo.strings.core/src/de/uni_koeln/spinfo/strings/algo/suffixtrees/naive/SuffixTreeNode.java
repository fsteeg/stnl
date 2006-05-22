package de.uni_koeln.spinfo.strings.algo.suffixtrees.naive;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * Represents a node in a suffix tree. Contains a collection of nodes, the
 * immediate children of this node and thus forms a recursive data structure.
 * 
 * @author Fabian Steeg
 */
public class SuffixTreeNode {

	/**
	 * The incoming edge of this node. Every node in a tree has one incoming
	 * edge, except for the root, which has none.
	 */
	private SuffixTreeEdge incomingEdge = null;

	/**
	 * The depth of this node (not the string depth).
	 */
	private int nodeDepth = -1;

	/**
	 * The label of this node. Currently for leafs only.
	 */
	private int label = -1;

	/**
	 * The collection of nodes, the immediate children of this node.
	 */
	private Collection<SuffixTreeNode> children = null;

	/**
	 * The parent node of this node
	 */
	private SuffixTreeNode parent = null;

	/**
	 * The string depth of this node.
	 */
	private int stringDepth;

	private int id = 0;

	/**
	 * Constructor for a node with a parent, that is, for any node except the
	 * root node.
	 * 
	 * @param parent
	 *            The parent node of this node.
	 * @param incomingLabel
	 *            The label for the incoming edge of this node.
	 * @param depth
	 *            The node depth to be assigned to this node.
	 * @param label
	 *            The label for this node (Nodes are currently labeled with leaf
	 *            numbers).
	 */
	public SuffixTreeNode(SuffixTreeNode parent, String incomingLabel,
			int depth, int label, int id) {
		children = new Vector<SuffixTreeNode>();
		incomingEdge = new SuffixTreeEdge(incomingLabel, label);
		this.nodeDepth = depth;
		this.label = label;
		this.parent = parent;
		this.stringDepth = parent.getStringDepth() + incomingLabel.length();
		this.id = id;
	}

	/**
	 * Empty constructor, for the root node.
	 */
	public SuffixTreeNode() {
		children = new Vector<SuffixTreeNode>();
		nodeDepth = 0;
		this.label = 0;
	}

	/**
	 * @param suffix
	 *            The suffix to insert into the suffix tree, will be inserted
	 *            behind the maximum prefix of the suffix found in the tree.
	 * @param pathIndex
	 *            The path index for labeling the leaf at the end of the path of
	 *            the suffix added.
	 */
	public void addSuffix(List<String> suffix, int pathIndex) {
		SuffixTreeNode insertAt = this;
		// recursivly find the node to insert at:
		insertAt = search(this, suffix);
		// insert new nodes:
		insert(insertAt, suffix, pathIndex);
	}

	/**
	 * @param startNode
	 *            The node in which to start the search.
	 * @param suffix
	 *            The suffix that is intended to be inserted into the tree.
	 * @return Returns the node in which to enter the suffix, that is the node
	 *         in which the path to the maximum prefix of the new suffix ends.
	 */
	private SuffixTreeNode search(SuffixTreeNode startNode, List<String> suffix) {
		if (suffix.size() == 0) {
			/*
			 * this should never happen, as a terminating "$" is always appended
			 * to the text, but if that wasn't the case, and the text would be
			 * something like "dacdac", and right here we would return the
			 * startNode, we would have an invalid suffix tree, where one path
			 * would end in an inner node.
			 */
			throw new IllegalArgumentException(
					"Empty suffix. Probably no valid simple suffix tree exists for the input.");
		}
		Collection<SuffixTreeNode> children = startNode.children;
		for (SuffixTreeNode child : children) {
			// longer prefix exists in the tree, descend:
			if (child.incomingEdge.getLabel().equals(suffix.get(0))) {
				suffix.remove(0);
				if (suffix.isEmpty()) {
					return child;
				}
				return search(child, suffix);
			}
		}
		// maximum prefix found:
		return startNode;
	}

	/**
	 * 
	 * @param insertAt
	 *            The node into which the suffix should be entered.
	 * @param suffix
	 *            The suffix to enter into the node (not all will be used, see
	 *            inline comment).
	 * @param pathIndex
	 *            the path index (for labels of leafs).
	 */
	private void insert(SuffixTreeNode insertAt, List<String> suffix,
			int pathIndex) {
		// skip the maximum prefix that has already been found, for every
		// remaining char, enter a node
		for (int j = 0; j < suffix.size(); j++) {
			SuffixTreeNode child = new SuffixTreeNode(insertAt, suffix.get(j)
					+ "", insertAt.nodeDepth + 1, pathIndex, id);
			insertAt.addChild(child);
			insertAt = child;
		}
	}

	/**
	 * @param child
	 *            The node to add as a child of this node.
	 */
	private void addChild(SuffixTreeNode child) {
		children.add(child);
	}

	/**
	 * @return Return s string representation of this node and all the children,
	 *         that is the subtree starting inthis node.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		// indent to visualize node depth
		for (int i = 1; i <= this.nodeDepth; i++)
			result.append("\t");
		// create a label
		String incomingLabel = this.isRoot() ? "" : this.incomingEdge
				.getLabel();
		if (this.isRoot())
			result.append(" " + this.nodeDepth + ", root");
		else
			result.append("\\-" + incomingLabel + "-> " + this.stringDepth);
		if (this.isLeaf())
			result.append(", leaf " + this.label + "\n");
		result.append("\n");
		// and do the same recursivly for all children
		for (SuffixTreeNode child : children)
			result.append(child.toString());
		return result.toString();
	}

	/**
	 * Recursively exports the node and all children as dot
	 * 
	 * @return Returns the tree as a dot string (www.graphviz.org)
	 */
	public String toDotString() {
		StringBuilder result = new StringBuilder();
		String incomingLabel = this.isRoot() ? "" : this.incomingEdge
				.getLabel();
		if (this.isRoot()) {
			result.append("digraph {\n" + "rankdir=LR;\n");
			CompactSuffixTree.c = 1;
			this.id = 1;
		} else {
			this.id = CompactSuffixTree.c;
			result.append(this.parent.id + " -> ");
			result.append(this.id + "[label=\"" + incomingLabel + "\"];\n");
		}
		for (SuffixTreeNode child : children) {
			child.parent.id = this.id;
			CompactSuffixTree.c++;
			child.id = CompactSuffixTree.c;
			result.append(child.toDotString());
		}
		return result.toString();
	}

	/**
	 * @return Returns true if this node is the root node.
	 */
	public boolean isRoot() {
		// the root node has no parent
		return this.parent == null;
	}

	/**
	 * @return Returns true if this node is a leaf.
	 */
	public boolean isLeaf() {
		// a leaf node has no children
		return children.size() == 0;
	}

	/**
	 * @return Returns the number of nodes in the subtree starting in this node.
	 */
	public int getNodeCount() {
		// count one for this node
		int res = 1;
		// and the same recursivly for all children
		for (SuffixTreeNode child : children) {
			res = res + child.getNodeCount();
		}
		return res;
	}

	/**
	 * @return Returns the immediate children of this node.
	 */
	public Collection<SuffixTreeNode> getChildren() {
		return children;
	}

	/**
	 * @return Returns the parent node of this node.
	 */
	public SuffixTreeNode getParent() {
		return parent;
	}

	/**
	 * @param children
	 *            The new children of this node.
	 */
	public void setChildren(Collection<SuffixTreeNode> children) {
		this.children = children;
	}

	/**
	 * @return Returns the label of the incoming edge.
	 */
	public SuffixTreeEdge getIncomingEdge() {
		return incomingEdge;
	}

	/**
	 * @param incomingEdge
	 *            The new label for the incoming edge of this node.
	 */
	public void setIncoming(SuffixTreeEdge incomingEdge) {
		this.incomingEdge = incomingEdge;
	}

	/**
	 * @param parent
	 *            The new parent of this node.
	 */
	public void setParent(SuffixTreeNode parent) {
		this.parent = parent;

	}

	/**
	 * @return Returns the node depth of this node, that is, how many edges are
	 *         on a path from the root node to this node.
	 */
	public int getNodeDepth() {
		return nodeDepth;
	}

	/**
	 * @param depth
	 *            The new depth for this node.
	 */
	public void setNodeDepth(int depth) {
		this.nodeDepth = depth;

	}

	/**
	 * @return The string depth of this node.
	 */
	public int getStringDepth() {
		return stringDepth;
	}

	/**
	 * @param stringDepth
	 *            The new string depth for this node.
	 */
	public void setStringDepth(int stringDepth) {
		this.stringDepth = stringDepth;
	}

	/**
	 * @return Returns the number of nodes in the subtree starting in this node.
	 */
	public int getSize() {
		// count one for this node
		int res = 0;
		if (this.isRoot() == false)
			res = this.incomingEdge.getLabel().split(",").length;
		// and the same recursivly for all children
		for (SuffixTreeNode child : children) {
			res = res + child.getSize();
		}
		return res;
	}
}

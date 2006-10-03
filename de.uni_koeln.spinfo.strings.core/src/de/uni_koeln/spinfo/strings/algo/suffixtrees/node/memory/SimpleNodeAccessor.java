package de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory;

import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor;

public class SimpleNodeAccessor implements NodeAccessor {

	private int idCounter = 1;
	
	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#getParent(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
	 */
	public Node getParent(Node node) {
		return ((SimpleNode)node).getParent();
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#getSuffixLink(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
	 */
	public Node getSuffixLink(Node currentNode) {
		return ((SimpleNode)currentNode).getSuffixLink();
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#isTerminal(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
	 */
	public boolean isTerminal(Node currentNode) {
		return ((SimpleNode)currentNode).isTerminal();
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setSuffixLink(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
	 */
	public void setSuffixLink(Node from, Node to) {
		((SimpleNode)from).setSuffixLink(to);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setAdditionalLabels(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node, int[])
	 */
	public void setAdditionalLabels(Node leaf, int[] additionalLabels) {
		((SimpleNode)leaf).setAdditionalLabels(additionalLabels);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setParent(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node, de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
	 */
	public void setParent(Node child, Node parent) {
		((SimpleNode)child).setParent(parent);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setLabelEnd(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node, int)
	 */
	public void setLabelEnd(Node leaf, int e) {
		((SimpleNode)leaf).setLabelEnd(e);
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#createNode()
	 */
	public Node createNode() {
		SimpleNode toReturn = new SimpleNode();
		toReturn.setId(idCounter++);
		return toReturn;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#createNode(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node, int, int, int, int)
	 */
	public Node createNode(Node parent, int suffixStart, int splittingPos, int number, int suffixIndex) {
		SimpleNode toReturn = new SimpleNode(parent,suffixStart,splittingPos);
		toReturn.setId(idCounter++);
		return toReturn;
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#createNode(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node, int, int, int)
	 */
	public Node createNode(Node parent, int suffixStart, int number, int suffixIndex) {
		SimpleNode toReturn = new SimpleNode(parent,suffixStart,number,suffixIndex);
		toReturn.setId(idCounter++);
		return toReturn;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#addChild(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node, java.lang.Long, de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
	 */
	public void addChild(Node parent, Long x, Node middle) {
		parent.getChildren().put(x,middle);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setId(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node, int)
	 */
	public void setId(Node root, int count) {
		((SimpleNode)root).setId(count);
	}


	
}



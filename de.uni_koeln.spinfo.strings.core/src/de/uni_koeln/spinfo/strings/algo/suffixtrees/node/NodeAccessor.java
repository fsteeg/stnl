package de.uni_koeln.spinfo.strings.algo.suffixtrees.node;

import java.util.Map;

/**
 * 
 * @author sschwieb
 *
 */
public interface NodeAccessor {

	public Node getParent(Node node);

	public Node getSuffixLink(Node currentNode);

	public boolean isTerminal(Node currentNode);

	public void setSuffixLink(Node from, Node to);

	public void setAdditionalLabels(Node leaf, int[] additionalLabels);

	public void setParent(Node child, Node parent);

	public void setLabelEnd(Node leaf, int e);

	public Node createInternalNode(Node parent, int suffixStart, int splittingPos);

	public void addChild(Node parent, Long x, Node middle);

	public Node createLeafNode(Node parent, int suffixStart, int number,
			int suffixIndex);

	public void setId(Node root, int count);

	public Node createRootNode();

	public Map<Long, Node> getChildren(Node root);


}

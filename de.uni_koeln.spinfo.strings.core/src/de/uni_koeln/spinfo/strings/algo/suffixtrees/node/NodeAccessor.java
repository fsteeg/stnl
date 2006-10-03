package de.uni_koeln.spinfo.strings.algo.suffixtrees.node;

public interface NodeAccessor {

	public Node getParent(Node node);

	public Node getSuffixLink(Node currentNode);

	public boolean isTerminal(Node currentNode);

	public void setSuffixLink(Node from, Node to);

	public void setAdditionalLabels(Node leaf, int[] additionalLabels);

	public void setParent(Node child, Node parent);

	public void setLabelEnd(Node leaf, int e);

	public Node createNode(Node parent, int suffixStart, int splittingPos,
			int number, int suffixIndex);

	public void addChild(Node parent, Long x, Node middle);

	public Node createNode(Node parent, int suffixStart, int number,
			int suffixIndex);

	public void setId(Node root, int count);

	public Node createNode();


}

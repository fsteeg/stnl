package de.uni_koeln.spinfo.strings.algo.suffixtrees.node;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author sschwieb
 *
 */
public interface NodeAccessor {

	public List<Node> getParents(Node node);

	public Node getSuffixLink(Node currentNode);

	public boolean isTerminal(Node currentNode);

	public void setSuffixLink(Node from, Node to);

	public void setAdditionalLabels(Node leaf, int[] additionalLabels);

	public void setParents(Node child, List<Node> parent);

	public void setLabelEnd(Node leaf, int e);

	public Node createInternalNode(List<Node> parent, int suffixStart, int splittingPos);

	/**
	 * Adds Node child to the children of Node parent. It is referenced by Long ref.
	 * The modified parent is returned.
	 * @param parent
	 * @param ref
	 * @param child
	 * @return Modified parent
	 */
	public void addChild(Node parent, Long ref, Node child);

	public Node createLeafNode(List<Node> parent, int suffixStart, int number,
			int suffixIndex);

	public void setId(Node root, int count);

	public Node createRootNode();
	
	public Node getRoot();

	public Map<Long, Node> getChildren(Node root);


}

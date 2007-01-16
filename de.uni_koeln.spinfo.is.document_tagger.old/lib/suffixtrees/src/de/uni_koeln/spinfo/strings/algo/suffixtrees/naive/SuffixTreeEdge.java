/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees.naive;

/**
 * Class representing an edge in a suffix tree.
 * 
 * @author Fabian Steeg
 * 
 */
public class SuffixTreeEdge {

	/**
	 * The label of this edge.
	 */
	private String label = null;

	/**
	 * The index where the branch this label belongs to starts in the text
	 * represented by the tree. That is the number that the leaf at the end of
	 * this branch will be labeled with (the index in the immediate children of
	 * the tree's root node).
	 */
	private int branchIndex = -1;

	/**
	 * @param label
	 *            The label for this edge.
	 * @param branchIndex
	 *            The index where the branch this label belongs to starts in the
	 *            text represented by the tree.
	 */
	public SuffixTreeEdge(String label, int branchIndex) {
		this.label = label;
		this.branchIndex = branchIndex;
	}

	/**
	 * @return Returns the label of this edge.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return Returns the index where the branch this label belongs to starts
	 *         in the text represented by the tree.
	 */
	public int getBranchIndex() {
		return branchIndex;
	}

	/**
	 * @param label
	 *            The new label for this edge.
	 */
	public void setLabel(String label) {
		this.label = label;

	}

	/**
	 * Creates a compact version of the label, if its length is
	 * >=2*ln(text.length-alphabet.size).
	 * 
	 * @param alphabetSize
	 *            The size of the input alphabet.
	 * @param textSize
	 *            The length of the text represented by the tree.
	 * @param stringDepth
	 *            The string depth of the parent node.
	 */
	public void makeLabelCompact(int alphabetSize, int textSize, int stringDepth) {
		/*
		 * the indices for start and end in the label are 1-based. for usage
		 * with substring() use string.substring(start-1,end). That is start
		 * index (zero based) and end index (zero based) + 1, as substring(...)
		 * demands.
		 */
		int startOneBased = branchIndex + stringDepth;
		int endOneBased = startOneBased + label.length() - 2;
		if (label.length() >= 2 * Math.log(textSize - alphabetSize))
			label = "[" + startOneBased + ".." + endOneBased + "]";
		else
			label = "'" + label + "'";
	}
}

/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor;

/**
 * <p>
 * A suffix tree is an efficient method for encoding the frequencies of motifs
 * in a sequence. They are sometimes used to quickly screen for similar
 * sequences. For instance, all motifs of length up to 2 in the sequence
 * <code>AAGT</code> could be encoded as:
 * </p>
 * 
 * <pre>
 *                   root(4)
 *                   |
 *                   A(2)--------G(1)-----T(1)
 *                   |           |
 *                   A(1)--G(1)  T(1)
 * </pre>
 * 
 * <p>
 * It supports addition of elements both as String and SymbolList. They should
 * not be mixed together. The strings are also terminated internally, so it is
 * possible to go and add more than one string to the suffix tree.
 * </p>
 * <p/> Some more work need to be done on how data should be generated from this
 * class. If you need something that's not in there, please e-mail the list at
 * biojava-dev@biojava.org and I'll add it in there. <p/> <b>Note:</b> (Fabian
 * Steeg) BioJava API-Code has been removed from the Implementation. The stuff
 * from BioJava might be interesting for modding this to allow larger alphabets
 * than 65535 through IntegerAlphabet. But also modding the String sto int[]
 * might work here (while it didn't for the ottawa-impl, that went quadratic)
 * 
 * @author Francois Pepin (original version from BioJava API), fsteeg (switched
 *         to longs)
 */
public class UkkonenSuffixTree {

    public static final Long DEFAULT_TERM_CHAR = -1L;
    
    public static final int TO_A_LEAF = -1;

    private Long terminationChar;

   // private Node root;

    private int e;

    private SequenceAccessor sequences;
    
//    private List<Long> sequences;
    
    protected NodeAccessor accessor;

    /**
     * Describes the rule that needs to be applied after walking down a tree.
     * Put as a class variable because it can only return a single object (and I
     * don't want to extend Node any further. rule 1: ended up at a leaf. rule
     * 2: need to extend an internalNode. rule 3: would split an edge. rule 4:
     * ended up in the middle of an edge. rule 5: ended up at an InternalNode
     * 
     * Production 5 counts as rule 4 when adding a sequence, but the rules are
     * also used to when searching the tree.
     */
    private int rule;

    /**
     * Initializes a new <code>UkkonenSuffixTree</code> instance with the given accessors.
     */    
    protected void initialize(NodeAccessor nodeAccessor, SequenceAccessor sequenceAccessor) {
    	this.accessor = nodeAccessor;
    	this.sequences = sequenceAccessor;
    	terminationChar = DEFAULT_TERM_CHAR;
        accessor.createRootNode();
        e = 0;
    }

    /**
     * Initializes a new <code>UkkonenSuffixTree</code> instance with a text and the given accessors.
     * 
     * @param seqs
     *            The text to add
     */
    public UkkonenSuffixTree(List<Long> seqs, NodeAccessor accessor, SequenceAccessor sequenceAccessor) {
        this(accessor, sequenceAccessor);
        addSequences(seqs, 1, false);
    }
    
    /**
     * Empty Constructor that does not do anything. 
     * NOTE: You <b>must</b> call <code>initialize</code>
     * when using this Constructor!
     */
    public UkkonenSuffixTree() {
    	
    }
    
    /**
     * Initializes a new <code>UkkonenSuffixTree</code> instance with the given accessors.
     * 
     * @param seqs
     *            The text to add
     */
    public UkkonenSuffixTree(NodeAccessor accessor, SequenceAccessor sequenceAccessor) {
        initialize(accessor, sequenceAccessor);
    }

    /**
     * Add a sequence into the tree. If there are more sequences, they should be
     * separated by a terminationChar ($ by default). If none exist, it is
     * assumed that there is only 1 continuous sequence to be added.
     * 
     * @param seq
     *            the sequence/sequences to be added into the tree.
     * @param number
     *            The text number, for generalized trees
     * @param doNotTerminate
     *            whether we should terminate the sequence if it's
     *            non-terminated.
     */
    public void addSequences(List<Long> seq, int number, boolean doNotTerminate) {
        int i;
        int start, end;
        ArrayList<List<Long>> toBeAdded = new ArrayList<List<Long>>();
        Iterator iterator;
        List<Long> subseq;

        if (seq == null || seq.size() == 0)
            return;

        // terminate the String if it's not terminated.
        if (!doNotTerminate && seq.get(seq.size() - 1) != terminationChar)
            seq.add(terminationChar);

        // count how many termination Chars in in.
        start = 0;
        for (i = 0; seq.subList(i, seq.size()).indexOf(terminationChar) != -1; i = seq
                .subList(i, seq.size()).indexOf(terminationChar) + 1) {
            end = seq.subList(i, seq.size()).indexOf(terminationChar);
            toBeAdded.add(seq.subList(start, end + 1));
        }

        iterator = toBeAdded.iterator();
        i = 0;
        while (iterator.hasNext()) {
            subseq = (List<Long>) iterator.next();
            addSingleSequence(subseq, number);
            i++;
        }
    }

    /**
     * Add a single sequence into the tree.
     * 
     * @param seq
     *            a <code>String</code> value
     * @param number
     */
    protected void addSingleSequence(List<Long> seq, int number) {
        int i, gammaStart;
        int j = 0;
        Node oldNode = null, newNode;
        Node currentNode;
        boolean canLinkJump = false;

        // Puts i at the end of the previous sequences
        i = sequences.size();
        int k = i;
        j = i;
        sequences.addAll(seq);

        currentNode = accessor.getRoot();

        // phase i
        for (; i < sequences.size(); i++) {
            // System.out.println("Phase "+i);

            e += 1;
            // extension j;
            for (; j <= i; j++) {
                // System.out.println("extension "+j);

                // reset a couple of things...
                newNode = null;

                // find first node v at or above s[j-1,i] that is root or has a
                // suffixLink
                while (!currentNode.equals(accessor.getRoot()) && accessor.getSuffixLink(currentNode) == null
                        && canLinkJump)
                    currentNode = accessor.getParent(currentNode);

                if (currentNode.equals(accessor.getRoot())) {
                	System.out.println("Jumping from root" + j + ", " + (i + 1));
                    currentNode = jumpTo(accessor.getRoot(), j, i + 1);
                } else {
                    if (canLinkJump)
                        currentNode = accessor.getSuffixLink(currentNode);
                    gammaStart = j + getPathLength(currentNode);

                    currentNode = jumpTo(currentNode, gammaStart,
                            i + 1);
                }

                if (rule == 1)
                    addPositionToLeaf(j, currentNode);
                if (rule == 2)
                    doRule2(currentNode, i, j, number, i - k);
                if (rule == 3) {
                    newNode = doRule3(currentNode, i, j, number, i - k);
                    currentNode = newNode;

                }

                if (rule == 1 || rule == 4 || rule == 5)
                    currentNode = accessor.getParent(currentNode);

                if (oldNode != null) {
                    if (accessor.isTerminal(currentNode))
                        currentNode = accessor.getParent(currentNode);
                    accessor.setSuffixLink(oldNode,currentNode);

                }
                oldNode = newNode;
                newNode = null;

                if (rule == 1 || rule == 4 || rule == 5) {
                    oldNode = null;
                    canLinkJump = false;
                    break;
                } else
                    canLinkJump = true;
            }// for phase i
        }// for extension j
        finishAddition();
    }

    /**
     * This method is used to walk down the tree, from a given node. The
     * <code>rule</code> variable can be used to check where the walk stopped.
     * Note that rule 3 means that the string used to walk down the tree does
     * not match (which is a bit different from the construction where rule 3
     * implies that only the last character doesn't match.
     * <p>
     * The String is encoded as a substring of a given source. This is done to
     * avoid replicating the string. To send walk down the string <code>x</code>
     * from the root, one would call walkTo(root,x,0,x.length()).
     * 
     * @param starting
     *            the root of the subtree we're walking down form.
     * @param source
     *            a superstring that contains the string we're using to walking
     *            down. source.subtring(from,to) should give the string we're
     *            walking down from.
     * @param from
     *            the start position (inclusive) of the target string in the
     *            source.
     * @param to
     *            the end position (exclusive) of the target string in the node.
     * @return a <code>SuffixNode</code> that the walk stopped at. If the walk
     *         stopped inside an edge. (check the rule variable to see where it
     *         stopped).
     */
    // public SuffixNode walkTo(SuffixNode starting, String source, int from,
    // int to) {
    // SuffixNode currentNode;
    // SuffixNode arrivedAt;
    // CharSequence edgeLabel;
    //
    // currentNode = starting;
    // arrivedAt = starting;
    // while (from < to) {
    // arrivedAt = (SuffixNode) currentNode.getChildren().get(
    // new Character(source.charAt(from)));
    // if (arrivedAt == null) {
    // from = to;
    // arrivedAt = currentNode;
    // rule = 2;
    // break;
    // }
    //
    // edgeLabel = getEdgeLabel(arrivedAt);
    // if (edgeLabel.length() >= to - from) {
    // if (edgeLabel.equals(source.substring(from, to))) {
    // // rule 1 or 5,
    // if (arrivedAt.isTerminal())
    // rule = 1;
    // else
    // rule = 5;
    // }
    // if (edgeLabel.subSequence(0, to - from).equals(
    // source.substring(from, to)))
    // rule = 4;
    // else
    // rule = 3;
    // from = to;
    // } else if (source.subSequence(from, from + edgeLabel.length())
    // .equals(edgeLabel)) {
    // from += edgeLabel.length();
    // currentNode = arrivedAt;
    // }
    //
    // else {
    // rule = 3;
    // from = to;
    // }
    // }
    //
    // return arrivedAt;
    //
    // }
    /**
     * Just like walkTo, but faster when used during tree construction, as it
     * assumes that a mismatch can only occurs with the last character of the
     * target string.
     * 
     * @param starting
     *            the root of the subtree we're walking down form.
     * @param source
     *            a superstring that contains the string we're using to walking
     *            down. source.subtring(from,to) should give the string we're
     *            walking down from.
     * @param from
     *            the start position (inclusive) of the target string in the
     *            source.
     * @param to
     *            the end position (exclusive) of the target string in the node.
     * @return a <code>SuffixNode</code> that the walk stopped at. If the walk
     *         stopped inside an edge. (check the rule variable to see where it
     *         stopped).
     */
    public Node jumpTo(Node starting, int from,
            int to) {
    	Node currentNode;
    	Node arrivedAt;
        boolean canGoDown = true;
        int edgeLength;
        int original = from;
        Node originalNode = starting;
        // int i = 0;

        currentNode = starting;
        arrivedAt = starting;

        rule = 0;

        if (from == to) {
            rule = 5;
            return starting;
        }
        while (canGoDown) {
            // if (source.substring(from, to).equals("CAGCG"))
            // System.out.println(to+" here to "+source.substring(from, to)+"
            // "+(i++));

            if (accessor.isTerminal(currentNode)) {
                System.out.println("ARRGH! at " + sequences.subList(original, to)
                        + "(" + from + "," + original + "," + to + ") from "
                        + getLabel(originalNode));
                // Something truly awful happened if this line is ever reached.
                // This bug should be dead, but it it came back from the dead a
                // couple
                // of times already.
            }
            Long key = sequences.get(from);
            arrivedAt = (Node) accessor.getChildren(currentNode).get(key);
            if (arrivedAt == null) {
                canGoDown = false;
                arrivedAt = currentNode;
                
                rule = 2;
                break;
            }

            edgeLength = getEdgeLength(arrivedAt);
            if (edgeLength >= to - from) {
                // int before = currentNode.labelEnd + to - from + 1;
//                int after = getPathEnd(arrivedAt) - getEdgeLength(arrivedAt)
//                        + to - from - 1;
            	int after = getPathEnd(arrivedAt) - edgeLength + to - from - 1;
                if (sequences.get(after).equals(sequences.get(to - 1))) {
                    //if (getEdgeLength(arrivedAt) == to - from) {
                	if (edgeLength == to - from) {
                        if (accessor.isTerminal(arrivedAt))
                            rule = 1;
                        else
                            rule = 5;
                    } else
                        rule = 4;
                } else
                    rule = 3;
                canGoDown = false;
                break;
            }
            from += edgeLength;
            currentNode = arrivedAt;

        }// while canGoDOwn

        return arrivedAt;
    }

    /***************************************************************************
     * Tree navigation methods
     **************************************************************************/

    public int getEdgeLength(Node child) {
        int parentLength, childLength;
        Node parent;
        if (child.equals(accessor.getRoot()))
            return 0;
        parent = accessor.getParent(child);
        parentLength = getPathLength(parent);
        childLength = getPathLength(child);
        if (childLength - parentLength <= 0) {

            System.out.println("negative length "
                    + (childLength - parentLength));

            System.out.println(getLabel(child) + "," + getLabel(parent));
        }

        return childLength - parentLength;
    }

    public List<Long> getEdgeLabel(Node child) {
        return sequences.subList(child.getLabelStart()
                + (getPathLength(child) - getEdgeLength(child)),
                (child.getLabelEnd() == TO_A_LEAF) ? e : child.getLabelEnd());
    }

    public int getPathLength(Node node) {
        return getPathEnd(node) - node.getLabelStart();
    }

    public int getPathEnd(Node node) {
        return node.getLabelEnd() == TO_A_LEAF ? e : node.getLabelEnd();
    }

    public List<Long> getLabel(Node node) {
        if (node.equals(accessor.getRoot()))
            return new Vector<Long>();
        else
            return sequences.subList(node.getLabelStart(),
                    (node.getLabelEnd() == TO_A_LEAF) ? e : node.getLabelEnd());
    }

    public ArrayList<Node> getAllNodes(Node root,
            ArrayList<Node> list, boolean leavesOnly) {
        Iterator iterator;
        if (list == null)
            list = new ArrayList<Node>();
        if (!leavesOnly || (leavesOnly && accessor.isTerminal(root)))
            list.add(root);
        if (!accessor.isTerminal(root)) {
            iterator = accessor.getChildren(root).values().iterator();
            while (iterator.hasNext())
                list = getAllNodes((Node) iterator.next(), list,
                        leavesOnly);
        }

        return list;
    }

    // public void printTree() {
    // ArrayList allNodes = getAllNodes(root, null, false);
    // StringBuilder builder = new StringBuilder();
    // for (int i = 0; i < allNodes.size(); i++) {
    // SuffixNode node = (SuffixNode) allNodes.get(i);
    // if (node == root)
    // System.out.println("root");
    // else {
    // CharSequence thisLabel = getLabel(node);
    // CharSequence parentLabel = getLabel(node.parent);
    // builder.append("node " + i + " label: \t" + thisLabel
    // + " attached to: \t" + parentLabel + "\n");
    // }
    // }
    // System.out.println(builder.toString());
    // }

    public Node getRoot() {
        return accessor.getRoot();
    }

    /***************************************************************************
     * End Tree Navigation Methods
     **************************************************************************/

    /***************************************************************************
     * Tree modification methods
     **************************************************************************/
    private void addPositionToLeaf(int pos, Node leaf) {
        int[] moreLabels;
        int[] additionalLabels = leaf.getAdditionalLabels();
        if (additionalLabels == null)
            additionalLabels = new int[] { pos };
        else {
            moreLabels = new int[additionalLabels.length + 1];
            System.arraycopy(additionalLabels, 0, moreLabels, 0,
                    additionalLabels.length);
            moreLabels[moreLabels.length - 1] = pos;
            additionalLabels = moreLabels;
        }
        accessor.setAdditionalLabels(leaf,additionalLabels);
    }

    private void doRule2(Node parent, int splittingPos, int suffixStart,
            int number, int suffixIndex) {
        // int number = getAllNodes(root, null, false).size();
        Node leaf = accessor.createLeafNode(parent, suffixStart, number, suffixIndex);
        Long splitPos = sequences.get(splittingPos);
        accessor.addChild(parent, splitPos, leaf);
//        parent.getChildren().put(new Long(sequences.get(splittingPos)), leaf);
        // System.out.println("rule 2: "+sequences.charAt(splittingPos)+" from
        // "+getLabel(parent)+ " to "+getLabel(leaf));

    }

    private Node doRule3(Node child, int splittingPos,
            int suffixStart, int number, int suffixIndex) {
        // return toBeSplit.splitEdge(endOfSubSeq,
        // sequences.charAt(endOfSubSeq),
        // toBeSplit.getStart()+endOfSubSeq-rule3Position,
        // suffixStart);
        // int number = getAllNodes(root, null, false).size();
        Node parent = accessor.getParent(child);
        Node middle = accessor.createInternalNode(parent,suffixStart,splittingPos);
//        SuffixNode middle = new SimpleNode(parent, suffixStart, splittingPos,
//                number, suffixIndex);
        int edgeLength = getEdgeLength(child);
        long x = sequences.get(child.getLabelStart() + getPathLength(child)
                - edgeLength);

        // System.out.println(parent.children.get(x)==child);

        long y = sequences.get(child.getLabelStart() + getPathLength(child)
                - edgeLength + getEdgeLength(middle));
        
        
//        parent.getChildren().remove(x);
//        parent.getChildren().put(x, middle);
        accessor.addChild(parent,x,middle);
        accessor.addChild(middle, y, child);
//        middle.getChildren().put(y, child);
        //child.parent = middle;
        accessor.setParent(child,middle);
        // System.out.println("rule 3: "+sequences.charAt(splittingPos)+"
        // between "+getLabel(parent)+" and "+getLabel(child) + " Addition
        // made:"+(number==getAllNodes(root, null,false).size()-1));
        doRule2(middle, splittingPos, suffixStart, number, suffixIndex);
        return middle;
    }

    private void finishAddition() {
        Node leaf;
        ArrayList leaves = getAllNodes(accessor.getRoot(), null, true);
        for (int i = 0; i < leaves.size(); i++) {
            leaf = (Node) leaves.get(i);
            if (leaf.getLabelEnd() == TO_A_LEAF)
            	accessor.setLabelEnd(leaf,e);
        }

    }
    
    public void printTree() {
        ArrayList allNodes = getAllNodes(accessor.getRoot(), null, false);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < allNodes.size(); i++) {
            Node node = (Node) allNodes.get(i);
            if (node.equals(accessor.getRoot()))
                System.out.println("root");
            else {
                List<Long> thisLabel = getLabel(node);
                List<Long> parentLabel = getLabel(accessor.getParent(node));
                builder.append("NodeImpl " + i + " label: \t" + thisLabel
                        + " attached to: \t" + parentLabel + "\n");
            }
        }
        System.out.println(builder.toString());
    }
    

    /***************************************************************************
     * end Tree modification methods
     **************************************************************************/

    /**
     * This is simply a debugging method to check that a node was created
     * normally. it doesn't return anything but prints to System.err if a bad
     * addition was made.
     * 
     * @param child
     *            a <code>SuffixNode</code> value
     */
    // private void checkParent(SuffixNode child) {
    // SuffixNode parent = child.parent;
    // CharSequence parentLabel = getLabel(parent);
    // CharSequence label = getLabel(child);
    //
    // if (parentLabel.equals("root"))
    // parentLabel = "";
    //
    // if (parentLabel.length() >= label.length()
    // || !parentLabel.equals(label.subSequence(0, parentLabel
    // .length()))) {
    // System.err.println("bad addition on rule " + rule);
    // System.err.println(parentLabel + " against " + label);
    // System.err.println("child (" + child.labelStart + ","
    // + ((child.labelEnd == -1) ? e : child.labelEnd) + ")");
    // System.err.println("parent (" + parent.labelStart + ","
    // + parent.labelEnd + ")");
    // }
    // }
    // public boolean subStringExists(String str) {
    // walkTo(root, str, 0, str.length());
    // return (rule == 1 || rule == 4 || rule == 5);
    // }
}

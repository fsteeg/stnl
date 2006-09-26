/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.LCE;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SimpleNodeAccessor;

/**
 * Implementation based on the linear-time algorithm for constructing word-based
 * suffix trees as described by Andersson, Larsson & Swansson in Algorithmica
 * 23, 1999
 * 
 * <p/>
 * 
 * Subclasses a modified {@link UkkonenSuffixTree} class from BioJava API,
 * builds a word based tree with symbols representing the words which are stored
 * separately in a map
 * 
 * <p/>
 * 
 * TODO implement LCE over LCA (then kmismatch an wildcards are running in
 * linear time), see {@link LCE}
 * 
 * <p/>
 * 
 * TODO implement adding of texts atfer instantiation for more flexibility
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class WordSuffixTree extends UkkonenSuffixTree {

    private String text;

    private boolean reverse;

    private boolean generalized;

    public Mapper mapper;
    
    // private int last = 1;

    /**
     * @param text
     *            The text to add to the tree, termination is handeled
     *            internally
     * @param reverse
     *            If true a tree is built for the reveresed word order
     * @param generalized
     *            If true the text is splittet into sentences, which are
     *            inserted separately (<b>Attention</b>: In the current
     *            Implementation, for generalized trees the construction runtime
     *            seems to grow quadratic)
     */
    public WordSuffixTree(String text, boolean reverse, boolean generalized, NodeAccessor accessor) {
        super(accessor);
    	this.reverse = reverse;
        this.generalized = generalized;
        setText(text);
    }

    /**
     * Sets the text and constructs the tree. TODO implement adding of multiple
     * texts from outside (public API)
     * 
     * @param text
     *            The text to add, termination is handled internally
     * 
     */
    private void setText(String text) {
        this.text = text;
        construct();
    }

    /**
     * 
     */
    private void construct() {

        /**
         * steps 1: construct a trie for the types in the input (ok, i'm using a
         * map but its nice and fast):
         */
        long counter = 0;
        Map<String, Long> trie = new HashMap<String, Long>();
        mapper = new Mapper(this, accessor);
        String[] sentences = text.split("[\\.!?;:]");
        Set<String> sentencesSet = new HashSet<String>();
        // if not generalized, we add the text as one
        if (!generalized)
            sentencesSet.add(text);
        // else we'll split it into sentences and later add these separately
        // (FIXME this currently give runtime problems, turn quadratic)
        else {
            for (String s : sentences) {
                if (!s.equals(" "))
                    sentencesSet.add(s.trim());
            }
        }
        /** step 2: number those types */
        System.out.print(sentencesSet.size() + " Sentences, ");
        int sentenceCount = 1;
        for (String sentence : sentencesSet) {
            // split each sentence into words
            String[] tokens = sentence.split("[^a-zA-Z0-9]");
            if (reverse) {
                List l = Arrays.asList(tokens);
                String[] tokRev = new String[tokens.length];
                int k = tokRev.length - 1;
                for (Object object : l) {
                    tokRev[k] = (String) object;
                    k--;
                }
                tokens = tokRev;
            }
            List<Long> builder = new ArrayList<Long>();
            for (String word : tokens) {
                if (!trie.containsKey(word)) {
                    // TODO attention, here we skip a $ in the weird-chars
                    // string, which would cause the tree to think it should
                    // terminate:
                    if (((char) counter) == '$')
                        counter++;
                    // map a word to a number:
                    trie.put(word, counter);
                    // TODO is there a better way do achieve this:
                    // map the number to the word:
                    mapper.put(counter, word);
                    counter++;
                    // TODO ideas: modding ukkonensuffixtree to use int[], or
                    // using IntegerAlphabet and BioJava:
                    if (counter > 65535)
                        throw new UnsupportedOperationException(
                                "Current implementation does not allow for Texts with a number of Types above 65535.");
                }
            }
            /**
             * step 3: assign the values from the trie to the words in the input
             * string:
             */
            long[] ids = new long[tokens.length];

            for (int i = 0; i < ids.length; i++) {
                String rec = tokens[i];
                Object val = trie.get(rec);
                ids[i] = (Long) val;
                builder.add(ids[i]);
            }

            /**
             * step 4: build a traditional suffix tree for the string of
             * numbers:
             */
            super.addSequence(builder, sentenceCount, false);
            sentenceCount++;
        }
        System.out.print(counter + " Types, ");

        /**
         * step 5: expand the tree for words: not present, the tree takes care
         * of the translation on request, using the map
         */

        // TODO fix quadratic runtime for generalized suffix tree, see gusfield,
        // page 116
    }

    /**
     * Minimal run of WordSuffixTree. For further tests see
     * {@link TestWordSuffixTree}.
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {
        String text = "Hallo Welt Hallo Rest";
        new WordSuffixTree(text, false, true, new SimpleNodeAccessor());
        System.out.println("Done.");
    }

    /**
     * <b>Experimental</b>
     * 
     * @param textNo
     *            The text number to get the suffix from
     * @param suffixIndex
     *            The index at which the suffix starts
     * @return The leaf representing the suffix starting at suffixIndex in
     *         textNo
     */
    public Node getNodeForSuffix(int textNo, int suffixIndex) {
        ArrayList<Node> leafs = this.getAllNodes(getRoot(), null, true);
        for (int i = 0; i < leafs.size(); i++) {
            Node node = (Node) leafs.get(i);
            if (node.getTextNumber() == textNo && node.getSuffixIndex() == suffixIndex)
                return node;
        }
        return null;
    }

    /**
     * <b>Experimental</b>
     * 
     * @param id
     *            The id of the node to get
     * @return The Node with id e
     */
    public Node getNodeForId(int id) {
        ArrayList<Node> leafs = this.getAllNodes(getRoot(), null, false);
        for (int i = 0; i < leafs.size(); i++) {
            Node node = (Node) leafs.get(i);
            if (node.getDfs() == id)
                return node;
        }
        return null;
    }

}

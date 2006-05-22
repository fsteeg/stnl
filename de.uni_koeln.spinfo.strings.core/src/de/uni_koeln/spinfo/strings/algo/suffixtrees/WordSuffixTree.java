/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo.suffixtrees;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import de.uni_koeln.spinfo.strings.algo.LCE;

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

    private Map<String, String> map;

    private boolean reverse;

    private boolean generalized;

    // these are for dot output
    private int count = 1;

//    private int last = 1;

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
     * @param pm
     *            An IProgressMonitor to show progress within eclipse, pass
     *            "null" if none is used
     */
    public WordSuffixTree(String text, boolean reverse, boolean generalized,
            IProgressMonitor pm) {
        this.reverse = reverse;
        this.generalized = generalized;
        setText(text, pm);
    }

    /**
     * Sets the text and constructs the tree. TODO implement adding of multiple
     * texts from outside (public API)
     * 
     * @param text
     *            The text to add, termination is handled internally
     * @param pm
     *            An IProgressMonitor to show progress within eclipse, pass
     *            "null" if none is used
     */
    private void setText(String text, IProgressMonitor pm) {
        this.text = text;
        construct(pm);
    }

    /**
     * @param pm
     *            An IProgressMonitor to show progress within eclipse, pass
     *            "null" if none is used
     */
    private void construct(IProgressMonitor pm) {
        if (pm != null)
            pm.beginTask("Konstruiere Suffixbaum...", this.text.length());
        /**
         * steps 1: construct a trie for the types in the input (ok, i'm using a
         * map but its nice and fast):
         */
        int counter = 0;
        Map<String, Integer> trie = new HashMap<String, Integer>();
        map = new HashMap<String, String>();
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
            StringBuilder builder = new StringBuilder();
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
                    map.put(counter + "", word);
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
            int[] ids = new int[tokens.length];

            for (int i = 0; i < ids.length; i++) {
                String rec = tokens[i];
                if (pm != null)
                    pm.worked(rec.length());
                Object val = trie.get(rec);
                ids[i] = (Integer) val;
                builder.append((char) ids[i]);
            }

            /**
             * step 4: build a traditional suffix tree for the string of
             * numbers:
             */
            addSequence(builder.toString(), sentenceCount, false);
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
        new WordSuffixTree(text, false, true, null);
        System.out.println("Done.");
    }

    /**
     * Translates from the char-based internal representation to the actual word
     * based text
     * 
     * @param map
     *            The mapping of the symbols to the words
     * @param label
     *            The label to translate
     * @param cut
     *            If true the result is cut after 10 words
     * @return Returns the actual label, containing blank-separated words
     */
    private String translate(Map<String, String> map, CharSequence label,
            boolean cut) {
        String res = "";
        int orig = label.length();
        if (!label.equals("root")) {
            if (cut)
                label = label.toString().substring(0,
                        Math.min(10, label.length()));
            char[] l = label.toString().toCharArray();
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < l.length; j++) {
                String string = map.get((int) l[j] + "");
                if (string == null)
                    string = "$";
                builder.append(string + " ");
            }
            res = builder.toString().trim();
        }
        if (cut && orig > 10)
            res += " [...]";
        return res;

    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.UkkonenSuffixTree#getEdgeLabel(de.uni_koeln.spinfo.strings.algo.suffixtrees.SuffixNode)
     */
    @Override
    public CharSequence getEdgeLabel(SuffixNode child) {
        return translate(map, super.getEdgeLabel(child), true);
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.UkkonenSuffixTree#getLabel(de.uni_koeln.spinfo.strings.algo.suffixtrees.SuffixNode)
     */
    @Override
    public CharSequence getLabel(SuffixNode node) {
        return translate(map, super.getLabel(node), true);
    }

    /**
     * Writes the tree as a dot text file to disk
     * 
     * <p/> TODO get this out of here, and into a class DotUtils, using a thin
     * interface like SuffixNode
     * 
     * @param root
     *            The root {@link SuffixNode} to export
     * @param dest
     *            The location in the file system to write to (eg. "out.dot")
     */
    public void exportDot(String dest) {
        try {
            String string = dest;
            if (reverse)
                string = "reverse-" + dest;
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File(string)));
            fileWriter
                    .write("/* this is a generated dot file: www.graphviz.org */\n"
                            + "digraph suffixtree {\n" + "\trankdir=LR;\n");
            printDotBody(root, null, false, fileWriter, 0);
            fileWriter.write("}");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<SuffixNode> printDotBody(SuffixNode root,
            ArrayList<SuffixNode> list, boolean leavesOnly, BufferedWriter writer,
            int depth) throws IOException {
        tab(writer, depth);
        root.id = count;
        if (root.parent != null)
            writer.write(root.parent.id + "->");
        writer.write("" + count);
        writer.write("[label=\"" + getEdgeLabel(root).toString().trim()
                + ", Text: " + root.textNumber + ", Suffix: "
                + ((SimpleNode) root).suffixIndex + "\"];\n");
        Iterator iterator;
        if (list == null) {
            list = new ArrayList<SuffixNode>();
            count = 1;
        }
        if (!leavesOnly || (leavesOnly && root.isTerminal()))
            list.add(root);
        if (!root.isTerminal()) {
            iterator = root.getChildren().values().iterator();
            // writer.write("\n");
            depth = depth + 1;
//            last = count;
            while (iterator.hasNext()) {
                SuffixNode next = (SuffixNode) iterator.next();
                count++;
                list = printDotBody(next, list, leavesOnly, writer, depth);
            }
        }
        return list;
    }

    /**
     * @param writer
     *            The writer to write tabs to
     * @param depth
     *            The current depth in the tree
     * @throws IOException
     *             If writing goes wrong
     */
    private void tab(BufferedWriter writer, int depth) throws IOException {
        for (int i = 0; i <= depth; i++) {
            writer.write("\t");
        }
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
    public SimpleNode getNodeForSuffix(int textNo, int suffixIndex) {
        ArrayList<SuffixNode> leafs = this.getAllNodes(getRoot(), null, true);
        for (int i = 0; i < leafs.size(); i++) {
            SimpleNode node = (SimpleNode) leafs.get(i);
            if (node.textNumber == textNo && node.suffixIndex == suffixIndex)
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
    public SimpleNode getNodeForId(int id) {
        ArrayList<SuffixNode> leafs = this.getAllNodes(getRoot(), null, false);
        for (int i = 0; i < leafs.size(); i++) {
            SimpleNode node = (SimpleNode) leafs.get(i);
            if (node.dfs == id)
                return node;
        }
        return null;
    }

}

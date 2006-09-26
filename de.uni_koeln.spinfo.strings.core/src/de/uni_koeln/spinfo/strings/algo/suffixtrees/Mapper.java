package de.uni_koeln.spinfo.strings.algo.suffixtrees;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Mapping of symbols in texts to symbols in the tree. Also provides the dot-output.
 * 
 * @author fsteeg
 * 
 */
public class Mapper {
    private Map<Long, String> map;

    // these are for dot output
    private int count = 1;

    private UkkonenSuffixTree tree;

    public Mapper(UkkonenSuffixTree tree) {
        this.tree = tree;
        map = new HashMap<Long, String>();
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
    public String translate(Map<Long, String> map, List<Long> label, boolean cut) {
        String res = "";
        int orig = label.size();
        if (label.size() != 0) {
            if (cut)
                label = label.subList(0, Math.min(10, label.size()));
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < label.size(); j++) {
                String string = map.get(label.get(j));
                if (string == null)
                    string = "$";
                builder.append(string + " ");
            }
            res = builder.toString().trim();
        } else {
            res = "root";
        }
        if (cut && orig > 10)
            res += " [...]";
        return res;

    }

    public CharSequence getTranslatedEdgeLabel(SuffixNode child) {
        return translate(map, tree.getEdgeLabel(child), true);
    }

    public CharSequence getTranslatedLabel(SuffixNode node) {
        return translate(map, tree.getLabel(node), true);
    }

    public void put(long counter, String word) {
        map.put(counter, word);

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
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(
                    new File(string)));
            fileWriter
                    .write("/* this is a generated dot file: www.graphviz.org */\n"
                            + "digraph suffixtree {\n"
                            + "\trankdir=LR\nnode[shape=box]");
            printDotBody(tree.root, null, false, fileWriter, 0);
            fileWriter.write("}");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<SuffixNode> printDotBody(SuffixNode root,
            ArrayList<SuffixNode> list, boolean leavesOnly,
            BufferedWriter writer, int depth) throws IOException {
        tab(writer, depth);
        root.id = count;
        if (root.parent != null) {
            writer.write(root.parent.id + "->");
            writer.write("" + count);
            writer.write("[label=\""
                    + getTranslatedEdgeLabel(root).toString().trim()
                    + ",\\n Text: " + root.textNumber + ", Suffix: "
                    + ((SimpleNode) root).suffixIndex + "\"];\n");
        }
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
            // last = count;
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

}

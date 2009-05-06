package de.uni_koeln.spinfo.strings.algo;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.CharSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.DAG;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.NumericSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;

/**
 * Sample creation and export to Graphviz DOT for numeric, char-based and
 * word-based suffix trees and directed acyclic graphs (DAG).
 * @author Fabian Steeg (fsteeg)
 */
public class TestExport extends TestCase {
    public void testExport() {
        /* In the end, every suffix tree is a numeric tree: */
        NumericSuffixTree<?> tree;
        /* It can be a word-based suffix tree: */
        tree = new WordSuffixTree(
                "Ich esse Fisch. Ich esse Fleisch. Ich esse. Ich trinke.");
        tree.exportDot("word-tree.dot");
        /* Any suffix tree can be converted to a DAG: */
        DAG wordDag = new DAG(tree);
        wordDag.exportDot("word-dag.dot");
        /* Or a char-based suffix tree: */
        tree = new CharSuffixTree("aufgegangen abgegangen aufgehangen");
        tree.exportDot("char-tree.dot");
    }
}

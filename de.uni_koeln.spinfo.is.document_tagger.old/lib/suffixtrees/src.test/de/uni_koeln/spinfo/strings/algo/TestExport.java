package de.uni_koeln.spinfo.strings.algo;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.CharSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.DAG;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.NumericSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;

public class TestExport extends TestCase {
    public void testExport() {
        NumericSuffixTree tree;
        // ein Baum mit Woertern:
        tree = new WordSuffixTree(
                "Ich esse Fisch. Ich esse Fleisch. Ich esse. Ich trinke.");
        tree.exportDot("word-tree.dot");
        // ein Baum mit Buchstaben:
        tree = new CharSuffixTree("aufgegangen abgegangen aufgehangen");
        tree.exportDot("char-tree.dot");

        NumericSuffixTree wordTree = new WordSuffixTree(
                "Ich esse Fisch. Ich esse Fleisch. Ich esse. Ich trinke.");
        DAG wordDag = new DAG(wordTree);
        wordDag.exportDot("word-dag.dot");
    }
}

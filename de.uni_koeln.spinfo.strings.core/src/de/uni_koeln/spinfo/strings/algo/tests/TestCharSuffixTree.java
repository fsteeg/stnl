package de.uni_koeln.spinfo.strings.algo.tests;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.CharSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.DAG;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SimpleNodeAccessor;

public class TestCharSuffixTree extends TestCase {
    public void testCharTree() {
        CharSuffixTree tree = new CharSuffixTree("gehen gehe geht", false,
                true, new SimpleNodeAccessor());
        tree.exportDot("tree.dot");
    }

    // TODO somethings weird here... nodes with no connection to the root...
    // every second time it looks good, the structure at least, not the labels
    public void testDAGFromCharTree() {
        CharSuffixTree tree = new CharSuffixTree("gehen gehe geht", false,
                true, new SimpleNodeAccessor());
        tree.exportDot("tree.dot");
        DAG dag = new DAG(tree);
        ((CharSuffixTree) dag.graph).exportDot("dag.dot");
    }
}

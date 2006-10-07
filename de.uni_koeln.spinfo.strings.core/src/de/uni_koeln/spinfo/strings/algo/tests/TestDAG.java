/**
 * 
 */
package de.uni_koeln.spinfo.strings.algo.tests;

import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;

import de.uni_koeln.spinfo.strings.algo.suffixtrees.CharSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.DAG;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.Mapper;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.SimpleSequenceAccessor;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.NumericSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SimpleNodeAccessor;

/**
 * @author fsteeg
 * 
 */
public class TestDAG extends TestCase {

    @Test
    public void testDAGFromUkkonenSuffixTree() {
        NumericSuffixTree ukkonenSuffixTree = createLongTree();
        DAG dag = new DAG(ukkonenSuffixTree);
        Mapper mapper = new Mapper(dag.graph, new SimpleNodeAccessor());
        mapper.exportDot("dag.dot");
        
        ukkonenSuffixTree = createLongTree();
        dag = new DAG(ukkonenSuffixTree);
    }

    @Test
    public void testDAGFromWordSuffixTree2() {
        WordSuffixTree wordTree = new WordSuffixTree(
                "Ich esse Fisch. Ich esse Fleisch. Ich esse. Ich trinke.",
//                 "Ich esse Fisch. Ich esse Fleisch. Wir essen. Wir trinken.",
//                "Er hat gestern sehr gelacht. Sie hat heute sehr geweint. Es hat letztens sehr geregnet.",
                false, true, new SimpleNodeAccessor());
        wordTree.exportDot("tree.dot");
        DAG wordDag = new DAG(wordTree);
        wordDag.exportDot("dag.dot");
    }

    private NumericSuffixTree createLongTree() {
        SimpleNodeAccessor simpleNodeAccessor = new SimpleNodeAccessor();
        SimpleSequenceAccessor simpleSequenceAccessor = new SimpleSequenceAccessor();
        NumericSuffixTree ukkonenSuffixTree = new NumericSuffixTree(
                simpleNodeAccessor, simpleSequenceAccessor);
        ukkonenSuffixTree.addSequences(new Vector<Long>(Arrays
                .asList(new Long[] { 1L, 2L, 3L })), 0, false);
        ukkonenSuffixTree.addSequences(new Vector<Long>(Arrays
                .asList(new Long[] { 1L, 2L, 4L })), 0, false);
        ukkonenSuffixTree.addSequences(new Vector<Long>(Arrays
                .asList(new Long[] { 1L, 5L })), 0, false);
        ukkonenSuffixTree.addSequences(new Vector<Long>(Arrays
                .asList(new Long[] { 1L, 6L })), 0, false);
        Mapper mapper = new Mapper(ukkonenSuffixTree, simpleNodeAccessor);
        mapper.exportDot("tree.dot");
        return ukkonenSuffixTree;
    }

}

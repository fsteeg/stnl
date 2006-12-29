/**
 * 
 */
package de.uni_koeln.spinfo.strings.algo;

import java.util.HashSet;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.suffixtrees.AlphanumericSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;

/**
 * An implementation of alignment-based learning (ABL, van Zaanen, Geertzen)
 * 
 * @author fsteeg
 * 
 */
public class ABL {

    private AlphanumericSuffixTree forwardTree;

    private AlphanumericSuffixTree backwardTree;

    public ABL(String text) {
        forwardTree = new WordSuffixTree(text, false, true);
        forwardTree.exportDot("abl-forward.dot");
        backwardTree = new WordSuffixTree(text, true, true);
        backwardTree.exportDot("abl-backward.dot");
    }

    public Set<String> getConstituentsForward() {
        return bootstrapConstituents(forwardTree);
    }

    public Set<String> getConstituentsBackward() {
        return bootstrapConstituents(backwardTree);
    }

    private Set<String> bootstrapConstituents(AlphanumericSuffixTree forwardTree) {
        Set<String> cf = new HashSet<String>();
        for (Node node : forwardTree.getAllNodes()) {
            if (node.isInternal()) {
                cf.addAll(forwardTree.getLabelsUntilLeaf(node));
            }
        }
        return cf;
    }

    public Set<String> getConstituentsCombo() {
        // TODO implement
        return null;
    }

}

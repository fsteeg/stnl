package de.uni_koeln.spinfo.strings.algo;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.AlphanumericSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;

public class ParadigmTutorial extends TestCase {
    public void testP() {
        P p = new P(
                "we are eating bread. we are drinking beer. "
                        + "we are eating soup. we are drinking soup. you are drinking beer.");
        System.out.println(p.pardigmsInText);
    }
}

class P {
    AlphanumericSuffixTree forwardTree;

    Set<Set<String>> pardigmsInText;

    AlphanumericSuffixTree backwardTree;

    public P(String text) {
        // forward: "the man. the boy." --> [man, boy]
        this.forwardTree = new WordSuffixTree(text, false, true);
        pardigmsInText = bootstrapParadigms(forwardTree);
        // backward: "a man. the man." --> [a, the]
        this.backwardTree = new WordSuffixTree(text, true, true);
        pardigmsInText.addAll(bootstrapParadigms(backwardTree));
    }

    private Set<Set<String>> bootstrapParadigms(AlphanumericSuffixTree tree) {
        Set<Set<String>> paradigms = new HashSet<Set<String>>();
        for (Node node : tree.getAllNodes()) {
            if (node.isInternal()) {
                // the current paradigm: all children of an inner node
                Set<String> paradigm = new HashSet<String>();
                for (Node child : node.getChildren()) {
                    /*
                     * add the first word of the child's incoming edge label to
                     * the current paradigm:
                     */
                    paradigm
                            .add(tree.getIncomingEdgeLabel(child).split(" ")[0]);
                }
                paradigms.add(paradigm);
            }
        }
        return paradigms;
    }
}

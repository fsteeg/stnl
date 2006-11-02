package de.uni_koeln.spinfo.strings.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.suffixtrees.AlphanumericSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;

public class Paradigms {
    private AlphanumericSuffixTree forwardTree;

    private AlphanumericSuffixTree backwardTree;

    Set<Set<String>> pardigmsInText;

    Set<Set<String>> derivedParadigms;

    /**
     * @param text
     *            The text to bootstrap paradigms from.
     */
    public Paradigms(String text) {
        derivedParadigms = new HashSet<Set<String>>();
        // forward: "the man. the boy." --> [man, boy]
        this.forwardTree = new WordSuffixTree(text, false, true);
        pardigmsInText = bootstrapParadigms(forwardTree);
        // backward: "a man. the man." --> [a, the]
        this.backwardTree = new WordSuffixTree(text, true, true);
        pardigmsInText.addAll(bootstrapParadigms(backwardTree));
    }

    private Set<Set<String>> bootstrapParadigms(
            AlphanumericSuffixTree forwardTree) {
        Set<Set<String>> paradigmsInText = new HashSet<Set<String>>();
        /*
         * a mapping to remember which words are already in a paradigm, for
         * deriving paradigms:
         */
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        for (Node node : forwardTree.getAllNodes()) {
            if (node.isInternal()) {
                // the current paradigm: all children of an inner node
                Set<String> paradigm = new HashSet<String>();
                for (Node child : node.getChildren()) {
                    /*
                     * the first word of the edge label in a suffix tree is a
                     * member of the paradigm:
                     */
                    String paradigmMember = forwardTree.getIncomingEdgeLabel(
                            child).split(" ")[0];
                    paradigm.add(paradigmMember);
                    /*
                     * derive new paradigms: if a word is already in another
                     * paradigm, add all the members of that other paradigm to
                     * the current:
                     */
                    if (map.containsKey(paradigmMember)) {
                        Set<String> pp = new HashSet<String>(paradigm);
                        pp.addAll(map.get(paradigmMember));
                        derivedParadigms.add(pp);
                    }
                    map.put(paradigmMember, paradigm);
                }
                paradigmsInText.add(paradigm);
            }
        }
        derivedParadigms.removeAll(paradigmsInText);
        return paradigmsInText;
    }

    /* Below: Unused methods for demonstration purpose */

    /**
     * @param forwardTree
     *            The tree to compute the paradigm from
     * @return Return the paradigm found in the text
     */
    public Set<Set<String>> getSimpleParadigms(
            AlphanumericSuffixTree forwardTree) {
        Set<Set<String>> cf = new HashSet<Set<String>>();
        for (Node node : forwardTree.getAllNodes()) {
            if (node.isInternal()) {
                Set<String> p = new HashSet<String>();
                for (Node child : node.getChildren()) {
                    p.add(forwardTree.getIncomingEdgeLabel(child));
                }
                cf.add(p);
            }
        }
        return cf;
    }
    
    public Set<Set<String>> getParadigms(
            AlphanumericSuffixTree tree) {
        Set<Set<String>> paradigms = new HashSet<Set<String>>();
        for (Node node : tree.getAllNodes()) {
            if (node.isInternal()) {
                Set<String> paradigm = new HashSet<String>();
                for (Node child : node.getChildren()) {
                    paradigm.add(tree.getIncomingEdgeLabel(child));
                }
                paradigms.add(paradigm);
            }
        }
        return paradigms;
    }

    /**
     * brute-force way of deriving paradigms from given paradigms (NOTE: just
     * for demonstration, the derived paradigms are computed efficiently after
     * construction, and thus can be used without any method call)
     * 
     * @return The paradigm derived from the paradigm found in the text
     */
    public Set<Set<String>> getNaiveDerivedParadigms() {
        Set<Set<String>> paradigms = new HashSet<Set<String>>();
        for (Set<String> set1 : pardigmsInText) {
            for (Set<String> set2 : pardigmsInText) {
                if (!set1.equals(set2)) {
                    Set<String> s1 = new HashSet<String>(set1);
                    Set<String> s2 = new HashSet<String>(set1);
                    for (String string1 : set1) {
                        for (String string2 : set2) {
                            if (string1.equals(string2)) {
                                s1.addAll(set2);
                                s2.addAll(set1);
                                if (!pardigmsInText.contains(s1))
                                    paradigms.add(s1);
                                if (!pardigmsInText.contains(s2))
                                    paradigms.add(s2);
                            }
                        }
                    }

                }
            }
        }
        return paradigms;
    }
}

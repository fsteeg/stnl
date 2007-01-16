package de.uni_koeln.spinfo.strings.algo;

import java.util.ArrayList;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.AlphanumericSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.WordSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;

public class TestMatching extends TestCase {
    public void testMatching() {
        AlphanumericSuffixTree tree = new WordSuffixTree(
                "Ich esse Fisch. Ich esse Eis. Ich trinke Wasser.");
        
        for (Node node : tree.getRoot().getChildren()) {
            System.out.println(tree.getIncomingEdgeLabel(node));
        }
        tree.exportDot("test.dot");
        
        // Ermittlung der Blaetter runterhalb eines Knotens, z.B. root:
        Node node = tree.getRoot();
        // Das true um nur Blaetter zu bekommen:
        ArrayList<Node> allNodes = tree.getAllNodes(node, true);
        System.out.println("Blaetter im Teilbaum unter dem Knoten: " + allNodes.size());
        for (Node leaf : allNodes) {
            // Verwendung der Blaetter:
            System.out.println("Kantenlabel zum Blatt: " + tree.getIncomingEdgeLabel(leaf));
        }
        
//        System.out.println();
//        int[] find = tree.find("trinke Wasser");
//        System.out.println(find[0] + ", " + find[1]);
    }
}

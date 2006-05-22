/**
 * @author Fabian Steeg
 * Created on 03.01.2006
 *
 */
package de.uni_koeln.spinfo.strings.algo.tests;

import java.io.File;
import java.io.RandomAccessFile;

import junit.framework.TestCase;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.naive.CompactSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.naive.SimpleSuffixTree;

/**
 * Tests for {@link CompactSuffixTree}
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class TestSuffixTree extends TestCase {
    final static String texts = "texts/";

    protected void setUp() throws Exception {
        super.setUp();
        String s = String.valueOf(5);
    }

    /**
     * Test for {@link CompactSuffixTree}
     */
    public void testCreation() {
        // runMyTreeTwice("small-text.txt");
        runMyTreeTwice(texts + "small-text.txt");
        // runMyTreeTwice("small-text2.txt");
        // runMyTreeTwice("small-text3.txt");
    }

    /**
     * Test for {@link CompactSuffixTree}
     */
    public void runMyTreeTwice(String filename) {
        try {
            RandomAccessFile raf = new RandomAccessFile(new File(filename), "r");
            System.out.println("Running for: " + filename + ", Size: "
                    + raf.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        long current = System.currentTimeMillis();
        File file = new File(filename);
        @SuppressWarnings("unused")
        CompactSuffixTree tree = new CompactSuffixTree(new SimpleSuffixTree(
                file, false, false), false);
        System.out.println("Erster Lauf, meiner: "
                + (System.currentTimeMillis() - current));
        current = System.currentTimeMillis();
        tree = new CompactSuffixTree(new SimpleSuffixTree(file, false, true),
                false);
        System.out.println("Zweiter Lauf, meiner: "
                + (System.currentTimeMillis() - current));
    }

}

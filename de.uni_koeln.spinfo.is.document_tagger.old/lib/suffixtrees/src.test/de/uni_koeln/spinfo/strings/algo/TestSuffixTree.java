/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.uni_koeln.spinfo.strings.algo;

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
//        String s = String.valueOf(5);
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

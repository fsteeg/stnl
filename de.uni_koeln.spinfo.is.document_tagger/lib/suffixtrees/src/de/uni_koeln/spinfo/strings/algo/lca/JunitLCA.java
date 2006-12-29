package de.uni_koeln.spinfo.strings.algo.lca;

import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test harness using the Junit test framework for LCA. The Junit framework is
 * by Erich Gamma and Kent Beck, and is freely available on the internet. This
 * is written to use junit2.1. Junit or those names should be good search terms
 * to use to find a copy. I got mine via a link from
 * http:www.xprogramming.com/software.htm
 * 
 * @author A. G. McDowell (original version)
 */
public class JunitLCA extends TestCase {
    /** Constructor with test name as required by Junit */
    public JunitLCA(String s) {
        super(s);
    }

    private Random r;

    /** run test of cycle detect */
    public void testCycles() {
        TestLCA.cycleCheck(100, r, 0.5, 100);
    }

    /** run main test: fast LCA versus slow but simple version */
    public void testLCA() {
        TestLCA.lcaCheck(100, 100, r, 0.5, 20);
    }

    /** setUp function for Junit */
    protected void setUp() {
        r = new Random(42);
    }

    /** Test suite factory method for Junit */
    public static Test suite() {
        TestSuite s = new TestSuite();
        s.addTest(new JunitLCA("cycle detect") {
            protected void runTest() {
                this.testCycles();
            }
        });
        s.addTest(new JunitLCA("lca check") {
            protected void runTest() {
                this.testLCA();
            }
        });
        return s;
    }
}
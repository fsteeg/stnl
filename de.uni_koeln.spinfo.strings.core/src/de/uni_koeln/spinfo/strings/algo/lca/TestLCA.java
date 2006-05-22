package de.uni_koeln.spinfo.strings.algo.lca;

import java.util.Random;

/**
 * Test LCA. The main routine of this class is intended to be run from the
 * command line. Routines in this class are also called from a Junit test
 * framework class, JunitLCA.
 * 
 * @author A. G. McDowell (original version)
 */
public class TestLCA {
    /** Usual main program - this one just runs random tests */
    public static void main(String[] s) {
        long seed = 42;
        double probChild = 0.5;
        int maxDepth = 12;
        int cycleChecks = 400;
        int lcaChecks = 40;
        int innerChecks = 10;
        boolean trouble = false;

        int argp = 0;
        try {
            for (; argp < s.length; argp++) {
                if ("-cycle".equals(s[argp]) && argp < s.length - 1) {
                    argp++;
                    cycleChecks = Integer.parseInt(s[argp]);
                } else if ("-depth".equals(s[argp]) && argp < s.length - 1) {
                    argp++;
                    maxDepth = Integer.parseInt(s[argp]);
                } else if ("-inner".equals(s[argp]) && argp < s.length - 1) {
                    argp++;
                    innerChecks = Integer.parseInt(s[argp]);
                } else if ("-lca".equals(s[argp]) && argp < s.length - 1) {
                    argp++;
                    lcaChecks = Integer.parseInt(s[argp]);
                } else if ("-prob".equals(s[argp]) && argp < s.length - 1) {
                    argp++;
                    probChild = Double.parseDouble(s[argp]);
                } else if ("-seed".equals(s[argp]) && argp < s.length - 1) {
                    argp++;
                    seed = Long.parseLong(s[argp]);
                } else {
                    System.err.println("Could not recognise flag " + s[argp]);
                    trouble = true;
                }
            }
        } catch (NumberFormatException nf) {
            System.err.println("Could not read number in " + s[argp]);
            trouble = true;
        }

        if (trouble) {
            System.err.println("Usage is TestLCA [-cycle #] [-depth #] "
                    + "[-inner #] [-lca #] [-prob #] [-seed #]");
            System.exit(1);
        }

        System.out.println("cycle:" + cycleChecks + " depth:" + maxDepth
                + " inner:" + innerChecks + " lca:" + lcaChecks + " prob:"
                + probChild + " seed:" + seed);
        System.out.flush();

        Random r = new Random(seed);
        cycleCheck(cycleChecks, r, probChild, maxDepth);
        // Check LCA
        Swatch timer = new Swatch();
        timer.start();
        int tests = lcaCheck(lcaChecks, innerChecks, r, probChild, maxDepth);
        timer.stop();
        System.out.println("Time for " + tests + " inner tests is " + timer);
        System.out.println("All OK");
    }

    /** Create a tree for testing */
    private static TestNode buildTree(Random r, double probChild, int maxDepth,
            int depth, int[] num, TestNode parent) {
        if (depth > maxDepth)
            return null;
        int here = num[0];
        num[0] = here + 1;
        TestNode tn = new TestNode(here, depth, parent);
        if (depth == maxDepth)
            return tn;
        int children = 0;
        while (r.nextDouble() < probChild)
            children++;
        TestNode[] nursery = new TestNode[children];
        for (int i = 0; i < children; i++)
            nursery[i] = buildTree(r, probChild, maxDepth, depth + 1, num, tn);
        tn.setChildren(nursery);
        return tn;
    }

    /**
     * Check cycle detect by introducing one, and checking that LCA detects it.
     * Catches the resulting exception, so this routine throws iff LCA doesn't -
     * unless something else goes wrong.
     */
    public static void cycleCheck(int cycleChecks, Random r, double probChild,
            int maxDepth) {
        // Check cycle detect
        for (int c = 0; c < cycleChecks;) {
            int[] num = new int[1];
            num[0] = 1;
            TestNode sample = buildTree(r, probChild, maxDepth, 0, num, null);
            int maxAllocated = num[0] - 1;
            TestNode[] nodes = new TestNode[maxAllocated + 1];
            setPointers(sample, nodes);
            // int drop1;
            // int drop2;
            for (int i = 0; i < 10; i++) {
                int drop = r.nextInt(maxAllocated) + 1;
                TestNode tn = nodes[drop];
                int childrenHere = tn.numChildren();
                if (childrenHere <= 0)
                    continue;
                int d = tn.getDepth();
                if (d > 0)
                    d = r.nextInt(d);
                TestNode parent = tn;
                while (d-- > 0)
                    parent = parent.getParent();
                tn.setChild(r.nextInt(childrenHere), parent);
                try {
                    new LCA(sample);
                } catch (IllegalStateException se) {
                    c++;
                    break;
                }
                // In fact, failing to catch a cycle should land us in an
                // infinite loop or stack overflow
                throw new IllegalStateException("Could not find cycle");
            }
        }
    }

    /**
     * Check that the fast LCA algorithm works by comparing it with the result
     * of a slower one.
     */
    public static int lcaCheck(int lcaChecks, int innerChecks, Random r,
            double probChild, int maxDepth) {
        int tests = 0;
        for (int c = 0; c < lcaChecks; c++) {
            // System.err.println("LCA check " + c + " of " + cycleChecks);
            int[] num = new int[1];
            num[0] = 1;
            TestNode sample = buildTree(r, probChild, maxDepth, 0, num, null);
            int maxAllocated = num[0] - 1;
            TestNode[] nodes = new TestNode[maxAllocated + 1];
            setPointers(sample, nodes);
            LCA lc = new LCA(sample);
            sample.checkNum(); // Check numbers from LCA really are dfs
            for (int i = 0; i < innerChecks; i++) {
                TestNode ta = nodes[1 + r.nextInt(maxAllocated)];
                TestNode tb = nodes[1 + r.nextInt(maxAllocated)];
                TestNode simple = simpleLCA(ta, tb);
                TreeNode fast = lc.lca(ta.getNum(), tb.getNum());
                if (simple != fast)
                    throw new IllegalStateException("Mismatch in LCAs");
                tests++;
            }
        }
        return tests;
    }

    /**
     * Set up an array of pointers into the tree, indexed by depth first search
     * number (counting from 1 - because that's good for the LCA algorithm).
     */
    private static void setPointers(TestNode top, TestNode[] ar) {
        if (top == null)
            return;
        ar[top.getNum()] = top;
        for (int i = top.numChildren() - 1; i >= 0; i--)
            setPointers(top.getTestChild(i), ar);
    }

    /**
     * Simple Least Common Ancestor code to check - takes time depending on
     * distance to common ancestor.
     * 
     * @param a
     *            node to find LCA of
     * @param b
     *            node to find LCA of
     * @exception NullPointerException
     *                if two nodes not in same tree, or other stupidity
     */
    private static TestNode simpleLCA(TestNode a, TestNode b) {
        int aDepth = a.getDepth();
        int bDepth = b.getDepth();
        if (aDepth < bDepth) {
            TestNode t = a;
            a = b;
            b = t;
            int td = aDepth;
            aDepth = bDepth;
            bDepth = td;
        }
        while (aDepth-- > bDepth)
            a = a.getParent();
        while (a != b) {
            a = a.getParent();
            b = b.getParent();
        }
        return a;
    }
}

class TestNode implements TreeNode {
    private int ourDepth;

    public int getDepth() {
        return ourDepth;
    }

    private int ourNum;

    private int theirNum;

    private TestNode[] children;

    public int numChildren() {
        if (children == null)
            return 0;
        return children.length;
    }

    public TestNode getTestChild(int x) {
        return children[x];
    }

    public TreeNode getChild(int x) {
        return getTestChild(x);
    }

    private TestNode parent;

    public TestNode getParent() {
        return parent;
    }

    public int getNum() {
        return ourNum;
    }

    public void checkNum() {
        if (ourNum != theirNum)
            throw new IllegalStateException("Number mismatch");
        if (children == null)
            return;
        for (int i = 0; i < children.length; i++)
            children[i].checkNum();
    }

    public void acceptDFSNum(int x) {
        theirNum = x;
    }

    public TestNode(int x, int depth, TestNode parent) {
        children = null;
        ourDepth = depth;
        ourNum = x;
        this.parent = parent;
    }

    void setChild(int num, TestNode t) {
        children[num] = t;
    }

    void setChildren(TestNode[] children) {
        this.children = children;
    }

    public int getDFSNum() {
        // TODO Auto-generated method stub
        return 0;
    }
}
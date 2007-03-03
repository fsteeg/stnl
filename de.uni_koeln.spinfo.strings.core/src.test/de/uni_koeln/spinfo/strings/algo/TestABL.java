package de.uni_koeln.spinfo.strings.algo;

import java.util.Set;


import junit.framework.TestCase;

/**
 * An STNL (http://stnl.sourceforge.net) tutorial
 * 
 * @author fsteeg
 * 
 */
public class TestABL extends TestCase {

    private ABL abl;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        abl = new ABL(
                "ich esse brot. ich trinke bier. ich esse quark. ich trinke quark. er trinke bier.");
    }

    /**
     * constituents in the middle of sentences
     */
    public void testCombo() {
        Set<String> constituentsCombo = abl.getConstituentsCombo();
        fail("Not yet implemented!");
        assertTrue(constituentsCombo.contains("esse"));
        assertTrue(constituentsCombo.contains("trinke"));
    }

    /**
     * constituents at the beginning of sentences
     */
    public void testBackward() {
        Set<String> constituentsBackward = abl.getConstituentsBackward();
        assertTrue(constituentsBackward.contains("er"));
        assertTrue(constituentsBackward.contains("ich"));

        assertTrue(constituentsBackward.contains("trinke ich"));
        assertTrue(constituentsBackward.contains("esse ich"));
    }

    /**
     * constituents at the end of sentences
     */
    public void testForward() {
        Set<String> constituentsForward = abl.getConstituentsForward();
        assertTrue(constituentsForward.contains("esse brot"));
        assertTrue(constituentsForward.contains("trinke bier"));
        assertTrue(constituentsForward.contains("esse quark"));
        assertTrue(constituentsForward.contains("trinke quark"));

        assertTrue(constituentsForward.contains("quark"));
        assertTrue(constituentsForward.contains("brot"));

        assertTrue(constituentsForward.contains("bier"));
        assertTrue(constituentsForward.contains("quark"));
    }

}

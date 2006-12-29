/**
 * 
 */
package de.uni_koeln.spinfo.strings.algo;

import junit.framework.TestCase;

/**
 * Bootstrapping paradigms from free text - an STNL Tutorial
 * 
 * @author fsteeg
 * 
 */
public class TestParadigms extends TestCase {

    private Paradigms p;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        p = new Paradigms(
                "we are eating bread. we are drinking beer. we are eating soup. we are drinking soup. you are drinking beer.");
        super.setUp();
    }

    /**
     * the paradigms found in the text
     */
    public void testParadigmsInText() {
        System.out.println("Paradigms found in text: " + p.pardigmsInText);
        assertEquals(
                "[[soup, bread], [drinking, eating], [soup, beer], [you, we]]",
                p.pardigmsInText.toString());
    }

    /**
     * the paradigms derived from the paradigms found (if paradigms have common
     * members, add them to each other)
     */
    public void testDerivedParadigms() {
        System.out
                .println("Paradigms derived from text: " + p.derivedParadigms);
        assertEquals("[[soup, bread, beer]]", p.derivedParadigms.toString());
    }

}

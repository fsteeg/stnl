/**
 * 
 */
package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Bootstrapping paradigms from free text - an STNL Tutorial
 * 
 * @author fsteeg
 * 
 */
public class TestParadigms {

    private Paradigms p;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        p = new Paradigms(
                "we are eating bread. we are drinking beer. we are eating soup. we are drinking soup. you are drinking beer.");
    }

    /**
     * the paradigms found in the text
     */
    @Test
    public void testParadigmsInText() {
        System.out.println("Paradigms found in text: " + p.pardigmsInText);
        assertEquals("[[soup, bread], [drinking, eating], [soup, beer]]",
                p.pardigmsInText.toString());
    }

    /**
     * the paradigms derived from the paradigms found (if paradigms have common
     * members, add them to each other)
     */
    @Test
    public void testDerivedParadigms() {
        System.out
                .println("Paradigms derived from text: " + p.derivedParadigms);
        assertEquals("[[soup, bread, beer], [you, we]]", p.derivedParadigms.toString());
    }

}

package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class TextTest {
    @Test
    public void test() {
        Text t = new Text("Content", new HashSet<String>(Arrays.asList("tag")),
                "location");
        assertEquals("Content", t.content);
        assertEquals("tag", t.tags.iterator().next());
        assertEquals("location", t.location);
    }
}

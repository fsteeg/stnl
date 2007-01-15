package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public class ClassificationTest {

    @Test
    public void testTag() {
        DocumentTagger tagger = new DocumentTagger();
        tagger
                .learn(Arrays.asList(new Text("Hallo du. Hallo ihr. Alle ihr.",
                        new HashSet<String>(Arrays.asList("test")),
                        "testing-nowhere")));
        Classification c = new Classification(tagger);
        List<Text> tagged = c.tag(Arrays.asList(new Text(
                "Hallo du. Hallo ihr. Alle ihr.", new HashSet<String>(Arrays
                        .asList("nonsense")), "testing-nowhere")));
        assertTrue(tagged.size() > 0);
        assertTrue(tagged.get(0).tags != null);
        assertTrue(tagged.get(0).tags.size() > 0);
        assertEquals("test", tagged.get(0).tags.iterator().next());

    }
}

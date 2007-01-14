package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class AcquirementTest {

    @Test
    public void testLearn() {
        Acquirement a = new Acquirement(new DocumentTagger());
        a.learn(Arrays.asList(new Text("Hallo ihr. Hallo du. Alle ihr.",
                new HashSet<String>(Arrays.asList("test")), "test")));
        Map<String, Set<Set<String>>> map = a.tagger.paradigmsForTags;
        assertTrue(map.values().iterator().next().contains(
                new HashSet<String>(Arrays.asList("ihr", "du"))));
        assertTrue(map.values().iterator().next().contains(
                new HashSet<String>(Arrays.asList("Hallo", "Alle"))));
    }

}

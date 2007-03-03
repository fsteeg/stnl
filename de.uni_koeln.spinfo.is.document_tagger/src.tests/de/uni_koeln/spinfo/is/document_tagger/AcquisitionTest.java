package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class AcquisitionTest {

    @Test
    public void testLearn() throws FileNotFoundException, IOException,
            ClassNotFoundException {
        Acquisition a = null;
        a = new Acquisition(new DocumentTagger("config/tagger.properties"));
        a.learn(Arrays.asList(new Text("Hallo Anna. Hallo Herbert. Hi Anna.",
                new HashSet<String>(Arrays.asList("test")), "test")));
        Map<Set<String>, Set<String>> map = a.tagger.paradigmsForTags;
        assertTrue(map.keySet().contains(
                new HashSet<String>(Arrays.asList("Anna", "Herbert"))));
        assertTrue(map.keySet().contains(
                new HashSet<String>(Arrays.asList("Hallo", "Hi"))));
    }

}

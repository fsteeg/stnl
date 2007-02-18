package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

public class ClassificationTest {

    /**
     * Test zur Verdeutlichung, dass gleiche Worte in anderen Kontexten nicht so
     * behandelt werden wie gleiche Worte in gleichen Kontexten
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws FileNotFoundException
     */
    @Test
    public void testTag2() throws FileNotFoundException, IOException,
            ClassNotFoundException {
        DocumentTagger tagger = null;
        tagger = new DocumentTagger("config/tagger.properties");
        tagger.learn(Arrays.asList(new Text(
                "Hans mag Eis. Anna mag Chips nicht. Ich mag Chips gern.",
                new HashSet<String>(Arrays.asList("test1", "test2", "test3")),
                "testing-nowhere")));
        Classification c = new Classification(tagger);
        List<Text> tagged = c
                .tag(
                        Arrays
                                .asList(new Text(
                                        "Ich muss heute wieder Eis kratzen. In Dresden produziert Infineon Chips.",
                                        new HashSet<String>(),
                                        "testing-nowhere")), "out-unit.txt");
        // Eis und Chips in anderen Kontaxten: Text bekommt keine Klassen:
        assertTrue(tagged.size() > 0);
        assertTrue(tagged.get(0).tags != null);
        assertTrue(tagged.get(0).tags.size() == 0);
    }

    /**
     * Wenn Woerter in gleichen Kontexten auftauchen, werden die Texte als der
     * gleichen Klasse angehoerig betrachtet
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws FileNotFoundException
     */
    @Test
    public void testTag3() throws FileNotFoundException, IOException,
            ClassNotFoundException {
        DocumentTagger tagger;
        tagger = new DocumentTagger("config/tagger.properties");
        tagger.learn(Arrays.asList(new Text(
                "Hans mag Eis. Anna mag Chips nicht. Ich mag Chips gern.",
                new HashSet<String>(Arrays.asList("test1", "test2", "test3")),
                "testing-nowhere")));
        Classification c = new Classification(tagger);
        List<Text> tagged = c.tag(Arrays.asList(new Text(
                "Ich esse Eis. Ich esse Chips.", new HashSet<String>(),
                "testing-nowhere")), "out-unit.txt");
        assertTrue(tagged.size() > 0);
        assertTrue(tagged.get(0).tags != null);
        assertTrue(tagged.get(0).tags.size() > 0);
        assertTrue(tagged.get(0).tags.contains("test1"));
        assertTrue(tagged.get(0).tags.contains("test2"));
        assertTrue(tagged.get(0).tags.contains("test3"));

    }
}

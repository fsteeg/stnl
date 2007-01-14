package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class EvaluationTest {
    @Test
    public void testEvaluation() {
        Evaluation e = new Evaluation(new HashSet<String>(Arrays.asList("java",
                "programming")), new HashSet<String>(Arrays.asList("java",
                "programming", "music")));
        assertEquals(2.0 / 3.0, e.recall());
        assertEquals(2.0 / 2.0, e.precision());
    }
}

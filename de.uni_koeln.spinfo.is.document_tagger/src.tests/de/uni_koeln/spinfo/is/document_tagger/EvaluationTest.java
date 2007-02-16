package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class EvaluationTest {
    @Test
    public void testEvaluation() {
        Evaluation e = new Evaluation();
        Text t = new Text("", new HashSet<String>(Arrays.asList("java",
                "programming", "music")), "dummy-location");
        try {
            FileWriter fileWriter = new FileWriter("junit-output.txt");
            e.evaluate(t, new HashSet<String>(Arrays.asList("java",
                    "programming")), fileWriter);
            assertEquals(2.0 / 3.0, e.recall());
            assertEquals(2.0 / 2.0, e.precision());
            e.evaluate(t, new HashSet<String>(Arrays.asList("java",
                    "programming", "music")), fileWriter);
            fileWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}

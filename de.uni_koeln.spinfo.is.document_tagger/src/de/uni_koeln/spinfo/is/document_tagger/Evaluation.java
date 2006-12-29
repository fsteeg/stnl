package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class Evaluation {
    private Set<String> original;

    private Set<String> result;

    public Evaluation(Set<String> result, Set<String> original) {
        this.result = result;
        this.original = original;
    }

    public double fValue() {
        return (2 * recall() * precision()) / (recall() + precision());
    }

    public double recall() {
        return hits() / original.size();
    }

    public double precision() {
        return hits() / result.size();
    }

    private double hits() {
        double found = 0.0;
        for (String s : result) {
            if (original.contains(s))
                found++;
        }
        return found;
    }

    public Evaluation() {

    }

    @Test
    public void testEvaluation() {
        Evaluation e = new Evaluation(new HashSet<String>(Arrays.asList("java",
                "programming")), new HashSet<String>(Arrays.asList("java",
                "programming", "music")));
        assertEquals(2.0 / 3.0, e.recall());
        assertEquals(2.0 / 2.0, e.precision());
    }
}

package de.uni_koeln.spinfo.is.document_tagger;

import java.util.Set;

/**
 * Evaluation of classification results. Compares a resulting set of tags with a
 * desired, given set of tags, using precision, recall and f-value.
 * 
 * @author fsteeg
 * 
 */
public class Evaluation {
    private Set<String> original;

    private Set<String> result;

    /**
     * @param result
     *            The result of the classification, which is to be evaluated.
     * @param original
     *            The desired, given sample.
     */
    public Evaluation(Set<String> result, Set<String> original) {
        this.result = result;
        this.original = original;
    }

    /**
     * @return Returns teh f-Value, a unifying number of precision and recall.
     */
    public double fValue() {
        return (2 * recall() * precision()) / (recall() + precision());
    }

    /**
     * @return Returns the recall of the evaluated result.
     */
    public double recall() {
        return hits() / original.size();
    }

    /**
     * @return Returns the precision of the evaluated result.
     */
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
}

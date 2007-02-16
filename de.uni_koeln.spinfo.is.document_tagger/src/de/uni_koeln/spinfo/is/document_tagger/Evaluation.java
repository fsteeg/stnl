package de.uni_koeln.spinfo.is.document_tagger;

import java.text.NumberFormat;
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

    private double precisionSum;

    private double recallSum;

    private int count;

    /**
     * @param result
     *            The result of the classification, which is to be evaluated.
     * @param original
     *            The desired, given sample.
     */
    // public void Evaluation(final Set<String> result, final Set<String>
    // original) {
    // this.result = result;
    // this.original = original;
    // }
    public Evaluation() {
        this.count = 0;
        this.recallSum = 0;
        this.precisionSum = 0;

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
        return hits() / this.original.size();
    }

    /**
     * @return Returns the precision of the evaluated result.
     */
    public double precision() {
        return hits() / this.result.size();
    }

    private double hits() {
        double found = 0.0;
        for (String s : this.result) {
            for (String o : this.original) {
                if (o.contains(":")) {
                    o = o.split(":")[1];
                }
                if (s.contains(":")) {
                    s = s.split(":")[1];
                }
                if (s.equalsIgnoreCase(o)) {
                    found++;
                }
            }
        }
        return found;
    }

    /**
     * Evaluates the classification result by cmparing the original tags with
     * the tags retrieved by the program, using recall, precision anf f-value.
     * 
     * @param originalText
     *            The original tags
     * @param newTags
     *            The new tags
     */
    void evaluate(final Text originalText, final Set<String> newTags) {
        // evaluation(newTags, originalText.tags);
        this.result = newTags;
        this.original = originalText.tags;
        System.out.println();
        count++;
        double recall = recall();
        recallSum += recall;
        double precision = precision();
        precisionSum += precision;
        System.out.println("Evaluation; Recall: "
                + NumberFormat.getNumberInstance().format(recall) + " (Mittel "
                + recallSum / count + "), Precision: "
                + NumberFormat.getNumberInstance().format(precision) + " (Mittel"
                + precisionSum / count + "), F-Value: "
                + NumberFormat.getNumberInstance().format(fValue()));
        System.out.println();
        if ((recall > 0) || (newTags.size() > 0)) {
            System.out
                    .println("________________________________________________________");
            System.out.println("New Tags for " + originalText.location);
            for (String s : newTags) {
                System.out.println(s);
            }
            System.out
                    .println("--------------------------------------------------------");
            System.out.println("Original Tags for " + originalText.location);
            for (String s : originalText.tags) {
                System.out.println(s);
            }
        }
    }
}

package de.uni_koeln.spinfo.is.document_tagger;

import java.io.IOException;
import java.io.Writer;
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

    private double fSum;

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
        this.fSum = 0;

    }

    /**
     * @return Returns teh f-Value, a unifying number of precision and recall.
     */
    public double fMeasure() {
        double d = (recall() + precision());
        return d == 0 ? 0 : (2 * recall() * precision()) / d;
    }

    /**
     * @return Returns the recall of the evaluated result.
     */
    public double recall() {
        int size = this.original.size();
        return size == 0 ? 0 : hits() / size;
    }

    /**
     * @return Returns the precision of the evaluated result.
     */
    public double precision() {
        int size = this.result.size();
        return size == 0 ? 0 : hits() / size;
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
     * @throws IOException
     */
    void evaluate(final Text originalText, final Set<String> newTags,
            Writer writer) throws IOException {
        // evaluation(newTags, originalText.tags);
        this.result = newTags;
        this.original = originalText.tags;
        // writer.write("\n");
        count++;
        double recall = recall();
        recallSum += recall;
        double precision = precision();
        precisionSum += precision;
        fSum += fMeasure();
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        writer.write("\n");// "________________________________________________________________________________________________________________\n");
        writer.write("Evaluation; Recall: " + numberInstance.format(recall)
                + " (Mittel " + numberInstance.format(recallSum / count)
                + "), Precision: " + numberInstance.format(precision)
                + " (Mittel " + numberInstance.format(precisionSum / count)
                + "), F-Value: " + numberInstance.format(fMeasure())
                + " (Mittel " + numberInstance.format(fSum / count) + ")");
        // writer.write("\n");
        if ((recall > 0) || (newTags.size() > 0)) {
            writer.write("\n");// "\n----------------------------------------------------------------------------------------------------------------\n");
            writer.write("New Tags for " + originalText.location + ": ");
            for (String s : newTags) {
                writer.write(s + " ");
            }
            writer.write("\n");// \n----------------------------------------------------------------------------------------------------------------\n");
            writer.write("Original Tags for " + originalText.location + ": ");
            for (String s : originalText.tags) {
                writer.write(s + " ");
            }
            writer.write("\n");// "\n________________________________________________________________________________________________________________\n");
        }
    }
}

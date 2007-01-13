package de.uni_koeln.spinfo.is.document_tagger;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.Paradigms;

/**
 * Classification of given texts: Tags text based on their paradigms.
 * 
 * @author fsteeg, ssubicin
 * 
 */
public class Classification {
    private DocumentTagger tagger;

    /**
     * @param tagger
     *            The tagger
     */
    public Classification(DocumentTagger tagger) {
        this.tagger = tagger;
    }

    /**
     * @param texts
     *            The texts to tag.
     */
    public void tag(List<Text> texts) {
        for (Text text : texts) {
            String content = text.content;
            Paradigms p = new Paradigms(content);
            Set<Set<String>> results = p.pardigmsInText;
            Set<String> newTags = new HashSet<String>();
            for (String tag : tagger.paradigmsForTags.keySet()) {
                Set<Set<String>> newPar = tagger.paradigmsForTags.get(tag);
                for (Set<String> set : newPar) {
                    set = Preprocessor.filter(set, "stopwords");
                    if (set == null)
                        continue;
                    for (Set<String> r : results) {
                        r = Preprocessor.filter(r, "stopwords");
                        if (newTags.contains(tag))
                            continue;
                        int counted = 0;
                        // TODO adjust this during learning, to optimize
                        // results:
                        int threshold = 1;
                        for (String s : set) {
                            if (r.contains(s)) {
                                counted++;
                            }
                        }
                        if (counted >= threshold) {
                            // TODO called too often
                            // System.out.println("Adding: " + tag
                            // + " with count " + counted);
                            newTags.add(tag);
                        }
                    }
                }
            }
            evaluate(text, newTags);
        }

    }

    /**
     * Evaluates the classification result by cmparing the original tags with
     * the tags retrieved by the program, using recall, precision anf f-value.
     * 
     * @param originalTags
     *            The original tags
     * @param newTags
     *            The new tags
     */
    private void evaluate(Text originalTags, Set<String> newTags) {
        Evaluation e = new Evaluation(newTags, originalTags.tags);
        System.out.println("Evaluation; Recall: "
                + NumberFormat.getNumberInstance().format(e.recall())
                + ", Precision: "
                + NumberFormat.getNumberInstance().format(e.precision())
                + ", F-Value: "
                + NumberFormat.getNumberInstance().format(e.fValue()));
        if (e.recall() > 0) {
            System.out
                    .println("________________________________________________________");
            System.out.println("New Tags for " + originalTags.location);
            for (String s : newTags) {
                System.out.println(s);
            }
            System.out
                    .println("--------------------------------------------------------");
            System.out.println("Original Tags for " + originalTags.location);
            for (String s : originalTags.tags) {
                System.out.println(s);
            }
        }
    }
}

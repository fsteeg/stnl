package de.uni_koeln.spinfo.is.document_tagger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.Paradigms;

/**
 * Classification of given texts: Tags text based on their paradigms.
 * 
 * @author fsteeg, ssubicin
 * 
 */
public class Classification {
    DocumentTagger tagger;

    /**
     * @param tagger
     *            The tagger
     */
    public Classification(final DocumentTagger tagger) {
        this.tagger = tagger;
    }

    /**
     * @param texts
     *            The texts to tag.
     */
    public List<Text> tag(final List<Text> texts) {
        System.out.print("Creating index...");
        for (Set<String> key : this.tagger.paradigmsForTags.keySet()) {
            Set<String> set = this.tagger.paradigmsForTags.get(key);
            System.out.println("Tags for paradigm: " + key + "   ++++++++    "
                    + set);
            for (String tag : set) {
                for (String string : key) {
                    this.tagger.index1.put(string, key);
                }
                set.add(tag);
            }
        }
        System.out.println("Tagging, using "
                + this.tagger.paradigmsForTags.keySet().size()
                + " features (paradigms)"
                + this.tagger.paradigmsForTags.keySet().size());
        for (Text text : texts) {
            Set<String> newTags = tag(text);
            Evaluation.evaluate(text, newTags);
            text.tags = newTags;
        }
        return texts;

    }

    /**
     * @param text
     *            The text to tag
     * @return Returns a set of strings, the tags for the text
     */
    private Set<String> tag(final Text text) {

        String content = text.content;
        // identify the paradigms in the text:
        Paradigms p = new Paradigms(content);
        Set<Set<String>> paradigmsInNewText = p.pardigmsInText;
        Set<String> newTags = new HashSet<String>();
        // index of members and their paradigms, eg heine -->
        // [heine,goethe,schiller]
        Map<String, Set<String>> index1 = this.tagger.index1;
        // index of paradigms and their tags, eg [heine,goethe,schiller] -->
        // literature
        Map<Set<String>, Set<String>> index2 = this.tagger.paradigmsForTags;
        // result: a mapping of candidate tags and their best single paradigm
        // correpondence
        Map<String, Double> correspondence = new HashMap<String, Double>();
        for (Set<String> para : paradigmsInNewText) {
            for (String string : para) {
                Set<String> paradigm = index1.get(string);
                if (paradigm != null) {
                    Set<String> tags = index2.get(paradigm);
                    if (tags != null) {
                        int before = paradigm.size();
                        HashSet<String> pa = new HashSet<String>(paradigm);
                        pa.removeAll(para);
                        double hits = Math.abs(pa.size() - before)
                                / (double) before;
                        for (String tag : tags) {
                            Double double1 = correspondence.get(tag);
                            if (double1 != null) {
                                correspondence
                                        .put(tag, Math.max(double1, hits));
                            } else {
                                correspondence.put(tag, hits);
                            }
                        }
                    }
                }

            }
            // TODO count relevant paradigms
        }
        for (String string : correspondence.keySet()) {
            Double d = correspondence.get(string);
            if (d > 0.5) {
                System.out.println("Adding " + string + ". correspondence is: "
                        + d);
                newTags.add(string);
            }
        }
        return newTags;
    }
}

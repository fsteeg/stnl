package de.uni_koeln.spinfo.is.document_tagger;

import java.text.NumberFormat;
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
    public Classification(DocumentTagger tagger) {
        this.tagger = tagger;
    }

    /**
     * @param texts
     *            The texts to tag.
     */
    public List<Text> tag(List<Text> texts) {
        System.out.print("Creating index...");
        for (Set<String> key : tagger.paradigmsForTags.keySet()) {
            Set<String> set = tagger.paradigmsForTags.get(key);
            System.out.println("Tags for paradigm: " + key + "   ++++++++    "
                    + set);
            for (String tag : set) {
                for (String string : key) {
                    tagger.index1.put(string, key);
                }
                set.add(tag);
            }
        }
        System.out.println("Tagging, using "
                + tagger.paradigmsForTags.keySet().size()
                + " features (paradigms)"
                + tagger.paradigmsForTags.keySet().size());
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
    private Set<String> tag(Text text) {

        String content = text.content;
        // identify the paradigms in the text:
        Paradigms p = new Paradigms(content);
        Set<Set<String>> paradigmsInNewText = p.pardigmsInText;
        Set<String> newTags = new HashSet<String>();

        // index of members and their paradigms, eg heine -->
        // [heine,goethe,schiller]
        Map<String, Set<String>> index1 = tagger.index1;
        // index of paradigms and their tags, eg [heine,goethe,schiller] -->
        // literature
        Map<Set<String>, Set<String>> index2 = tagger.paradigmsForTags;
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
                            if (double1 != null)
                                correspondence
                                        .put(tag, Math.max(double1, hits));
                            else
                                correspondence.put(tag, hits);
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

        // // for all the tags the tagger knows...
        // for (String tag : tagger.paradigmsForTags.keySet()) {
        // if (relevant(paradigmsInNewText, newTags, tag))
        // newTags.add(tag);
        // }
        // System.out.println("Tagged Text");
        return newTags;
    }

    // private boolean relevant(Set<Set<String>> paradigmsInNewText,
    // Set<String> newTags, String tag) {
    // // we check all the associated paradigms...
    // Set<Set<String>> paradigmsForTag = tagger.paradigmsForTags.get(tag);
    // System.out.print("Checking relevance of: " + tag + ", "
    // + paradigmsInNewText.size() + " paradigms in the new text, "
    // + paradigmsForTag.size() + " paradigms for current tag...");
    // int anotherCounter = 0;
    // for (Set<String> paradigm : paradigmsForTag) {
    // paradigm = Preprocessor.filter(paradigm, "stopwords");
    // if (paradigm == null)
    // continue;
    // // we compare every word of the paradigm...
    // // and for every paradigm found for the text to tag...
    // double hits = 0.0;
    // hits = bestCorrespondence(paradigmsInNewText, paradigm);
    // // System.out.println(hits + " hits.");
    // if (hits > 0) {
    // // System.out.println("Upping another counter");
    // anotherCounter++;
    // }
    // }
    // System.out.println(" final counter: " + anotherCounter
    // + " relevant in " + paradigmsForTag.size() + " paradigms.");
    // if (anotherCounter > 0)// paradigmsForTag.size() / 2)
    // return true;
    // return false;
    // }

    // private double bestCorrespondence(Set<Set<String>> paradigmsInNewText,
    // Set<String> paradigm) {
    // double bestHit = 0.0;
    // // System.out.println("We have " + paradigmsInNewText.size()
    // // + " paradigms in the new text...");
    // for (Set<String> correspondingParadigm : paradigmsInNewText) {
    // correspondingParadigm = Preprocessor.filter(correspondingParadigm,
    // "stopwords");
    // if (correspondingParadigm == null)
    // continue;
    // int length = correspondingParadigm.size();
    // correspondingParadigm.removeAll(paradigm);
    // int hits = (length - correspondingParadigm.size()) / length;
    // if (hits == 1)
    // return 1; // full hit, it won't get any better
    // // System.out.println(hits + " hits");
    // // for (String s : correspondingParadigm) {
    // // if (paradigm.contains(s)) {
    // // hits++;
    // // }
    // // }
    // bestHit = Math.max(bestHit, hits);
    // }
    //
    // return bestHit;
    //
    // // for (String member : paradigm) {
    // // int counted = 0;
    // // // TODO adjust this during learning, to optimize
    // // // results:
    // // int threshold = (paradigm.size() / 5) * 4;
    // // // // we compare every word of the paradigm...
    // // // for (String member : paradigm) {
    // // // if (paradigmInNewText.contains(member)) {
    // // // counted++;
    // // // }
    // // // }
    // // for (Set<String> paradigmInNewText : paradigmsInNewText) {
    // // paradigmInNewText = Preprocessor.filter(paradigmInNewText,
    // // "stopwords");
    // // if (paradigmInNewText == null)
    // // continue;
    // // // if (newTags.contains(tag))
    // // // continue;
    // // if (paradigmInNewText.contains(member)) {
    // // counted++;
    // // System.out.println("Upping counted");
    // // }
    // //
    // // }
    // // if (counted >= threshold) {
    // // // TODO called too often
    // // // System.out.println("Adding: " + tag
    // // // + " with count " + counted);
    // // // newTags.add(tag);
    // // hits++;
    // // System.out.println("Upping hits");
    // // // TODO we could count this too: the number ov relevant
    // // // paradigms
    // // }
    // //
    // // }
    // // return hits;
    // }

   
}

package de.uni_koeln.spinfo.is.document_tagger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.Paradigms;

/**
 * Knowledge Acquisition: Learns from tagged texts by creating paradigms and
 * storing a mapping of paradigms and tags.
 * 
 * @author fsteeg, ssubicin
 * 
 */
public class Acquirement {
    DocumentTagger tagger;

    private List<Text> texts;

    /**
     * @param tagger
     *            The tagger
     */
    public Acquirement(DocumentTagger tagger) {
        this.tagger = tagger;
    }

    /**
     * Retrieves all paradigms from the text, storing them in a mapping, mapping
     * tags to paradigms.
     */
    private void extractParadigms() {
        for (Text text : texts) {
            Paradigms p = new Paradigms(text.content);
            Set<Set<String>> results = p.pardigmsInText;
            Set<String> tags = text.tags;
            
            for (String tag : tags) {
                Set<Set<String>> paradigms = tagger.paradigmsForTags
                        .get(tag);
                if (paradigms == null) {
                    Set<Set<String>> par = new HashSet<Set<String>>();
                    par.addAll(results);
                    tagger.paradigmsForTags.put(tag, par);
                } else {
                    paradigms.addAll(results);
                }
            }
            
//            for (Set<String> paradigm : results) {
//                paradigm = Preprocessor.filter(paradigm, "stopwords");
//                if (paradigm == null)
//                    continue;
//                for (String tag : tags) {
//                    Set<Set<String>> paradigms = tagger.paradigmsForTags
//                            .get(tag);
//                    if (paradigms == null) {
//                        Set<Set<String>> par = new HashSet<Set<String>>();
//                        par.add(paradigm);
//                        tagger.paradigmsForTags.put(tag, par);
//                    } else {
//                        paradigms.add(paradigm);
//                    }
//                }
//            }
        }
        try {
            // store the result on disk:
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("index.bin"));
            out.writeObject(tagger.paradigmsForTags);
            System.out.println("WROTE INDEX TO DISK");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param texts
     *            The tagged texts to learn from.
     */
    public void learn(List<Text> texts) {
        this.texts = texts;
        // for (Text text : texts) {
        // tagger.tagsForContent.put(text.content, text.tags
        // .toArray(new String[] {}));
        // System.out.println("Added: " + text.toString());
        // }
        extractParadigms();
    }
}

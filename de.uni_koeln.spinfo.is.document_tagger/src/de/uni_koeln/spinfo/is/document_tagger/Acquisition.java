package de.uni_koeln.spinfo.is.document_tagger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
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
public class Acquisition {
    DocumentTagger tagger;

    private List<Text> texts;

    /**
     * @param tagger
     *            The tagger
     */
    public Acquisition(final DocumentTagger tagger) {
        this.tagger = tagger;
    }

    /**
     * Retrieves all paradigms from the text, storing them in a mapping, mapping
     * tags to paradigms.
     */
    private void extractParadigms() {
        for (Text text : this.texts) {
            Paradigms p = new Paradigms(text.content);
            Set<Set<String>> results = p.pardigmsInText;
            Set<String> tags = text.tags;
            for (Set<String> set : results) {
                this.tagger.paradigmsForTags.put(set, tags);
            }
        }
        try {
            // store the result on disk:
            System.out.print("Writing result of aquisition... ");
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("index.bin"));
            out.writeObject(this.tagger.paradigmsForTags);
            System.out.println("done.");
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
    public void learn(final List<Text> texts) {
        this.texts = texts;
        if (this.tagger.paradigmsForTags == null) {
            this.tagger.paradigmsForTags = new HashMap<Set<String>, Set<String>>();
            extractParadigms();
        }
    }
}

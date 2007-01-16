package de.uni_koeln.spinfo.is.document_tagger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Classifies documents.
 * 
 * @author fsteeg, ssubicin
 * 
 */
public class DocumentTagger {

    /**
     * A mapping of text content (keys) onto tags (values).
     */
    Map<String, String[]> tagsForContent = new HashMap<String, String[]>();

    /**
     * A mapping of tags (keys) onto paradigms (values);
     */
    Map<String, Set<Set<String>>> paradigmsForTags = new HashMap<String, Set<Set<String>>>();

    /**
     * @param texts
     *            The texts to classify.
     */
    public void tag(List<Text> texts) {
        new Classification(this).tag(texts);

    }

    /**
     * @param texts
     *            The texts to learn from
     */
    public void learn(List<Text> texts) {
        new Acquirement(this).learn(texts);
    }

}

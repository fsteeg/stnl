package de.uni_koeln.spinfo.is.document_tagger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    // Map<String, String[]> tagsForContent = new HashMap<String, String[]>();
    /**
     * A mapping of tags (keys) onto paradigms (values);
     */
    Map<Set<String>, Set<String>> paradigmsForTags = null;

    // index of members and their paradigms, eg heine -->
    // [heine,goethe,schiller] //TODO und mehrere paradigmen?
    Map<String, Set<String>> index1 = new HashMap<String, Set<String>>();

    // index of paradigms and their tags, eg [heine,goethe,schiller] -->
    // literature
    Map<Set<String>, String> index2 = new HashMap<Set<String>, String>();

    private List<Text> texts;

    public DocumentTagger(String indexLocation) {
        if (indexLocation != null) {
            // load the index from disk:
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(new FileInputStream(indexLocation));
                paradigmsForTags = (Map<Set<String>, Set<String>>) in
                        .readObject();
                System.out.println("READ INDEX FROM DISK");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * @param texts
     *            The texts to classify.
     */
    public void tag(List<Text> texts) {
        long start = System.currentTimeMillis();
        new Classification(this).tag(texts);
        System.out.println("[PROFILING] Indexing and Tagging took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");

    }

    /**
     * @param texts
     *            The texts to learn from
     */
    public void learn(List<Text> texts) {
        long start = System.currentTimeMillis();
        new Acquirement(this).learn(texts);
        System.out.println("[PROFILING] Learning took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");
    }

}

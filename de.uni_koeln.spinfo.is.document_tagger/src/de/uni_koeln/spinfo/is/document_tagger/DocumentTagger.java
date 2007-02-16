package de.uni_koeln.spinfo.is.document_tagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koeln.spinfo.is.document_tagger.crawling.DeliciousCrawler;

/**
 * Classifies documents.
 * 
 * @author fsteeg, ssubicin
 * 
 */
public class DocumentTagger {

    /**
     * A mapping of tags (keys) onto paradigms (values);
     * 
     * mapping of paradigms and their tags, eg [heine,goethe,schiller] --> //
     * [literature, peotry]
     * 
     */
    Map<Set<String>, Set<String>> paradigmsForTags = null;

    // map of members and their paradigms, eg heine -->
    // [heine,goethe,schiller] //TODO und mehrere paradigmen?
    Map<String, Set<String>> index1 = new HashMap<String, Set<String>>();

    @SuppressWarnings("unchecked")
    public DocumentTagger(final String indexLocation) {
        if (indexLocation != null) {
            // load the index from disk:
            ObjectInputStream in;
            try {
                System.out.print("Reading knowledge from disk... ");
                in = new ObjectInputStream(new FileInputStream(indexLocation));
                this.paradigmsForTags = (Map<Set<String>, Set<String>>) in
                        .readObject();
                System.out.println("done.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param texts
     *            The texts to classify.
     */
    public void tag(final List<Text> texts) {
        long start = System.currentTimeMillis();
        new Classification(this).tag(texts);
        System.out.println("[PROFILING] Indexing and Tagging took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");
    }

    /**
     * @param texts
     *            The texts to learn from
     */
    public void learn(final List<Text> texts) {
        long start = System.currentTimeMillis();
        new Acquisition(this).learn(texts);
        System.out.println("[PROFILING] Learning took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");
    }

    public static void main(String[] args) {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger(null);
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(200);
        List<Text> crawl = deliciousCrawler.crawl(deliciousCrawler.getProperties()
                        .getProperty(DeliciousCrawler.TRAINING_BUNDLE),
                        "delicious.html");
        System.out.println("Crawling done, learning...");
        // List<Text> crawl = deliciousCrawler.crawl("spiegel-korpus");
        tagger.learn(crawl);
        System.out.println("Crawled and learned from a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.tag(deliciousCrawler.crawl(deliciousCrawler.getProperties()
                .getProperty(DeliciousCrawler.TEST_BUNDLE), "delicious.html"));
    }

}

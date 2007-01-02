package de.uni_koeln.spinfo.is.document_tagger;

import java.util.List;

import org.junit.Test;

public class TestDocumentTagger {
    @Test
    public void testTagger() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger();
        List<Text> crawl = new DeliciousCrawler(200).crawl();
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));
        

    }
}

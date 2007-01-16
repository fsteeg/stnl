package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

/**
 * Tests and demos for the document tagger.
 * 
 * @author fsteeg
 * 
 */
public class TestDocumentTagger {
    @Test
    public void testTaggerDeliciousBundle() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger();
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(10);
        List<Text> crawl = deliciousCrawler.crawl("linguistics");
        System.out.println("Crawled a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }

    @Test
    public void testTaggerDelicious() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger();
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(20);
        List<Text> crawl = deliciousCrawler.crawl(null);
        System.out.println("Crawled a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }

    @Test
    public void testTaggerFilesystem() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger();
        FilesystemCrawler crawler = new FilesystemCrawler();
        List<Text> crawl = crawler.crawl();
        System.out.println("Crawled a corpus of " + crawler.wordCount()
                + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }

    @Test
    public void testEvaluation() {
        Evaluation e = new Evaluation(new HashSet<String>(Arrays.asList("java",
                "programming")), new HashSet<String>(Arrays.asList("java",
                "programming", "music")));
        assertEquals(2.0 / 3.0, e.recall());
        assertEquals(2.0 / 2.0, e.precision());
    }
}

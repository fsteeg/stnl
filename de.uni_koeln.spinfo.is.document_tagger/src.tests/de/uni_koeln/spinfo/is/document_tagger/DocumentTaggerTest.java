package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import de.uni_koeln.spinfo.is.document_tagger.DocumentTagger;
import de.uni_koeln.spinfo.is.document_tagger.Evaluation;
import de.uni_koeln.spinfo.is.document_tagger.Text;
import de.uni_koeln.spinfo.is.document_tagger.crawling.DeliciousCrawler;
import de.uni_koeln.spinfo.is.document_tagger.crawling.FilesystemCrawler;

/**
 * Tests and demos for the document tagger.
 * 
 * @author fsteeg
 * 
 */
public class DocumentTaggerTest {
    @Test
    public void testTaggerDeliciousBundle() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger(null);
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(20);
        List<Text> crawl = deliciousCrawler.crawl("linguistics");
        System.out.println("Crawled a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }

    @Test
    public void testTaggerDeliciousBundle2() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger(null);
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(200);
        // List<Text> crawl = deliciousCrawler.crawl("spiegel-korpus");
        tagger.learn(deliciousCrawler.crawl("spiegel-korpus"));
        System.out.println("Crawled and learned from a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.tag(deliciousCrawler.crawl("spiegel-test"));

    }

    @Test
    public void testTaggerDelicious() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger(null);
        long start = System.currentTimeMillis();
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(200);
        List<Text> crawl = deliciousCrawler.crawl(null);
        System.out.println("[PROFILING] Crawling took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");
        System.out.println("Crawled a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        int i = 50;
        List<Text> subList = crawl.subList(0, crawl.size() - i);
        tagger.learn(subList);
        start = System.currentTimeMillis();
        List<Text> subList2 = crawl.subList(crawl.size() - i, crawl.size());
        tagger.tag(subList2);

    }

    @Test
    public void testTaggerDeliciousNoLearning() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger("index.bin-400-posts.bin");
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(30);
        List<Text> crawl = deliciousCrawler.crawl(null);
        tagger.tag(crawl);

        // System.out.println("[PROFILING] Crawling took: "
        // + (System.currentTimeMillis() - start) / 1000 + " sec.");
        // System.out.println("Crawled a corpus of "
        // + deliciousCrawler.wordCount() + " words.");
        // int i = 40;
        // List<Text> subList = crawl.subList(0, crawl.size() - i);
        // tagger.learn(subList);
        // start = System.currentTimeMillis();
        // List<Text> subList2 = crawl.subList(crawl.size() - i, crawl.size());
        // tagger.tag(subList2);

    }

    @Test
    public void testTaggerFilesystem() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger(null);
        FilesystemCrawler crawler = new FilesystemCrawler("ISO-8859-1");
        List<Text> crawl = crawler.crawl();
        System.out.println("Crawled a corpus of " + crawler.wordCount()
                + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }
}

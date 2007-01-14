package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import de.uni_koeln.spinfo.is.document_tagger.DeliciousCrawler;
import de.uni_koeln.spinfo.is.document_tagger.DocumentTagger;
import de.uni_koeln.spinfo.is.document_tagger.Evaluation;
import de.uni_koeln.spinfo.is.document_tagger.FilesystemCrawler;
import de.uni_koeln.spinfo.is.document_tagger.Text;

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
        long start = System.currentTimeMillis();
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(10);
        List<Text> crawl = deliciousCrawler.crawl(null);
        System.out.println("[PROFILING] Crawling took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");
        System.out.println("Crawled a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        start = System.currentTimeMillis();
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        System.out.println("[PROFILING] Learning took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");
        start = System.currentTimeMillis();
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));
        System.out.println("[PROFILING] Tagging took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");

    }

    @Test
    public void testTaggerFilesystem() {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger();
        FilesystemCrawler crawler = new FilesystemCrawler("ISO-8859-1");
        List<Text> crawl = crawler.crawl();
        System.out.println("Crawled a corpus of " + crawler.wordCount()
                + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }
}

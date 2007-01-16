package de.uni_koeln.spinfo.is.document_tagger;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.uni_koeln.spinfo.is.document_tagger.crawling.DeliciousCrawler;

public class DeliciousCrawlerTest {
    private DeliciousCrawler c;

    @Before
    public void init() {
        c = new DeliciousCrawler(6);
    }

    @Test
    public void testCrawl0() {
        c.crawl(null);
    }
    
    @Test
    public void testCrawl1() {
        c.crawl("linguistics");
    }

    @Test
    public void testWordCount() {
        System.out.println(c.wordCount());
    }

    @Test
    public void testReadURL() {
        try {
            System.out.println(c.readURL("http://www.spiegel.de"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

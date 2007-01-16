package de.uni_koeln.spinfo.is.document_tagger;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uni_koeln.spinfo.is.document_tagger.crawling.FilesystemCrawler;

public class FilesystemCrawlerTest {
    FilesystemCrawler c;

    @Before
    public void init() {
        // TODO Ordner angeben, ordner fuer tests mit minimal-texten
        c = new FilesystemCrawler("ISO-8859-1");
    }

    @Test
    public void testCrawl() {
        List<Text> crawl = c.crawl();
        for(Text t : crawl){
            System.out.println("Text: " + t);
        }
    }

    @Test
    public void testWordCount() {
        System.out.println(c.wordCount());
    }

}

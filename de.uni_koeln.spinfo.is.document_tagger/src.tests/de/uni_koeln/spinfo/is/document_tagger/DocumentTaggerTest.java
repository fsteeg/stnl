package de.uni_koeln.spinfo.is.document_tagger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

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
    public void testTaggerDeliciousBundle() throws FileNotFoundException,
            IOException, ClassNotFoundException {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = null;
        tagger = new DocumentTagger("config/tagger.properties");

        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(20, "stnl",
                "sp1nfo");
        List<Text> crawl = deliciousCrawler.crawl("linguistics");
        System.out.println("Crawled a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }
    
    @Test
    public void testTaggerDeliciousBundleFile() throws FileNotFoundException,
            IOException, ClassNotFoundException {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = null;
        tagger = new DocumentTagger("config/tagger.properties");

        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(20, "stnl",
                "sp1nfo");
        List<Text> crawl = deliciousCrawler.crawl("corpus-2", "config/delicious.html");
        save(crawl, "corpus-2.txt");
        System.out.println("Crawled a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }

    private void save(List<Text> crawl, String string) {
		try {
			FileWriter fw = new FileWriter(string);
			for (Text text : crawl) {
				fw.write(new String((text.content + "\n\n").getBytes("utf-8")));
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	@Test
    public void testTaggerDeliciousBundle2() throws FileNotFoundException,
            IOException, ClassNotFoundException {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = null;
        tagger = new DocumentTagger("config/tagger.properties");
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(200, "stnl",
                "sp1nfo");
        // List<Text> crawl = deliciousCrawler.crawl("spiegel-korpus");
        tagger.learn(deliciousCrawler.crawl("spiegel-korpus"));
        System.out.println("Crawled and learned from a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.tag(deliciousCrawler.crawl("spiegel-test"));

    }

    @Test
    public void testTaggerDeliciousBundle3() throws FileNotFoundException,
            IOException, ClassNotFoundException {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = null;
        tagger = new DocumentTagger("config/tagger.properties");
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(200, "stnl",
                "sp1nfo");
        // List<Text> crawl = deliciousCrawler.crawl("spiegel-korpus");
        tagger.learn(deliciousCrawler.crawl(tagger.trainingBundle));
        System.out.println("Crawled and learned from a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        tagger.tag(deliciousCrawler.crawl(tagger.testBundle));
    }

    @Test
    public void testTaggerDelicious() throws FileNotFoundException,
            IOException, ClassNotFoundException {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = null;
        tagger = new DocumentTagger("config/tagger.properties");
        long start = System.currentTimeMillis();
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(220, "stnl",
                "sp1nfo");
        List<Text> crawl = deliciousCrawler.crawl(null);
        System.out.println("[PROFILING] Crawling took: "
                + (System.currentTimeMillis() - start) / 1000 + " sec.");
        System.out.println("Crawled a corpus of "
                + deliciousCrawler.wordCount() + " words.");
        int i = 20;
        List<Text> subList = crawl.subList(0, crawl.size() - i);
        tagger.learn(subList);
        start = System.currentTimeMillis();
        List<Text> subList2 = crawl.subList(crawl.size() - i, crawl.size());
        tagger.tag(subList2);

    }

    @Test
    public void testTaggerDeliciousNoLearning() throws FileNotFoundException,
            IOException, ClassNotFoundException {
        System.out.println("Creating tagger.");
        // prop file anpassen
        DocumentTagger tagger = null;
        tagger = new DocumentTagger("config/tagger.properties");
        DeliciousCrawler deliciousCrawler = new DeliciousCrawler(30, "stnl",
                "sp1nfo");
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
    public void testTaggerFilesystem() throws FileNotFoundException,
            IOException, ClassNotFoundException {
        System.out.println("Creating tagger.");
        DocumentTagger tagger = new DocumentTagger("config/tagger.properties");
        FilesystemCrawler crawler = new FilesystemCrawler("ISO-8859-1");
        List<Text> crawl = crawler.crawl();
        System.out.println("Crawled a corpus of " + crawler.wordCount()
                + " words.");
        tagger.learn(crawl.subList(0, crawl.size() / 2));
        tagger.tag(crawl.subList(crawl.size() / 2, crawl.size()));

    }
}

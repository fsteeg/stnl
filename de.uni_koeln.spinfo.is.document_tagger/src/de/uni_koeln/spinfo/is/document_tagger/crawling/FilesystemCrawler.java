package de.uni_koeln.spinfo.is.document_tagger.crawling;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import de.uni_koeln.spinfo.is.document_tagger.Text;
import de.uni_koeln.spinfo.strings.algo.Util;

/**
 * Reads texts from the filesystem from a given directory. TODO: recurse into
 * subdirectories
 * 
 * @author fsteeg, ssubicin
 * 
 */
public class FilesystemCrawler {

    private int count = 0;

    private String encoding = "UTF-8";

    public FilesystemCrawler(String string) {
        this.encoding = string;
    }

    public FilesystemCrawler() {
    }

    /**
     * @return Returns a list of texts, the corpus to be used.
     */
    public List<Text> crawl() {
        File dir = new File("texte/tagged/");
        String[] files = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });
        List<Text> texts = new Vector<Text>();
        for (String string : files) {
            String filename = dir.getAbsolutePath() + File.separator + string;
            String all = Util.getText(new File(filename), encoding);
            // the tags are seperated from the conten by a "#"
            String[] tags = all.substring(0, all.indexOf('#')).split(" ");
            String content = all.substring(all.indexOf('#') + 1).trim();
            Text text = new Text(content, new HashSet<String>(Arrays
                    .asList(tags)), filename);
            texts.add(text);
            count += content.split(" ").length;

        }
        return texts;
    }

    /**
     * @return Returns the number of words crawled by this crawler.
     */
    public int wordCount() {
        return count;
    }
}

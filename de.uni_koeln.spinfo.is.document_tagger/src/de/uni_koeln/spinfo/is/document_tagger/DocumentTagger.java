package de.uni_koeln.spinfo.is.document_tagger;

import java.awt.Component;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import de.uni_koeln.spinfo.is.document_tagger.crawling.DeliciousCrawler;
import del.icio.us.DeliciousException;

/**
 * Classifies documents.
 * 
 * @author fsteeg, ssubicin
 * 
 */
public class DocumentTagger {

    // Constants for keys in the used Java properties file:

    private static final String USE_BIN_INDEX = "use_bin_index";

    private static final String BIN_INDEX_LOCATION = "bin_index";

    private static final String TRAINING_BUNDLE = "training_bundle";

    private static final String TEST_BUNDLE = "test_bundle";

    private static final String USE_INPUT_FILE = "use_input_file";

    private static String INPUT_FILE = "input_file";

    private static String OUTPUT_FILE = "output_file";

    private static String LOGIN = "login";

    private static String PASS = "pass";

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

    private Properties properties;

    private static String login;

    private static String pass;

    private static String in;

    private static String out;

    static String trainingBundle;

    static String testBundle;

    private static boolean useInputFile;

    String binIndexLocation;

    @SuppressWarnings("unchecked")
    public DocumentTagger(String propertiesFileLocation)
            throws FileNotFoundException, IOException, ClassNotFoundException {

        if (propertiesFileLocation == null)
            throw new IllegalArgumentException(
                    "Properties file must be supplied!");
        properties = new Properties();
        properties.load(new FileInputStream(propertiesFileLocation));
        in = properties.getProperty(INPUT_FILE);
        out = properties.getProperty(OUTPUT_FILE);
        trainingBundle = properties.getProperty(TRAINING_BUNDLE);
        testBundle = properties.getProperty(TEST_BUNDLE);
        String p = properties.getProperty(USE_INPUT_FILE);
        useInputFile = p != null && p.equals("yes");
        login = properties.getProperty(LOGIN);
        pass = properties.getProperty(PASS);
        binIndexLocation = properties.getProperty(BIN_INDEX_LOCATION);

        String property = properties.getProperty(USE_BIN_INDEX);
        if (property != null && property.equals("yes")) {
            // load the index from disk:
            ObjectInputStream in;
            System.out.print("Reading knowledge from disk... ");
            String loc = properties.getProperty(BIN_INDEX_LOCATION);
            in = new ObjectInputStream(new FileInputStream(loc));
            this.paradigmsForTags = (Map<Set<String>, Set<String>>) in
                    .readObject();
            System.out.println("done.");
        }
    }

    /**
     * @param texts
     *            The texts to classify.
     */
    public void tag(final List<Text> texts) {
        long start = System.currentTimeMillis();
        new Classification(this).tag(texts, out);
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
        JPanel panel = new JPanel();
        DocumentTagger tagger = null;
        try {
            tagger = new DocumentTagger(args.length == 1 ? args[0]
                    : "config/tagger.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showError(panel, e, "Can't find properties file");
        } catch (IOException e) {
            e.printStackTrace();
            showError(panel, e, "Can't open properties file");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            showError(panel, e, "Can't deserialize object");
        }
        JFrame f = new JFrame();
        f.setSize(500, 80);
        f.setContentPane(panel);
        JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0,
                100) {
            public Dimension getPreferredSize() {
                return new Dimension(300, super.getPreferredSize().height);
            }
        };
        progressBar.setVisible(true);
        panel.add(progressBar);
        String string = "Step 1/4: Crawling training corpus from links, source: '";
        String string2 = "Step 3/4: Retrieving paradigms from crawled corpus... ";
        String string3 = "Step 2/4: Crawling test corpus from links in file '";
        String string4 = "Step 4/4: Tagging texts from crawled test corpus...";
        String string5 = "Test run done. Result written to '";
        JLabel label = new JLabel();
        panel.add(label);
        f.setVisible(true);
        progressBar.setValue(10);
        DeliciousCrawler deliciousCrawler = null;
        List<Text> crawl = null;
        List<Text> crawl2 = null;
        /** We are parsing links from a lokal file: */
        if (useInputFile) {
            label.setText(string + in + "'");
            deliciousCrawler = new DeliciousCrawler(200, login, pass);
            try {
                crawl = deliciousCrawler.crawl(trainingBundle, in);
                progressBar.setValue(30);
                label.setText(string3 + in + "'");
                crawl2 = deliciousCrawler.crawl(testBundle, in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showError(panel, e, "Could not crawl corpus from '" + in + "'");
                System.exit(-1);
            }
        }
        /** We are parsing links directly from Delicious: */
        else {
            try {
                label.setText(string + login + "/" + pass + "'");
                deliciousCrawler = new DeliciousCrawler(200, login, pass);
                crawl = deliciousCrawler.crawl(trainingBundle);
                progressBar.setValue(30);
                label.setText(string3 + in + "'");
                crawl2 = deliciousCrawler.crawl(testBundle);
            } catch (DeliciousException x) {
                x.printStackTrace();
                showError(
                        panel,
                        x,
                        "This is probably a problem with the Delicious API.\nTry crawling from exported file (set in tagger.properties)");
                System.exit(-1);
            }
        }
        progressBar.setValue(50);
        label.setText(string2);
        tagger.learn(crawl);
        progressBar.setValue(80);
        label.setText(string4);
        tagger.tag(crawl2);
        label.setText(string5 + out + "'");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressBar.setValue(100);
    }

    private static void showError(Component parent, Exception e, String s) {
        JOptionPane.showMessageDialog(parent,
                "Error while running the tagger: " + e.getMessage() + "\n" + s);
        System.exit(-1);
    }

}

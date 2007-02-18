package de.uni_koeln.spinfo.is.document_tagger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

    private static final String INPUT_FILE = "input_file";

    private static final String OUTPUT_FILE = "output_file";

    private static final String LOGIN = "login";

    private static final String PASS = "pass";

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

    Properties properties;

    String login;

    String pass;

    String in;

    String out;

    String trainingBundle;

    String testBundle;

    boolean useInputFile;

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
}

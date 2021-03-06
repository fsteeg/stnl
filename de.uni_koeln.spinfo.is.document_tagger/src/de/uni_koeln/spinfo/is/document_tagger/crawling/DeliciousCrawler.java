package de.uni_koeln.spinfo.is.document_tagger.crawling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uni_koeln.spinfo.is.document_tagger.Preprocessor;
import de.uni_koeln.spinfo.is.document_tagger.Text;
import del.icio.us.Delicious;
import del.icio.us.DeliciousException;
import del.icio.us.beans.Bundle;
import del.icio.us.beans.Post;

/**
 * A crawler that assembles a corpus using delicious bookmarks
 * (http://del.icio.us).
 * 
 * @author fsteeg
 * 
 */
public class DeliciousCrawler {
    private static String login = null;

    private static String pass = null;

    // public static final String TRAINING_BUNDLE = "training_bundle";
    //
    // public static final String TEST_BUNDLE = "test_bundle";

    private int limit;

    private int wordCount;

    // Properties props;

    /**
     * @param i
     *            The max number of posts to get.
     */
    // public DeliciousCrawler(int i) {
    // limit = i;
    // // props = new Properties();
    // // try {
    // // props.load(new FileInputStream("config/delicious.properties"));
    // // } catch (FileNotFoundException e) {
    // // System.err.println("Missing a \"delicious.properties\" file!");
    // // e.printStackTrace();
    // // } catch (IOException e) {
    // // e.printStackTrace();
    // // }
    //
    // }
    public DeliciousCrawler(int i, String login, String pass) {
        // this(i);
        limit = i;
        this.login = login;
        this.pass = pass;
    }

    /**
     * @param bundle
     *            The bundle to retrieve texts for; pass null for all posts.
     * @return Returns a list of texts, the created corpus.
     */
    public List<Text> crawl(String bundle) {
        List<Post> list = getPostsFromDelicious(bundle);
        List<Text> texts = readContentFromPosts(list);
        return texts;
    }

    public List<Text> crawl(String bundle, String file)
            throws FileNotFoundException {
        List<Text> texts = readContentFromFile(bundle, file);
        return texts;
    }

    private List<Text> readContentFromFile(String bundle, String file)
            throws FileNotFoundException {
        System.out.println("Using file: " + file);
        List<Text> texts = new Vector<Text>();
        Scanner scanner = new Scanner(new FileReader(file));
        int c = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            Matcher m = Pattern.compile(
                    ".*?HREF=\"(http://[^\"]+)\".*?TAGS=\"([^\"]+)\".*?")
                    .matcher(line);
            if (m.matches()) {
                String url = m.group(1);
                // System.out.println("URL: " + url);
                String allTags = m.group(2);
                if (!allTags.startsWith(bundle))
                    continue;
                c++;
                // System.out.println("TAGS: " + allTags);
                Set<String> tags = new HashSet<String>(Arrays.asList(allTags
                        .split(",")));
                String clean = new Preprocessor(url).clean();
                if (!clean.trim().equals(""))
                    texts.add(new Text(clean, tags, url));
            }
        }
        System.out.println("Bundle " + bundle + ": " + c);

        return texts;
    }

    /**
     * Gets posts from delicious.
     * 
     * @param bundle
     *            The bundle to get posts for.
     * @return Returns a list of deliciois posts: the selected post to create
     *         the corpus from.
     */
    @SuppressWarnings("unchecked")
    // delicious-api
    private List<Post> getPostsFromDelicious(String bundle) {
        String property = login;
        String property2 = pass;
        System.out.println("Using account: " + property + "/" + property2);
        Delicious delicious = new Delicious(property, property2);
        List<Post> list = new Vector<Post>();
        if (bundle == null) {
            list = delicious.getAllPosts();
        } else {
            List<Bundle> bundles = delicious.getBundles();

            for (Bundle b : bundles) {
                System.out.println("Bundle: " + b);
                String name = b.getName();
                System.out.println("Comparing: " + name + " and " + bundle);
                if (name.equalsIgnoreCase(bundle)) {
                    String[] split = b.getTags().split(" ");
                    List postsForTags = delicious.getPostsForTags(split);
                    list.addAll(postsForTags);
                }
            }
        }
        return list;
    }

    /**
     * Parses the sites specified by the delicious posts using nekohtml, getting
     * all text within p-tags.
     * 
     * @param list
     *            The list of delicious post to be used for creating the corpus.
     * @return Returns a list of texts: the resulting corpus.
     */
    private List<Text> readContentFromPosts(List<Post> list) {
        List<Text> texts = new Vector<Text>();
        int i = 0;
        for (Post post : list) {
            if (i == limit)
                break;
            System.out.println("Processing post " + (i + 1) + " of "
                    + Math.min(limit, list.size()) + " (total " + list.size()
                    + "): " + post.getHref());
            Set<String> tags = new HashSet<String>(Arrays.asList(post
                    .getTagsAsArray(" ")));
            String clean = new Preprocessor(post.getHref()).clean();
            if (!clean.trim().equals(""))
                texts.add(new Text(clean, tags, post.getHref()));

            i++;
            // TODO add a flag for such stuff, to increase performance
            wordCount = wordCount + clean.split(" ").length;
        }
        return texts;
    }

    public int wordCount() {
        return wordCount;
    }

    /**
     * NOTE: Not currently used anywhere, parsing is done using nekohtml.
     * 
     * @param adress
     *            The adress to read the content from
     * @return Returns the html content of the website at the given adress
     * @throws IOException
     */
    public String readURL(String adress) throws IOException {
        URL url = new URL(adress);
        BufferedReader urlReader = new BufferedReader(new InputStreamReader(url
                .openStream()));
        String line;
        StringBuilder res = new StringBuilder();
        while ((line = urlReader.readLine()) != null) {
            res.append(line);
        }
        return res.toString();
    }

    // public Properties getProperties() {
    // return props;
    // }
}

package de.uni_koeln.spinfo.is.document_tagger.crawling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import de.uni_koeln.spinfo.is.document_tagger.Preprocessor;
import de.uni_koeln.spinfo.is.document_tagger.Text;
import del.icio.us.Delicious;
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
    private int limit;

    private int wordCount;

    /**
     * @param i
     *            The max number of posts to get.
     */
    public DeliciousCrawler(int i) {
        limit = i;
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
        Delicious delicious = new Delicious("fsteeg", "steegf");
        List<Post> list = new Vector<Post>();
        if (bundle == null) {
            list = delicious.getAllPosts();
        } else {
            List<Bundle> bundles = delicious.getBundles();

            for (Bundle b : bundles) {
                System.out.println("Bundle: " + b);
                if (b.getName().equalsIgnoreCase(bundle))
                    list.addAll(delicious.getPostsForTags(b.getTags()
                            .split(" ")));
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
}

package de.uni_koeln.spinfo.is.document_tagger;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import del.icio.us.Delicious;
import del.icio.us.beans.Post;

public class DeliciousCrawler {
    private Post last;

    private int limit;

    private String tag;

    public DeliciousCrawler(int i) {
        limit = i;
        this.tag = tag;
    }

    public List<Text> crawl() {
        List<Text> texts = new Vector<Text>();
        Delicious delicious = new Delicious("fsteeg", ".steegfa.");
        List<Post> list = delicious.getAllPosts();
        // List<Bundle> bundles = delicious.getBundles();
        // for(Bundle b : bundles){
        // System.out.println("Bundle: " + b);
        // }
        int i = 0;
        for (Post post : list) {
            if (i == limit)
                break;
            System.out.println("Processing post " + i + " of " + limit
                    + " (total " + list.size() + "): " + post.getHref());
            // System.out.println(i + " " + post);
            // System.out.println(post.getDescription());
            // System.out.println(post.getExtended());
            List<String> tags = Arrays.asList(post.getTagsAsArray(" "));
            String clean = new Preprocessor(post.getHref()).clean();
            // for (String s : tags) {
            // System.out.println("TAG: " + s);
            // }
            if (!clean.trim().equals(""))
                texts.add(new Text(clean, tags, post.getHref()));

            i++;
            // last = post;
        }
        return texts;
    }

    // public String readURL(String adress) throws IOException {
    // URL url = new URL(adress);
    // BufferedReader urlReader = new BufferedReader(new InputStreamReader(url
    // .openStream()));
    // String line;
    // StringBuilder res = new StringBuilder();
    // while ((line = urlReader.readLine()) != null) {
    // res.append(line);
    // }
    // return res.toString();
    // }
    // public static void main(String[] args) {
    // Delicious delicious = new Delicious("fsteeg", ".steegfa.");
    // List<Post> list = delicious.getAllPosts();
    // int i = 0;
    // Post last = null;
    // for (Post post : list) {
    // System.out.println(i + " " + post);
    // System.out.println(post.getDescription());
    // System.out.println(post.getExtended());
    // String[] tags = post.getTagsAsArray(" ");
    // for (int j = 0; j < tags.length; j++) {
    // System.out.println("TAG: " + tags[j]);
    // }
    // i++;
    // last = post;
    // }
    // }

    // @Test
    // public void testCrawling() {
    // DeliciousCrawler crawler = new DeliciousCrawler();
    // crawler.crawl();
    // }
    // @Test
    // public void testURLOpening(){
    // DeliciousCrawler main = new DeliciousCrawler();
    // try {
    // System.out.println(main.readURL(last.getHref()));
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
}

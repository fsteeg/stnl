package de.uni_koeln.spinfo.is.document_tagger;

import java.util.HashSet;
import java.util.Set;

/**
 * A class representing a text, consisting of content and a set of tags.
 * 
 * @author fsteeg
 * 
 */
public class Text {
    /**
     * The tags for a text, the classification.
     */
    Set<String> tags = new HashSet<String>();
    /**
     * The actual content of a text.
     */
    String content = "";

    /**
     * The original location of the text: a web adress, file location or such.
     */
    String location;

    /**
     * @param content
     *            The actual content of the text.
     * @param tags
     *            The tags for the text, it's classification.
     * @param location
     *            The original location, where the text is from, a web adress,
     *            file system location or such
     */
    public Text(String content, Set<String> tags, String location) {
        this.content = content;
        this.tags = tags;
        this.location = location;
    }

    /**
     * Creates a string from the tags and the content.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String s : tags) {
            builder.append("," + s);
        }
        return builder.substring(1) + "\n\n" + content + "\n\n";
    }
}

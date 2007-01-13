package de.uni_koeln.spinfo.is.document_tagger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLParagraphElement;
import org.xml.sax.SAXException;

import de.uni_koeln.spinfo.strings.algo.Util;

/**
 * A preprocessor for raw html. Parses the html using nekohtml, a correcting
 * html parser. Adds all text in paragraph-tags to the corpus. This class also
 * provides a static class for stopwords-based filtering.
 * 
 * @author fsteeg
 * 
 */
public class Preprocessor {

    private String url;

    /**
     * @param content
     *            The content to be preprocessed.
     */
    public Preprocessor(String content) {
        this.url = content;
    }

    /**
     * @return Returns the text in paragraph-tags in the html.
     */
    public String clean() {
        DOMParser parser = new DOMParser();
        try {
            parser.parse(url);
        } catch (SAXException e) {
            System.out.println("Catching SAXException...");
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            System.out.println("Catching IOException...");
            e.printStackTrace();
            return "";
        } catch (RuntimeException e) {
            System.out.println("Catching RuntimeException...");
            e.printStackTrace();
            return "";
        }
        return content(parser.getDocument());
    }

    /**
     * @param node
     *            The DOM node
     * @return Returns the content of the node if it is a paragraph node.
     */
    public String content(Node node) {
        StringBuilder builder = new StringBuilder();
        if (node instanceof HTMLParagraphElement) {
            builder.append(node.getTextContent().trim().replaceAll("[ \t\n]+",
                    " "));
        }
        Node child = node.getFirstChild();
        while (child != null) {
            builder.append(content(child));
            child = child.getNextSibling();
        }
        return builder.toString();
    }

    /**
     * @param paradigm
     *            The paradigm to free from stopwords.
     * @param location
     *            The full location of the stopword file.
     * @return Returns the paradigms sans stopwords.
     */
    static Set<String> filter(Set<String> paradigm, String location) {
        String stopwords = Util.getText(new File(location));
        for (String stopword : stopwords.split(" ")) {
            Set<String> cleaned = new HashSet<String>();
            for (String m : paradigm) {
                if (!m.equalsIgnoreCase(stopword))
                    cleaned.add(m);
            }
            paradigm = cleaned;
        }
        if (paradigm.size() <= 1)
            return null;
        return paradigm;
    }

}

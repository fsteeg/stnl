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
     * @param url
     *            The content to be preprocessed.
     */
    public Preprocessor(final String url) {
        this.url = url;
    }

    /**
     * @return Returns the text in paragraph-tags in the html.
     */
    public String clean() {
        DOMParser parser = new DOMParser();
        try {
            parser.parse(this.url);
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
    public String content(final Node node) {
        // System.out.println("Node: " + node.getClass());
        StringBuilder builder = new StringBuilder();
        if (node instanceof HTMLParagraphElement /*
                                                     * || node instanceof
                                                     * TextImpl
                                                     */) {
            String textContent = node.getTextContent();
            String replaceAll = textContent.trim().replaceAll("[ \t\n]+", " ");
            builder.append(replaceAll);
        }
        Node child = node.getFirstChild();
        while (child != null) {
            builder.append(content(child));
            child = child.getNextSibling();
        }
        return (builder.toString() + " ").replaceAll("[ \t\n]+", " ");
    }

    /**
     * @param paradigm
     *            The paradigm to free from stopwords.
     * @param location
     *            The full location of the stopword file.
     * @return Returns the paradigms sans stopwords.
     */
    public static Set<String> filter(Set<String> paradigm, final String location) {
        String stopwords = Util.getText(new File(location), "utf-8");
        for (String stopword : stopwords.split(" ")) {
            Set<String> cleaned = new HashSet<String>();
            for (String m : paradigm) {
                if (!m.equalsIgnoreCase(stopword) && !m.trim().equals("")
                        && (m.trim().length() != 1) && m.matches(".*[a-zA-Z].*")) {
                    cleaned.add(m);
                }
            }
            paradigm = cleaned;
        }
        if (paradigm.size() <= 1) {
            return null;
        }
        return paradigm;
    }

}

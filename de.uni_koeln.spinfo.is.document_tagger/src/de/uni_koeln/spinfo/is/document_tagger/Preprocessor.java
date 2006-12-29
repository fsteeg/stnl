package de.uni_koeln.spinfo.is.document_tagger;

import java.io.IOException;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLParagraphElement;
import org.xml.sax.SAXException;

public class Preprocessor {

    private String url;

    public Preprocessor(String content) {
        this.url = content;
    }

    public String clean() {
        DOMParser parser = new DOMParser();
        // System.out.println("Parsing: " + url);
        try {
            parser.parse(url);
        } catch (SAXException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            System.out.println("Catching...");
            e.printStackTrace();
            return "";
        } catch (RuntimeException e) {
            System.out.println("Catching...");
            e.printStackTrace();
            return "";
        }
        return print(parser.getDocument(), "");
    }

    public String print(Node node, String indent) {
        StringBuilder builder = new StringBuilder();
        if (node instanceof HTMLParagraphElement) {
            // System.out.println(indent + node.getClass().getName());
            builder.append(node.getTextContent().trim().replaceAll("[ \t\n]+",
                    " "));
            // System.out.println("Content: " + node.getTextContent());
        }
        Node child = node.getFirstChild();
        while (child != null) {
            builder.append(print(child, indent + " "));
            child = child.getNextSibling();
        }
        return builder.toString();
    }

}

package de.uni_koeln.spinfo.is.document_tagger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.Paradigms;
import de.uni_koeln.spinfo.strings.algo.Util;

public class DocumentTagger {

    Map<String, String[]> map = new HashMap<String, String[]>();

    Map<String, Set<Set<String>>> paradigmsForTags = new HashMap<String, Set<Set<String>>>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        DocumentTagger documentTagger = new DocumentTagger();
        new Acquirement(documentTagger).createTaggedCorpus();
        new Classification(documentTagger).learnNewTags();
    }

    Set<String> filter(Set<String> paradigm, String loc) {
        String stopwords = Util.getText(new File(loc));
        for (String stopword : stopwords.split(" ")) {
            Set<String> cleaned = new HashSet<String>(paradigm);
            for (String m : paradigm) {
                if (m.equalsIgnoreCase(stopword))
                    cleaned.remove(stopword);
            }
            paradigm = cleaned;
        }
        if (paradigm.size() <= 1)
            return null;
        return paradigm;
    }

    public void tag(List<Text> name) {
        new Classification(this).tag(name);

    }

    public void learn(List<Text> name) {
        new Acquirement(this).learn(name);
    }

}

package de.uni_koeln.spinfo.is.document_tagger;

import java.io.File;
import java.io.FilenameFilter;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.Paradigms;
import de.uni_koeln.spinfo.strings.algo.Util;

public class Classification {
    private DocumentTagger tagger;

    public Classification(DocumentTagger tagger) {
        this.tagger = tagger;
    }

    public void tag(List<Text> name) {
        for (Text t : name) {
            String content = t.content;
            Paradigms p = new Paradigms(content);
            Set<Set<String>> results = p.pardigmsInText;
            Set<String> newTags = new HashSet<String>();
            for (String tag : tagger.paradigmsForTags.keySet()) {
                Set<Set<String>> newPar = tagger.paradigmsForTags.get(tag);
                for (Set<String> set : newPar) {
                    set = tagger.filter(set, "stopwords");
                    if (set == null)
                        continue;
                    for (Set<String> r : results) {
                        if (newTags.contains(tag))
                            continue;
                        int counted = 0;
                        int threshold = 4;
                        for (String s : set) {
                            // System.out.println("Trying " + s);
                            // System.out.println("Treffer fuer: " + s
                            // + ", counted: " + counted);
                            if (r.contains(s)) {
                                counted++;
                            }
                        }
                        if (counted >= threshold) {
                            // TODO called too often
                            // System.out.println("Adding: " + tag
                            // + " with count " + counted);
                            newTags.add(tag);
                        }
                    }
                }
            }
            // System.out
            // .println("________________________________________________________");
            // System.out.println("New Tags for " + t.location);
            // for (String s : newTags) {
            // System.out.println(s);
            // }
            // System.out
            // .println("--------------------------------------------------------");
            // System.out.println("Original Tags for " + t.location);
            // for (String s : t.tags) {
            // System.out.println(s);
            // }
            Evaluation e = new Evaluation(newTags, t.tags);
            System.out.println("Evaluation; Recall: "
                    + NumberFormat.getNumberInstance().format(e.recall())
                    + ", Precision: "
                    + NumberFormat.getNumberInstance().format(e.precision())
                    + ", F-Value: "
                    + NumberFormat.getNumberInstance().format(e.fValue()));
        }

    }

    void learnNewTags() {
        File dir = new File("texte/raw/");
        String[] files = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });
        for (String filename : files) {
            String absoluteFilename = dir.getAbsolutePath() + File.separator
                    + filename;
            System.out.println("Analysing: " + filename);
            String content = Util.getText(new File(absoluteFilename));
            Paradigms p = new Paradigms(content);
            Set<Set<String>> results = p.pardigmsInText;
            Set<String> newTags = new HashSet<String>();
            for (String tag : tagger.paradigmsForTags.keySet()) {
                Set<Set<String>> newPar = tagger.paradigmsForTags.get(tag);
                for (Set<String> set : newPar) {
                    set = tagger.filter(set, "stopwoerter.txt");
                    if (set == null)
                        continue;
                    if (results.contains(set)) {
                        newTags.add(tag);
                        System.out.println("Treffer fuer: ");
                        for (String string : set) {
                            System.out.println(string);
                        }
                    }
                }
            }
            System.out.println("New Tags: ");
            for (String string2 : newTags) {
                System.out.println(string2);
            }
        }
    }
}

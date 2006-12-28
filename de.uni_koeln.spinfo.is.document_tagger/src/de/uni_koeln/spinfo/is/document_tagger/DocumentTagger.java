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

    private Map<String, String[]> map = new HashMap<String, String[]>();

    private Map<String, Set<Set<String>>> paradigmsForTags = new HashMap<String, Set<Set<String>>>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        DocumentTagger documentTagger = new DocumentTagger();
        documentTagger.createTaggedCorpus();
        documentTagger.extractParadigms();
        documentTagger.learnNewTags();
    }

    private void extractParadigms() {
        for (String text : map.keySet()) {
            Paradigms p = new Paradigms(text);
            Set<Set<String>> results = p.pardigmsInText;
            // System.out.println();
            // System.out.println("-------------------------------------");
            String[] tags = map.get(text);
            // System.out.print("Paradigms for text with tags: ");
            // for (String string : tags) {
            // System.out.print(string+" ");
            // }

            System.out.println();

            for (Set<String> paradigm : results) {

                paradigm = filter(paradigm, "stopwords");
                if (paradigm == null)
                    continue;
                for (String string : tags) {
                    Set<Set<String>> paradigms = paradigmsForTags.get(string);
                    if (paradigms == null) {
                        Set<Set<String>> par = new HashSet<Set<String>>();
                        par.add(paradigm);
                        paradigmsForTags.put(string, par);
                    } else {
                        paradigms.add(paradigm);
                    }
                }
                // System.out.println("Paradigm:");
                // for (String string : paradigm) {
                // System.out.println(string);
                // }

            }
            System.out.println();
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("index.bin"));
                out.writeObject(paradigmsForTags);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }

    private Set<String> filter(Set<String> paradigm, String loc) {
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

    private void createTaggedCorpus() {
        File dir = new File("texte/tagged/");
        String[] files = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });
        for (String string : files) {
            String filename = dir.getAbsolutePath() + File.separator + string;
            // System.out.println(filename);
            String content = Util.getText(new File(filename));
            String[] tags = content.substring(0, content.indexOf('#')).split(
                    " ");
            String text = content.substring(content.indexOf('#') + 1).trim();
            map.put(text, tags);
            // System.out.println(content);

        }
    }

    private void learnNewTags() {
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
            for (String tag : paradigmsForTags.keySet()) {
                Set<Set<String>> newPar = paradigmsForTags.get(tag);
                for (Set<String> set : newPar) {
                    set = filter(set, "stopwoerter.txt");
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

    public void learn(List<Text> texts) {
        for (Text text : texts) {
            map.put(text.content, text.tags.toArray(new String[] {}));
            System.out.println("Added: " + text.toString());
        }
        extractParadigms();

    }

    public void tag(List<Text> name) {
        for (Text t : name) {

            String content = t.content;
            Paradigms p = new Paradigms(content);
            Set<Set<String>> results = p.pardigmsInText;
            Set<String> newTags = new HashSet<String>();
            for (String tag : paradigmsForTags.keySet()) {
                Set<Set<String>> newPar = paradigmsForTags.get(tag);
                for (Set<String> set : newPar) {
                    set = filter(set, "stopwords");
                    if (set == null)
                        continue;
                    for (Set<String> r : results) {
                        if(newTags.contains(tag))
                            continue;
                        int counted = 0;
                        int threshold = 4;
                        for (String s : set) {
                            // System.out.println("Trying " + s);
//                            System.out.println("Treffer fuer: " + s
//                                    + ", counted: " + counted);
                            if (r.contains(s)) {
                                counted++;
                            }
                        }
                        if (counted >= threshold){
                            //TODO called too often
                            System.out.println("Adding: " + tag + " with count " + counted);
                            newTags.add(tag);
                        }
                    }
                }
            }
            System.out.println("________________________________________________________");
            System.out.println("New Tags for " + t.location);
            for (String s : newTags) {
                System.out.println(s);
            }
            System.out.println("--------------------------------------------------------");
            System.out.println("Original Tags for " + t.location);
            for (String s : t.tags) {
                System.out.println(s);
            }
        }

    }

}

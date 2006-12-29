package de.uni_koeln.spinfo.is.document_tagger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.Paradigms;
import de.uni_koeln.spinfo.strings.algo.Util;

public class Acquirement {
    private DocumentTagger tagger;

    public Acquirement(DocumentTagger tagger) {
        this.tagger = tagger;
    }

    private void extractParadigms() {
        for (String text : tagger.map.keySet()) {
            Paradigms p = new Paradigms(text);
            Set<Set<String>> results = p.pardigmsInText;
            // System.out.println();
            // System.out.println("-------------------------------------");
            String[] tags = tagger.map.get(text);
            // System.out.print("Paradigms for text with tags: ");
            // for (String string : tags) {
            // System.out.print(string+" ");
            // }

            System.out.println();

            for (Set<String> paradigm : results) {

                paradigm = tagger.filter(paradigm, "stopwords");
                if (paradigm == null)
                    continue;
                for (String string : tags) {
                    Set<Set<String>> paradigms = tagger.paradigmsForTags
                            .get(string);
                    if (paradigms == null) {
                        Set<Set<String>> par = new HashSet<Set<String>>();
                        par.add(paradigm);
                        tagger.paradigmsForTags.put(string, par);
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
                ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream("index.bin"));
                out.writeObject(tagger.paradigmsForTags);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void learn(List<Text> texts) {
        for (Text text : texts) {
            tagger.map.put(text.content, text.tags.toArray(new String[] {}));
            System.out.println("Added: " + text.toString());
        }
        extractParadigms();

    }
    
    void createTaggedCorpus() {
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
            tagger.map.put(text, tags);
            // System.out.println(content);

        }
        extractParadigms();
    }
}

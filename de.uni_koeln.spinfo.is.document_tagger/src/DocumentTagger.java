import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_koeln.spinfo.strings.algo.Paradigms;
import de.uni_koeln.spinfo.strings.algo.Util;


public class DocumentTagger {

	private Map<String,String[]> map = new HashMap<String,String[]>();
	private Map<String,Set<Set<String>>> paradigmsForTags = new HashMap<String,Set<Set<String>>>();
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
//			System.out.println();
//			System.out.println("-------------------------------------");
			String[] tags = map.get(text);
//			System.out.print("Paradigms for text with tags: ");
//			for (String string : tags) {
//				System.out.print(string+" ");
//			}
			
			System.out.println();
			
			for (Set<String> paradigm : results) {
				
				paradigm = filter(paradigm);
				if(paradigm==null)
					continue;
				for (String string : tags) {
					Set<Set<String>> paradigms = paradigmsForTags.get(string);
					if(paradigms == null){
						Set<Set<String>> par = new HashSet<Set<String>>();
						par.add(paradigm);
						paradigmsForTags.put(string, par);
					}
					else{
						paradigms.add(paradigm);
					}
				}
//				System.out.println("Paradigm:");
//				for (String string : paradigm) {
//					System.out.println(string);
//				}

			}
			System.out.println();
		}
	}

	private Set<String> filter(Set<String> paradigm) {
		String stopwords = Util.getText(new File("stopwoerter.txt"));
		for (String stopword : stopwords.split(" ")) {
			if(paradigm.contains(stopword)){
				paradigm.remove(stopword);
			}
		}
		if(paradigm.size()<=1)
			return null;
		return paradigm;
	}

	private void createTaggedCorpus() {
		File dir = new File("texte/tagged/");
		String[]files = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});
		for (String string : files) {
			String filename = dir.getAbsolutePath() + File.separator + string;
//			System.out.println(filename);
			String content = Util.getText(new File(filename));
			String[] tags = content.substring(0,content.indexOf('#')).split(" ");
			String text = content.substring(content.indexOf('#')+1).trim();
			map.put(text,tags);
//			System.out.println(content);
			
		}
	}
	private void learnNewTags() {
		File dir = new File("texte/raw/");
		String[]files = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});
		for (String filename : files) {
			String absoluteFilename = dir.getAbsolutePath() + File.separator + filename;
			System.out.println("Analysing: " + filename);
			String content = Util.getText(new File(absoluteFilename));
			Paradigms p = new Paradigms(content);
			Set<Set<String>> results = p.pardigmsInText;
			Set<String> newTags = new HashSet<String>();
			for (String tag : paradigmsForTags.keySet()) {
				Set<Set<String>> newPar = paradigmsForTags.get(tag);
				for (Set<String> set : newPar) {
					set = filter(set);
					if(set==null)
						continue;
					if(results.contains(set)){
						newTags.add(tag);
						System.out.println("Treffer für: ");
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

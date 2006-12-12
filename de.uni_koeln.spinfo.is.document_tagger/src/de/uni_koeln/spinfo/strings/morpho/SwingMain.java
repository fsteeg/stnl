///////////////////////////////////////
/// Projekt: Morphologische Analyse 
/// Klasse: Main.java		
/// Paket: meinPaket
/// Autor: Eva Hasler				
/// Datum: 8.4.06					
/// Weitere Pakete: BioJava,		
///			suffixComparator,		
///			wordCount				
///////////////////////////////////////

package de.uni_koeln.spinfo.strings.morpho;

import javax.swing.JOptionPane;
//TODO translate comments, variable names, gui
public class SwingMain{

	public static void main(String[] args) {
		// Auswahldialog
	    String options[] = { "Suffixe", "Praefixe", "Suffixe und Praefixe", "Suffixe(Ukkonen)" };
	    String baumArt = (String) JOptionPane.showInputDialog( null, "Was soll ermittelt werden?",
	    							"Morpholog. Analyse", JOptionPane.QUESTION_MESSAGE, null, options, options[0] );
		if(baumArt.equals("Suffixe")){
			SwingSuffixes.meinFenster = new SwingSuffixes("Kompakter Trie (Suffixanalyse)");
		}
		else if(baumArt.equals("Praefixe")){
			SwingPrefixes.meinFenster = new SwingPrefixes("Kompakter Trie (Praefixanalyse)");
		}
		else if(baumArt.equals("Suffixe und Praefixe")){
			SwingPreSuf.meinFenster = new SwingPreSuf("Kompakter Trie (Praefix- und Suffixanalyse )");
		}
		else if(baumArt.equals("Suffixe(Ukkonen)")){
			SwingSuffixesUkkonen.meinFenster = new SwingSuffixesUkkonen("Ukkonen-Suffixbaum (Suffixanalyse)");
		}	
	}
}

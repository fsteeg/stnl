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

package meinPaket;

import javax.swing.JOptionPane;

public class Main{

	public static void main(String[] args) {
		// Auswahldialog
	    String options[] = { "Suffixe", "Praefixe", "Suffixe und Praefixe", "Suffixe(Ukkonen)" };
	    String baumArt = (String) JOptionPane.showInputDialog( null, "Was soll ermittelt werden?",
	    							"Morpholog. Analyse", JOptionPane.QUESTION_MESSAGE, null, options, options[0] );
		if(baumArt.equals("Suffixe")){
			GUI_suffixe.meinFenster = new GUI_suffixe("Kompakter Trie (Suffixanalyse)");
		}
		else if(baumArt.equals("Praefixe")){
			GUI_praefixe.meinFenster = new GUI_praefixe("Kompakter Trie (Praefixanalyse)");
		}
		else if(baumArt.equals("Suffixe und Praefixe")){
			GUI_prae_suf.meinFenster = new GUI_prae_suf("Kompakter Trie (Praefix- und Suffixanalyse )");
		}
		else if(baumArt.equals("Suffixe(Ukkonen)")){
			GUI_suffixe_Ukkonen.meinFenster = new GUI_suffixe_Ukkonen("Ukkonen-Suffixbaum (Suffixanalyse)");
		}	
	}
}

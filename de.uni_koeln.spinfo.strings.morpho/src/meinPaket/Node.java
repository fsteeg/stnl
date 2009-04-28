///////////////////////////////////////
/// Projekt: Morphologische Analyse 
/// Klasse: Node.java	
/// Paket: meinPaket
/// Autor: Eva Hasler				
/// Datum: 8.4.06					
///////////////////////////////////////

package meinPaket;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class Node implements Serializable{
	public String inhalt;
	public HashMap<Character, Integer> kinder = new HashMap<Character, Integer>();
	public int mutter;
	public int tiefeImBaum;
	public int posInListe;
	
//	 Konstruktor für root-Knoten
	public Node(){
		mutter = -1;
		posInListe = 0;
	}
	
	public Node(String knotenInhalt, Node mutterKnoten, int indexMutter, int pos){
		inhalt = knotenInhalt;
		mutter = indexMutter;
		tiefeImBaum = mutterKnoten.tiefeImBaum + 1;
		posInListe = pos;
		// Mutterknoten aktualisieren
		if( !mutterKnoten.kinder.containsValue(pos) ){
			mutterKnoten.kinder.put(inhalt.charAt(0), pos);
		}
	}
 }

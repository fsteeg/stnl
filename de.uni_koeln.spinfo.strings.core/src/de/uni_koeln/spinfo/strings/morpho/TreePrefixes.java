///////////////////////////////////////
/// Projekt: Morphologische Analyse 
/// Klasse: Tree_praefixe.java	
/// Paket: meinPaket
/// Autor: Eva Hasler				
/// Datum: 8.4.06					
///////////////////////////////////////

package de.uni_koeln.spinfo.strings.morpho;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
//TODO translate comments, variable names, gui
public class TreePrefixes implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 4954776598597554437L;
    public String suffixBaumString = new String();
	public String[] wortliste;
	public LinkedList<Node> knotenListe;
	public int pos;
	
	// alle Praefixe, die im Baum vorkommen
	public LinkedList<String> praefixListe;
	
	// zu jedem Praefix die Suffixmenge
	public TreeMap<String,TreeSet<String>> suffixMengen = new TreeMap<String,TreeSet<String>>();
	
	// aus methode druckeBaum()
	String[] ergebnisListe = new String[1];
	
	// aktuell benutzte Textdatei
	String datei = new String();
	String dateinamenAnzeigen = new String();
	
	//Konstruktor	
	public TreePrefixes(){
		knotenListe = new LinkedList<Node>();
		praefixListe = new LinkedList<String>();
		// Liste mit root-Knoten initialisieren
		pos = 1;
		Node root = new Node();
		knotenListe.add(root);
	}
	
	// Es wird nur das Wort selbst, nicht aber seine Suffixe in den Baum eingefuegt 
	public void alleSuffixe(String text){
		
		wortliste = text.split("[^a-zA-Z_áÁéÉíÍóÓúÚàÀèÈìÌòÒùÙñÄäÖöÜüß]+");
		//wortliste = wort.split("[\\s+\\d+\\p{Punct}+]");
		String wort = new String();
		
		for(int i=0; i<wortliste.length; i++){
			wort = wortliste[i].toLowerCase();
			StringBuffer wortBuf = new StringBuffer(wort);
			// Wort vor Einfuegen in den Baum umdrehen:
			wortBuf = wortBuf.reverse();
			String wortUmgedreht = wortBuf.toString();
			wortUmgedreht = wortUmgedreht + "$";
			suffixEinfuegen(wortUmgedreht, 0);
		}
	}
	
	public boolean istPraefix(Node knoten, String knotenInhalt){
		if( knotenInhalt.startsWith( (this.knotenListe.get(knoten.posInListe)).inhalt ) ){
			return true;
		}
		else return false;
	}
	
	private void suffixEinfuegen(String suffix, int indexMutter){
		Node mutterKnoten = this.knotenListe.get(indexMutter);
		if(mutterKnoten.kinder.containsKey(suffix.charAt(0))){
        	Node aktuellerKnoten = knotenListe.get( mutterKnoten.kinder.get( suffix.charAt(0) ) );
    		String inhalt = aktuellerKnoten.inhalt;
			// FALL 1: der Knoteninhalt ist ein Praefix des Suffix
    		if( suffix.startsWith(inhalt) ){
    			if(!suffix.equals(inhalt)){
    				// schon gefundenen Teil des Suffix abschneiden
    				suffix = suffix.substring(inhalt.length());
    				// in den Kindern des aktuellen Knotens weitersuchen
    				suffixEinfuegen(suffix, this.knotenListe.indexOf(aktuellerKnoten) );
    			}
			}  
			else{
				// FALL 2: der erste Buchstabe von Suffix und Knoteninhalt stimmt ueberein
				//  --> laengstmoeglicher Praefix von suffix in inhalt muss gefunden werden
				// der folgende Index wird sich gemerkt, damit man spaeter an dieser Stelle den Knoten ersetzen kann
				int indexInKnotenListe = this.knotenListe.indexOf(aktuellerKnoten);
				String prae = new String();
				Boolean eingefuegt = false;
				for(int k = 1; (eingefuegt==false) && k <= inhalt.length(); k++){
					prae = inhalt.substring(0,k);
					// Sobald prae nicht mehr Praefix von suffix ist, wird der vorherige Praefix verwendet.
					if(!suffix.startsWith(prae)){
						prae = prae.substring(0,prae.length()-1);
						Node praefixKnoten = new Node(prae, mutterKnoten, knotenListe.indexOf(mutterKnoten), indexInKnotenListe);
						String altesSuffix = aktuellerKnoten.inhalt.substring(k-1);
						Node altesSfx = new Node(altesSuffix, praefixKnoten, indexInKnotenListe, pos);
						// Wenn der alte Knoten ein Blatt war, koennen einige Schritte uebersprungen werden:
						if(inhalt.endsWith("$")){
							knotenListe.add(altesSfx);
							pos++;
							String neuesSuffix = suffix.substring( prae.length() );
							Node neuesSfx = new Node(neuesSuffix, praefixKnoten, indexInKnotenListe, pos);
							knotenListe.add(neuesSfx);
							pos++;
							knotenListe.set(indexInKnotenListe, praefixKnoten);
							eingefuegt = true;
						}
						else{
							// In den Kindern des alten Knotens 'aktuellerKnoten' die Variable 'mutter' aktualisieren!!
							Set keys = aktuellerKnoten.kinder.keySet();
							for(Iterator iter = keys.iterator(); iter.hasNext(); ){
								int kind = aktuellerKnoten.kinder.get( (Character)iter.next() );
								knotenListe.get(kind).mutter = pos;
							}
							knotenListe.add(altesSfx);
							pos++;
							// Kinder in neuen Knoten kopieren
							knotenListe.get(altesSfx.posInListe).kinder = aktuellerKnoten.kinder;
																					
							String neuesSuffix = suffix.substring( prae.length() );
							Node neuesSfx = new Node(neuesSuffix, praefixKnoten, indexInKnotenListe, pos);
							knotenListe.add(neuesSfx);
							pos++;
							knotenListe.set(indexInKnotenListe, praefixKnoten);
							eingefuegt = true;
						}
					}
				}
			}
		}
		else{
			Node neu = new Node(suffix, mutterKnoten, knotenListe.indexOf(mutterKnoten), pos);			
			this.knotenListe.add(neu);
			this.pos++;
		}
	}
	
	public String[] druckeBaum(String eingabe){
		String[] ergebnisListe = new String[2];
		StringBuffer textSpeicher = new StringBuffer();
		//textSpeicher.append("Kompakter Suffixbaum fuer: \n---\n\"" + eingabe + "\"\n---\n\n");
		textSpeicher.append("root\n");
		int tiefe = 0;
				
		druckeKinder(0, tiefe, textSpeicher);		
		String textSpeicherSuf = textSpeicher.toString();
		ergebnisListe[0] = textSpeicherSuf;		
		
		// Frequenzliste erzeugen
				
		String textSpeicherFreq = new String();
		HashMap<String,WordData> wordDatas = new HashMap<String,WordData>(); 
        WordData wd;
        for(ListIterator it = praefixListe.listIterator(); it.hasNext(); ){
        	String next = (String)it.next();
        	
			// Frequenzliste
            wd = (WordData) wordDatas.get(next);
            if (wd == null) { // neues Wort am Ende anhaengen
                wordDatas.put(next, new WordData(next));
            }
            else { // Wort war schon eingetragen
            	wd.incrementCount();
            }
        }
        
        // Frequenzliste sortieren
        Object[] values = wordDatas.values().toArray();
        Comparator c = new WordCountComparator();
        Arrays.sort(values, c);
          
        for (int i=0; i<values.length; i++) {
        	wd = (WordData) values[i];
        	String PRAEFIX = wd.getWord();
        	// PRAEFIX UMDREHEN!!
            StringBuffer praefBuf = new StringBuffer(PRAEFIX);
            praefBuf.reverse();
            String praefUmgedreht = praefBuf.toString();
   
            String str = wd.getCount() + "\t" + praefUmgedreht + "\n";
            textSpeicherFreq = textSpeicherFreq + str;
        }
		ergebnisListe[1] = textSpeicherFreq;
		
		return ergebnisListe;
	}
		
	public void druckeKinder(int wurzelIndex, int tiefe, StringBuffer textSpeicher){
		tiefe++;
		Set keys = knotenListe.get(wurzelIndex).kinder.keySet();
		for ( Iterator i = keys.iterator(); i.hasNext(); ){
			Node kind = knotenListe.get( knotenListe.get(wurzelIndex).kinder.get( (Character)i.next() ));
			textSpeicher.append( gibTabs(tiefe) + kind.inhalt.toString() + "\n");
			
			// im Array 'praefixListe' werden alle Blaetter des Baums eingetragen
			// In der Variable 'suffixMengen' wird zu jedem Praefix die Menge der Suffixe gespeichert
			// (Die Suffixe sind alle Strings, die im Baum dem Praefix "vorangehen", die Woerter stehen 
			// ja rueckwaerts darin!)
			if(kind.inhalt.endsWith("$")){
				// '$' am Ende des Strings entfernen und  Strings, die nur aus '$' bestehen, aussortieren
				if(kind.inhalt.length() > 1){				
					Node mutter = new Node();
					mutter = knotenListe.get(kind.mutter);
				
					String praefix = kind.inhalt.substring(0,kind.inhalt.length()-1);
					praefixListe.add(praefix);
					// Praefixe stehen verkehrt herum im Baum und werden umgedreht, bevor sie gespeichert werden.
					StringBuffer praefixRichtigherum = new StringBuffer(praefix);
					praefixRichtigherum.reverse();
					String praefRichtigherum = praefixRichtigherum.toString();
					if(!suffixMengen.containsKey(praefRichtigherum)){
						suffixMengen.put(praefRichtigherum,new TreeSet<String>());
					}
					String suffix = "";
					while(mutter.inhalt != null){
						suffix = mutter.inhalt + suffix;
						mutter = knotenListe.get(mutter.mutter);
					}
					suffixMengen.get(praefRichtigherum).add(suffix);
				}
			}
			druckeKinder(kind.posInListe, tiefe, textSpeicher);
		}
	}
	
	
	public String gibTabs(int tiefeImBaum){
		String tabs = "\\___  ";
		int k = 0;
		while(k < tiefeImBaum){
			tabs = "\t" + tabs;
			k++;
		}
		return tabs;
	}
}

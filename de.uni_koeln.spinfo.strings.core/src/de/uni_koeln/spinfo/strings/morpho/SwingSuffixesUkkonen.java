////////////////////////////////////////
/// Projekt: Morphologische Analyse	 
/// Klasse: GUI_suffixe_Ukkonen.java 
/// Paket: meinPaket
/// Autor: Eva Hasler				 
/// Datum: 8.4.06					 
////////////////////////////////////////

package de.uni_koeln.spinfo.strings.morpho;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import de.uni_koeln.spinfo.strings.algo.suffixtrees.SimpleSequenceAccessor;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.UkkonenSuffixTree;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SimpleNode;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SimpleNodeAccessor;
import de.uni_koeln.spinfo.strings.algo.suffixtrees.node.memory.SuffixNode;
//TODO translate comments, variable names, gui
public class SwingSuffixesUkkonen extends JFrame{
	
	// aktuelles Fenster
	public static SwingSuffixesUkkonen meinFenster;
	
	public UkkonenSuffixTree ukkonenSuffixBaum = new UkkonenSuffixTree(new SimpleNodeAccessor(), new SimpleSequenceAccessor(1000));
	StringBuffer baumString = new StringBuffer();
	String dateiEin = new String();
	String pfadDateiEin = new String();
	String dateinamenAnzeigen = new String();
	// alle Suffixe, die in einem kompletten Wort vorkommen
	LinkedList<String> suffixListe = new LinkedList<String>();
	TreeSet<String> wortListe = new TreeSet<String>(); 
	String frequenzListe = new String();
	// zu jedem Suffix die Praefixmenge
	TreeMap<String,TreeSet<String>> praefixMengen = new TreeMap<String,TreeSet<String>>();
	String text = new String(); 
	
	// aus TextField
	String eingabeString = "";
	String eingabeStringTF1 = "";
	Integer schwellenWert = 0;
	// Variable zur Unterscheidung: Suffixbaum aus Textfeld oder Textdatei 
	Boolean tf1 = false;
	
	String dateiPfad = "";
	long differenz;
		
	private LinkedList<String> haeufig = new LinkedList<String>();
	
	// zum Zwischenspeichern von Mengen
	TreeSet<String> temp = new TreeSet<String>();
		
	// Liste der Praefixe, die mit den haeufigsten Suffixen vorkommen
	public TreeSet<String> allgemeinePraefixMenge = new TreeSet<String>();
	
	// zu jedem Praefix die Suffixmenge
	public TreeMap<String,TreeSet<String>> suffixMengen = new TreeMap<String,TreeSet<String>>();
	
	// hier werden die Klassen von zusammengehoerigen Woertern/Morphemen gespeichert
	LinkedList<TreeSet> klassen = new LinkedList<TreeSet>();
	
	// Vereinigte Mengen von Klassen
	HashMap<TreeSet, TreeSet> vereinigteKlassen = new HashMap<TreeSet, TreeSet>(); 
	
	// AnzeigeFenster
	JTextArea myTextArea;
	TextField textFeld1;
	TextField textFeld2;
	TextField schwellenFeld;
	
	Color lila = new Color(0.95f, 0.85f, 0.95f);
	Color weiss = new Color(1.0f, 1.0f, 1.0f);
		
	public SwingSuffixesUkkonen(String titel){
		super(titel);
		setBackground(Color.lightGray);
		//setSize(850,1010);
		//setLocation(350,0);
		setSize(750,700);
		setLocation(170,20);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// JTextArea
		myTextArea = new JTextArea();
		myTextArea.setBackground(lila);
		myTextArea.setEditable(false);
		myTextArea.addMouseListener(new MyMouseListener());
		myTextArea.addMouseMotionListener(new MyMouseMotionListener());
		JScrollPane scrollpane = new JScrollPane(myTextArea);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
		JPanel panel1 = new JPanel();
		GridLayout grid = new GridLayout(2,3);
		grid.setVgap(5);
		panel1.setLayout(grid);
		textFeld1 = new TextField();
		textFeld1.addTextListener(new MyTextListener());
		textFeld2 = new TextField();
		JButton knopf1 = new JButton("Eingabefeld loeschen");
		knopf1.addActionListener(new MyActionListener());
		JButton knopf2 = new JButton("Suffixbaum aus String");
		knopf2.addActionListener(new MyActionListener());
		JButton knopf4 = new JButton("Textdatei oeffnen..");
		knopf4.addActionListener(new MyActionListener());
		JButton knopf5 = new JButton("1) Suffixbaum aus Textdatei");
		knopf5.addActionListener(new MyActionListener());
		panel1.add(textFeld1);
		panel1.add(knopf1);
		panel1.add(knopf2);
		panel1.add(textFeld2);
		panel1.add(knopf4);
		panel1.add(knopf5);
		
		JPanel panel2 = new JPanel();
		GridLayout grid2 = new GridLayout(7,1);
		grid2.setVgap(5);
		panel2.setLayout(grid2);
		
		JButton knopf11 = new JButton("(Baum zeigen)");
		knopf11.addActionListener(new MyActionListener());
		JButton knopf7 = new JButton("2) Frequenzliste der Blaetter");
		knopf7.addActionListener(new MyActionListener());
		
		JLabel label = new JLabel("         3) Schwellenwert(int) eingeben:");
		schwellenFeld = new TextField();
		schwellenFeld.addTextListener(new MyTextListener());
		JPanel panel3 = new JPanel();
		panel3.setBackground(Color.white);
		panel3.setLayout(new GridLayout(2,1));
		panel3.add(label);
		panel3.add(schwellenFeld);
		
		JButton knopf8 = new JButton("4) Haeufigkeit der Suffixe >= Schwelle");
		knopf8.addActionListener(new MyActionListener());
		JButton knopf9 = new JButton("5) Praefixmengen der haeufigsten Suffixe");
		knopf9.addActionListener(new MyActionListener());
		JButton knopf10 = new JButton("6) Suffixmengen dieser Praefixe");
		knopf10.addActionListener(new MyActionListener());
		JButton knopf12 = new JButton("7) Klassen");
		knopf12.addActionListener(new MyActionListener());
		panel2.add(knopf11);
		panel2.add(knopf7);
		panel2.add(panel3);			
		panel2.add(knopf8);
		panel2.add(knopf9);
		panel2.add(knopf10);
		panel2.add(knopf12);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panel1, BorderLayout.NORTH);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		getContentPane().add(panel2, BorderLayout.EAST);
		
		Menuleiste ml = new Menuleiste();
		this.setMenuBar(ml);
		setVisible(true);
	}
	
	public void erzeugeTree(){
		baumString = new StringBuffer();
		// Speicherobejekte erneuern
		ukkonenSuffixBaum = new UkkonenSuffixTree(new SimpleNodeAccessor(), new SimpleSequenceAccessor());
		dateinamenAnzeigen = dateiEin;
		
		String[] woerter = text.split("[^a-zA-Z_áÁéÉíÍóÓúÚñÄaÖöÜüß]+");
		String wort = new String();
		for (int i = 0; i < woerter.length; i++) {
			wort = woerter[i].toLowerCase();
            // FIXME attention, temp!
            throw new RuntimeException("curretly not implemented!");
//			ukkonenSuffixBaum.addSequence(wort, 0, false);
//			wortListe.add(wort);
		}
		
		Node root = ukkonenSuffixBaum.getRoot();
		Integer tiefe = 0;
		kinder(root, tiefe);
	}
	
	void kinder(Node mutter,Integer t){
		Integer baumTiefe = t;
		String tabs =  new String();
		if(t==0){
			tabs = "root";
		}
		else{
			tabs = "\\___ ";
		}
		while(t > 0){
			tabs = "\t" + tabs;
			t--;
		}
		baumString.append(tabs);
		baumString.append(ukkonenSuffixBaum.getEdgeLabel(mutter));
		baumString.append("\n");
		// nur wenn der aktuelle Knoten kein Blatt ist, wird weiter nach Kindern gesucht:
		if(mutter.getChildren().size()!=0){
			Collection kinder = mutter.getChildren().values();
			for(Iterator it = kinder.iterator(); it.hasNext(); ){
				SuffixNode kind = (SuffixNode)it.next();
				kinder(kind, baumTiefe+1);
			}
		}
	}
	
	class WindowListener extends WindowAdapter{
		public void windowDeactivated(WindowEvent e){
			myTextArea.setBackground(Color.DARK_GRAY);
			repaint();
		}
	}
		
	class MyMouseListener extends MouseAdapter{
		public void mouseEntered(MouseEvent event){
			repaint();
		}
		public void mouseExited(MouseEvent event){
			repaint();
		}
		public void mousePressed(MouseEvent event){
			if(event.getButton() == MouseEvent.BUTTON1){
				repaint();
			}
			else if(event.getButton() == MouseEvent.BUTTON3){
				repaint();
			}
		}
	}
	
	class MyMouseMotionListener extends MouseMotionAdapter{
		public void mouseMoved(MouseEvent event){
			repaint();
		}
	}
		
	class MyTextListener implements TextListener{
		public void textValueChanged(TextEvent event){
			TextField tf = (TextField)event.getSource();
			if(tf.equals(textFeld1)){
				eingabeStringTF1 = tf.getText();
			}
			else if(tf.equals(schwellenFeld)){
				try{
					String eingabe = tf.getText();
					if(!eingabe.equals("")){
						schwellenWert = Integer.parseInt( eingabe );
					}
				}catch(Exception e){
					System.out.println("Schwellenwert muss Integer sein!");
				}
			}
		}
	}
	
	class MyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			// Schriftgroesse  fuer den Text setzen
			Font f = new Font ("Serif",Font.PLAIN,20);
			myTextArea.setFont(f);
			
			String cmd = event.getActionCommand();
			// SUFFIXBAUM AUS STRING
			if( cmd.equals("Suffixbaum aus String") ){
				//erzeuge Suffixbaum und gebe ihn in der JTextArea aus
				if(eingabeStringTF1.equals("")){
					myTextArea.setText("Bitte String eingeben");
				}
				else{
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					tf1 = true;
					text = eingabeStringTF1;
					erzeugeTree(); 
					myTextArea.setText(baumString.toString());
					repaint();
					tf1 = false;
					dateinamenAnzeigen = "";
					cur = new Cursor(Cursor.DEFAULT_CURSOR); 
					meinFenster.setCursor(cur);
				}
			}
			// EINGABEFELD LOESCHEN
			else if( cmd.equals("Eingabefeld loeschen") ){
				textFeld1.setText("");
				eingabeString = "";
				repaint();
			}
			// TEXTDATEI SUCHEN
			else if( cmd.equals("Textdatei oeffnen..") ){
				FileDialog d = new FileDialog(SwingSuffixesUkkonen.meinFenster, "Oeffnen...", FileDialog.LOAD);
				d.pack(); 
				d.setVisible(true);
				dateiEin = d.getFile();
				String pfad = d.getDirectory() + dateiEin;
				textFeld2.setText(pfad);
				d.dispose();					
			}
			// SUFFIXBAUM AUS TEXTDATEI
			else if( cmd.equals("1) Suffixbaum aus Textdatei") ){
				// Zeit messen:
				long zeitDavor = System.currentTimeMillis();
				String pfad = textFeld2.getText();
				String ausgabe = new String();
				
				if(pfad.equals("") || pfad.equals("nullnull")){
					ausgabe = "Bitte Datei auswaehlen";
					myTextArea.setText(ausgabe);	
					repaint();
				}
				else{
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					text = new String();
					try{
						FileInputStream ein = new FileInputStream(pfad);
						BufferedReader dateiBuf = new BufferedReader( new InputStreamReader(ein));
						String zeile = dateiBuf.readLine();
						while(zeile != null){
							text += zeile;
							zeile = dateiBuf.readLine();
						}
						erzeugeTree();
						long zeitDanach = System.currentTimeMillis();
						long differenz = zeitDanach-zeitDavor;
						StringBuffer temp = new StringBuffer("Ukkonen-Suffixbaum aus Datei '"+dateinamenAnzeigen+"'\nBaum wurde erzeugt in "+ differenz+" Millisekunden!\n\n");
						temp.append(baumString);
						baumString =  temp;
						ausgabe = baumString.toString();
						myTextArea.setText(ausgabe);	
						repaint();
						cur = new Cursor(Cursor.DEFAULT_CURSOR); 
						meinFenster.setCursor(cur);
					}catch(FileNotFoundException e){
						ausgabe = "Datei nicht gefunden!";
						myTextArea.setText(ausgabe);	
						repaint();
					}catch(IOException e){
						ausgabe = "Fehler beim Lesen aus der Datei!";
						myTextArea.setText(ausgabe);	
						repaint();
					}finally{
						cur = new Cursor(Cursor.DEFAULT_CURSOR); 
						meinFenster.setCursor(cur);
					}
				}
			}
			// BAUM ZEIGEN 
			else if( cmd.equals("(Baum zeigen)")){				
				String ausgabe = new String();
				ArrayList array = new ArrayList();
				ukkonenSuffixBaum.getAllNodes(ukkonenSuffixBaum.getRoot(), array, false);
				if(array.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthlt)
					ausgabe = "Bitte zuerst einen Suffixbaum erzeugen";
				}
				else{
					// zuletzt erzeugten Baum anzeigen
					ausgabe = baumString.toString();
				}
				myTextArea.setText(ausgabe);
				repaint();
			}
			// FREQUENZLISTE
			else if( cmd.equals("2) Frequenzliste der Blaetter") ){
				frequenzListe = new String();
				suffixListe.clear();
				String ausgabe = new String();
				ArrayList array = new ArrayList();
				ukkonenSuffixBaum.getAllNodes(ukkonenSuffixBaum.getRoot(), array, false);
				if(array.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthlt)
					ausgabe = "Bitte zuerst einen Suffixbaum erzeugen";
				}
				else{
					/*********************************************************************/
					// Alle Suffixe, die zu einem kompletten Wort gehoeren, werden in
					// suffixListe gespeichert, um danach gezaehlt zu werden:
					ArrayList<Node> liste = new ArrayList<Node>();
					ukkonenSuffixBaum.getAllNodes(ukkonenSuffixBaum.getRoot(), liste, false);
					for (Node node : liste) {
						if(node.getChildren().size()==0){
							String string2 = ukkonenSuffixBaum.getEdgeLabel(node).toString();
							String suffix = string2;
							suffix = suffix.replaceAll("\\$","");
							while(!ukkonenSuffixBaum.getRoot().equals(((SimpleNode)node).getParent())){
								string2 = ukkonenSuffixBaum.getEdgeLabel(((SimpleNode)node).getParent()).toString() + string2;
								node = ((SimpleNode)node).getParent();
							}
							string2 = string2.replaceAll("\\$","");
							// Testen, ob der erzeugte String ein Wort des Textes ist:
							if(wortListe.contains(string2)){
								suffixListe.add(suffix);
							}
						}
					}
					// Frequenzliste erzeugen
					HashMap<String,WordData> wordDatas = new HashMap<String,WordData>(); 
			        WordData wd;
			        for(ListIterator it = suffixListe.listIterator(); it.hasNext(); ){
			        	String next = (String)it.next();
			        	
			        	if(!(next.equals("")) ){
			        		// Frequenzliste
			        		wd = (WordData) wordDatas.get(next);
			        		if (wd == null) { // neues Wort am Ende anhaengen
			        			wordDatas.put(next, new WordData(next));
			        		}
			        		else { // Wort war schon eingetragen
			        			wd.incrementCount();
			        		}
			        	}
			        }
			        
			        // Frequenzliste sortieren
			        Object[] values = wordDatas.values().toArray();
			        Comparator c = new WordCountComparator();
			        Arrays.sort(values, c);
			        // Ausgabe
			        for (int i=0; i<values.length; i++) {
			        	wd = (WordData) values[i];
			            String str = wd.getCount() + "\t" + wd.getWord() + "\n";
			            ausgabe += str;
			            frequenzListe += str;
			        }
			        /***********************************************************************/
			        ausgabe = "Frequenzliste der Blaetter:\n(Datei '" + dateinamenAnzeigen + "')\n\n" 
					+ frequenzListe;
				}
				myTextArea.setText(ausgabe);	
				repaint();
			}
			// LISTE DER HAEUFIGSTEN SUFFIXE
			else if( cmd.equals("4) Haeufigkeit der Suffixe >= Schwelle")){
				String ausgabe = new String();
				ArrayList array = new ArrayList();
				ukkonenSuffixBaum.getAllNodes(ukkonenSuffixBaum.getRoot(), array, false);
				if(frequenzListe.length() == 0){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthlt)
					ausgabe = "Bitte zuerst die Frequenzliste erzeugen";
				}
				else{
					haeufig.clear();
					StringReader stringRead = new StringReader(frequenzListe);
					String buffer = new String();
					int buchstabe;
					Boolean weiter = true;
					if(schwellenWert != null){
						while(weiter){
							try{
								buchstabe = stringRead.read();
								buffer = buffer + (char)buchstabe;
								if(buchstabe == 10 || buchstabe == 13){
									String[] s = buffer.split("\\s+");
									Integer anzahl = Integer.parseInt(s[0]);
									if(anzahl > schwellenWert || anzahl == schwellenWert){
										String nurChars = s[1];
										haeufig.add(nurChars);
									}
									else{
										weiter = false;
									}
									buffer = "";
								}
								else if(buchstabe == -1){
									weiter = false;
								}
							}catch(IOException e){ 
								System.out.println("Fehler beim Lesen des Strings");	
							}
						}
													
						if(!haeufig.isEmpty()){
							// Liste der haeufigen Suffixe nach Laenge sortieren, damit jeweils der laengste Suffix fuer ein Wort gefunden wird
							Collections.sort(haeufig, new SwingComparator());
							ausgabe = "Mind. "+ schwellenWert +"mal vorkommende Suffixe:\n(Datei '" +meinFenster.dateinamenAnzeigen+ "')\n\n";
							for(ListIterator it = haeufig.listIterator(); it.hasNext(); ){
								ausgabe += it.next() + "\n";
							}
						}
						else{
							ausgabe = "Keine Suffixe mit Haeufigkeit > "+schwellenWert+ " vorhanden!";
						}
					}
					else{
						ausgabe = "Bitte Schwellenwert eingeben!";
					}
				}
				myTextArea.setText(ausgabe);	
				repaint();
			}
			// PRAEFIXMENGEN
			else if( cmd.equals("5) Praefixmengen der haeufigsten Suffixe") ){	
				String ausgabe = new String();
				if(haeufig.isEmpty()){
					ausgabe = "Bitte erst die Liste der haeufigsten Suffixe erzeugen!";
				}	
				else{	
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					// Erzeugung der Praefixmengen: Baum durchsuchen
					// Alle Blaetter pruefen und von da aus die Praefixe suchen:
					ArrayList<Node> liste = new ArrayList<Node>();
					ukkonenSuffixBaum.getAllNodes(ukkonenSuffixBaum.getRoot(), liste, true);
					for (Node node : liste) {
						// label ist zunaechst der Inhalt des Blatts
						String label = ukkonenSuffixBaum.getEdgeLabel(node).toString();
						String suffix = label;
						String praefix = "";
						suffix = suffix.replaceAll("\\$","");
						while(!ukkonenSuffixBaum.getRoot().equals(((SimpleNode)node).getParent())){
							praefix = ukkonenSuffixBaum.getEdgeLabel(((SimpleNode)node).getParent()).toString() + praefix;
							node = ((SimpleNode)node).getParent();
						}
						// Testen, ob der erzeugte String ein Wort des Textes ist. Wenn ja, den Praefix
						// zu den Praefixmengen hinzufuegen.
						if(wortListe.contains(praefix+suffix)){
							if(!praefixMengen.containsKey(suffix)){
								praefixMengen.put(suffix,new TreeSet<String>());
							}
							praefixMengen.get(suffix).add(praefix);
						}
					}
					// Ausgabe der Praefixmengen:
					for(Iterator it = haeufig.iterator(); it.hasNext(); ){
						String suffix = (String)it.next();
						ausgabe += "\n\nFuer das Suffix \"" + suffix + "\": \n\n";
						
						for(Iterator iter = praefixMengen.get(suffix).iterator(); iter.hasNext(); ){
							ausgabe += iter.next() + " + " +suffix+ "\n";
						}
						ausgabe += "-------------------------------------\n\n";
					}
					String titel = "Praefixmengen der mind. " + schwellenWert + "mal vorkommenden Suffixe: \n(Datei '" +dateinamenAnzeigen+ "')";
					ausgabe = titel + ausgabe;
					cur = new Cursor(Cursor.DEFAULT_CURSOR); 
					meinFenster.setCursor(cur);
				}
				myTextArea.setText(ausgabe);	
				repaint();
			}
            // SUFFIXMENGEN DER PRAEFIXE
			else if( cmd.equals("6) Suffixmengen dieser Praefixe")){	
				String ausgabe = new String();
				if(praefixMengen.isEmpty()){
					ausgabe = "Bitte erst die Praefixmengen erzeugen!";		
				}
				else{	
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					ausgabe = new String();
					TreeSet<String> zwischenMenge = new TreeSet<String>();		
					
					for(ListIterator it = haeufig.listIterator(); it.hasNext(); ){
						String next = (String)it.next();
						zwischenMenge = praefixMengen.get(next);
					
						//  fuer jedes der haeufigen Suffixe, alle Praefixe in allgemeinePraefixMenge speichern
						for(Iterator iter = zwischenMenge.iterator(); iter.hasNext(); ){
							String aktuellesPraefix = (String)iter.next();
							allgemeinePraefixMenge.add(aktuellesPraefix);
						}
					}
							
					for(Iterator it = allgemeinePraefixMenge.iterator(); it.hasNext(); ){
						String aktuellesPraefix = (String)it.next();
						// neues Speicherobjekt anlegen
						temp = new TreeSet<String>();
						// sucheEndungenInBaum speichert alle moeglichen Suffixe des Praefixes in temp
						// diese muessen aber gleichzeitig auch in der Menge der haeufigsten Suffixe sein, sonst entsteht Schrott
						sucheEndungenImBaum(aktuellesPraefix,ukkonenSuffixBaum.getRoot(), aktuellesPraefix);
						if(!temp.isEmpty()){
							// Suffixmengen abspeichern
							suffixMengen.put(aktuellesPraefix,temp);
							ausgabe += aktuellesPraefix+"\n";
							
							for(Iterator iterator = temp.iterator(); iterator.hasNext(); ){
								ausgabe += "\t-"+iterator.next()+ "\n";
							}
						}
						else{
							System.out.println("Nicht im Baum wiedergefunden: "+aktuellesPraefix);
						}
					}
					ausgabe = "Fuer jedes Praefix die Menge der haeufigen Suffixe: \n(Datei '" +dateinamenAnzeigen+ "')\n\n" + ausgabe;
					cur = new Cursor(Cursor.DEFAULT_CURSOR); 
					meinFenster.setCursor(cur);
				}
				myTextArea.setText(ausgabe);	
				repaint();
			}
			// KLASSEN
			else if(cmd.equals("7) Klassen")){
				String ausgabe = new String();
				if(suffixMengen.isEmpty()){
					ausgabe = "Bitte erst die Suffixmengen erzeugen!";
				}
				else{
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					ausgabe = "Klassen von Strings mit denselben (haeufigen) Suffixen: \n(Datei '" +dateinamenAnzeigen+ "')\n\n";
						
					TreeSet<String> suffixmenge1 = new TreeSet<String>();
					TreeSet<String> suffixmenge2 = new TreeSet<String>();
					// Variablen zum Zwischenspeichern
					TreeSet<String> menge; 
					
					for(Iterator it = allgemeinePraefixMenge.iterator(); it.hasNext(); ){
						String praefix1 = (String)it.next();
						if(suffixMengen.containsKey(praefix1)){
							suffixmenge1 = suffixMengen.get(praefix1);
							// Die Suffixmenge soll mehr als ein Element haben, damit der Vegleich sinnvoll ist
							if(suffixmenge1.size() > 1){
								for(Iterator iter = allgemeinePraefixMenge.iterator(); iter.hasNext(); ){
									String praefix2 = (String)iter.next();
									if(!(praefix1.equals(praefix2))){
										if(suffixMengen.containsKey(praefix2)){
											suffixmenge2 = suffixMengen.get(praefix2);
											if(suffixmenge1.containsAll(suffixmenge2) && suffixmenge2.containsAll(suffixmenge1)){
												Boolean eingefuegt = false;
												if( !(klassen.isEmpty()) ){
													for(ListIterator iterat = klassen.listIterator(); iterat.hasNext(); ){
														menge = (TreeSet)iterat.next();
														if( menge.contains(praefix1)){
															menge.add(praefix2);
															eingefuegt = true;
															break;
														}
														else if(menge.contains(praefix2)){
															menge.add(praefix1);
															eingefuegt = true;
															break;
														}
													}
												}
												// Wenn es noch gar keine klassen-Menge gibt, wird hier die erste angelegt
												// Wenn die Praefixe noch in keiner Menge sind, wird eine neue Menge angelegt
												if(klassen.isEmpty() || !eingefuegt){
													menge = new TreeSet();
													menge.add(praefix1);
													menge.add(praefix2);
													klassen.add(menge);
												}
											}
										}
									}	
								}
							}
						}
					}
				
					TreeSet<String> menge1;
					TreeSet<String> menge2;
					for(ListIterator it = klassen.listIterator(); it.hasNext(); ){
						menge1 = (TreeSet)it.next();
						String erstesPraefix = menge1.first();
						menge2 = new TreeSet<String>();
						menge2 = suffixMengen.get(erstesPraefix);
						ausgabe += "Suffixe {  ";
						for(Iterator iter = menge2.iterator(); iter.hasNext(); ){
							String suffix = (String)iter.next();
							ausgabe += "-" + suffix + "   ";
						}
						ausgabe += "}\n\t";
					
						for(Iterator iterat = menge1.iterator(); iterat.hasNext(); ){
							ausgabe += (String)iterat.next() + "-   ";
						}
						ausgabe += "\n\n";
						}
						cur = new Cursor(Cursor.DEFAULT_CURSOR); 
						meinFenster.setCursor(cur);
					}
				myTextArea.setText(ausgabe);	
				repaint();
			}
		}
	}
	
	// Diese Methode speichert die Menge der moeglichen Suffixe eines Strings in 'temp'
	// und wird mit Index 0 aufgerufen
	// (diese Suffixe muessen aber gleichzeitig auch in der Menge der haeufigsten Suffixe sein)	
	void sucheEndungenImBaum(String praefix, Node mutter, String Prae){
		Collection kinder = mutter.getChildren().values();
		for(Iterator it = kinder.iterator(); it.hasNext(); ){
			SuffixNode kind = (SuffixNode)it.next();
			String label = ukkonenSuffixBaum.getEdgeLabel(kind).toString();
			if(kind.isTerminal()){
				label = label.replaceAll("\\$","");
				if(label.startsWith(praefix)){
					String endung = label.substring( praefix.length() );
					if( wortListe.contains(Prae+endung) && haeufig.contains(endung)){
						temp.add(endung);
					}
				}
			}
			else{
				if(praefix.equals(label)){
					kinderSpeichern(kind, "", Prae);
				}
				else if(praefix.startsWith(label)){
					String restPraefix = praefix.substring( label.length() );
					sucheEndungenImBaum(restPraefix, kind, Prae);
				}
			}
		}
	}
	
	void kinderSpeichern(SuffixNode mutter, String e, String Prae){
		Collection kinder = mutter.getChildren().values();
		for(Iterator it = kinder.iterator(); it.hasNext(); ){
			SuffixNode kind = (SuffixNode)it.next();
			String label = ukkonenSuffixBaum.getEdgeLabel(kind).toString();
			if(kind.isTerminal()){
				String endung = e;
				label = label.replaceAll("\\$","");
				endung += label;
				if( wortListe.contains(Prae+endung) && haeufig.contains(endung) && !endung.equals("")){
					temp.add(endung);
				}
			}
			else{
				String endung = e;
				endung += label;
				kinderSpeichern(kind, endung, Prae);
			}
		}
	}
	
	public class Menuleiste extends MenuBar{ 
		public Menuleiste(){
			super();
			this.add(new Menu("Datei"));
			MenuItem textSpeichern = new MenuItem("Aktuellen Text speichern");
			textSpeichern.addActionListener(new MenuActionListener());
			this.getMenu(0).add(textSpeichern);
			this.getMenu(0).addSeparator();
			MenuItem schliessen = new MenuItem("Schliessen");
			schliessen.addActionListener(new MenuActionListener());
			this.getMenu(0).add(schliessen);
		}
		
		class MenuActionListener implements ActionListener{
			public void actionPerformed(ActionEvent event){
				String cmd = event.getActionCommand();
				if(cmd.equals("Schliessen")){
					setVisible(false);
					dispose();
					System.exit(0);
				}
				else if(cmd.equals("Aktuellen Text speichern")){	
					// Dateinamen waehlen:
					FileDialog d = new FileDialog(meinFenster, "Speichern...", FileDialog.SAVE);
					d.pack(); 
					d.setVisible(true);
					String dateiname = d.getFile();
					String pfad = d.getDirectory() + dateiname;
					d.dispose();				
					
					if(!pfad.equals("nullnull")){
						try{
							String text = myTextArea.getText();
							String[] zeilen = text.split("[\\n]");
							File datei = new File(pfad);            
							if(!datei.exists() ){ 
								datei.createNewFile(); 
							}
							String newline = System.getProperty("line.separator"); 
							BufferedWriter aus = 
								new BufferedWriter( new OutputStreamWriter( new FileOutputStream(datei)));
							for(int i=0; i < zeilen.length; i++){
								aus.write(zeilen[i]+newline);
							}
							aus.close();
							myTextArea.setText("Aktueller Text wurde gespeichert unter '"+ pfad);		
							repaint();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		}
	}		
}
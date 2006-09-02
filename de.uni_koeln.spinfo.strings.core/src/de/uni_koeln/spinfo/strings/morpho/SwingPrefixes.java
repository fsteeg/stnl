///////////////////////////////////////
/// Projekt: Morphologische Analyse 
/// Klasse: GUI_praefixe.java		
/// Paket: meinPaket
/// Autor: Eva Hasler				
/// Datum: 8.4.06					
///////////////////////////////////////

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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

//TODO translate comments, variable names, gui
public class SwingPrefixes extends JFrame{
	
	/**
     * 
     */
    private static final long serialVersionUID = -6418673241394107598L;
    // aktuelles Fenster
	public static SwingPrefixes meinFenster;
	public TreePrefixes meinSuffixBaum = new TreePrefixes();
	
	// aus TextField
	String eingabeString = "";
	String eingabeStringTF1 = "";
	Integer schwellenWert = null;
	// Variable zur Unterscheidung: Suffixbaum aus Textfeld oder Textdatei 
	Boolean tf1 = false;
	
	String dateiPfad = "";
	long differenz;
	
	private LinkedList<String> haeufig = new LinkedList<String>();
	
	// zum Zwischenspeichern von Mengen
	TreeSet<String> temp = new TreeSet<String>();
	
	// Liste der Suffixe, die mit den haeufigsten Praefixen vorkommen
	public TreeSet<String> allgemeineSuffixMenge = new TreeSet<String>();
	
	// zu jedem Suffix die Praefixmenge
	public TreeMap<String,TreeSet<String>> praefixMengen = new TreeMap<String,TreeSet<String>>();
	
	// hier werden die Klassen von zusammengehoerigen Woertern/Morphemen gespeichert
	LinkedList<TreeSet> klassen = new LinkedList<TreeSet>();
	
	// Vereinigte Mengen von Klassen
	HashMap<TreeSet, TreeSet> vereinigteKlassen = new HashMap<TreeSet, TreeSet>();
	
	// AnzeigeFenster
	JTextArea myTextArea;
	TextField textFeld1;
	TextField textFeld2;
	TextField schwellenFeld;
	
	//Color blau = new Color(0.5f, 0.5f, 1.0f);
	Color blau = new Color(0.8f, 0.8f, 1.0f);
	Color weiss = new Color(1.0f, 1.0f, 1.0f);
	
	public SwingPrefixes(String titel){
		super(titel);
		setBackground(Color.lightGray);
		//setSize(850,1010);
		//setLocation(350,0);
		setSize(750,700);
		setLocation(170,20);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// JTextArea
		myTextArea = new JTextArea();
		myTextArea.setBackground(blau);
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
		GridLayout grid2 = new GridLayout(8,1);
		grid2.setVgap(5);
		panel2.setLayout(grid2);
		
		JButton knopf13 = new JButton("1) Gespeicherten Baum einlesen");
		knopf13.addActionListener(new MyActionListener());
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
		
		JButton knopf8 = new JButton("4) Haeufigkeit der Praefixe >= Schwelle");
		knopf8.addActionListener(new MyActionListener());
		JButton knopf9 = new JButton("5) Suffixmengen der haeufigsten Praefixe");
		knopf9.addActionListener(new MyActionListener());
		JButton knopf10 = new JButton("6) Praefixmengen dieser Suffixe");
		knopf10.addActionListener(new MyActionListener());
		JButton knopf12 = new JButton("7) Klassen");
		knopf12.addActionListener(new MyActionListener());
		panel2.add(knopf13);
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
		meinSuffixBaum.suffixBaumString = new String();
		// String sichern, bevor das Tree_praefixe-Objekt erneuert wird:
		String datei = meinSuffixBaum.datei;
		
		// Speicherobjekte erneuern
		meinSuffixBaum = new TreePrefixes();
		if(!tf1){
			meinSuffixBaum.alleSuffixe(eingabeString);
			meinSuffixBaum.ergebnisListe = meinSuffixBaum.druckeBaum(eingabeString);
		}
		else{
			meinSuffixBaum.alleSuffixe(eingabeStringTF1);
			meinSuffixBaum.ergebnisListe = meinSuffixBaum.druckeBaum(eingabeStringTF1);
		}
		if(!tf1){
			String titel = "Kompakter Baum aus der Datei '" + datei + "'\n\n";
			meinSuffixBaum.suffixBaumString = titel + meinSuffixBaum.ergebnisListe[0];
			meinSuffixBaum.dateinamenAnzeigen = datei;
		}
		else{
			meinSuffixBaum.suffixBaumString = meinSuffixBaum.ergebnisListe[0];
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
					myTextArea.setText("Schwellenwert muss Integer sein!");	
					repaint();
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
					erzeugeTree(); 
					myTextArea.setText(meinSuffixBaum.suffixBaumString);
					repaint();
					tf1 = false;
					meinSuffixBaum.dateinamenAnzeigen = "";
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
				FileDialog d = new FileDialog(SwingPrefixes.meinFenster, "Oeffnen...", FileDialog.LOAD);
				d.pack(); 
				d.setVisible(true);
				meinSuffixBaum.datei = d.getFile();
				String pfad = d.getDirectory() + meinSuffixBaum.datei;
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
					try{	
						BufferedReader in = new BufferedReader(new FileReader(pfad));
						eingabeString = "";
						int ascii = in.read();
						char zeichen;
						while(ascii != -1){
							zeichen = (char)ascii;
							eingabeString += zeichen;
							ascii = in.read();
						}
						erzeugeTree(); 
						long zeitDanach = System.currentTimeMillis();
						differenz = zeitDanach-zeitDavor;
												
						meinSuffixBaum.suffixBaumString = "Dauer der Erzeugung: "+differenz+ " Millisekunden\n"+meinSuffixBaum.suffixBaumString;
						myTextArea.setText(meinSuffixBaum.suffixBaumString);
						repaint();
						cur = new Cursor(Cursor.DEFAULT_CURSOR); 
						meinFenster.setCursor(cur);
					}catch(IOException e){
						ausgabe = "Fehler beim Einlesen der Datei!";
						cur = new Cursor(Cursor.DEFAULT_CURSOR); 
						meinFenster.setCursor(cur);
						myTextArea.setText(ausgabe);	
						repaint();
					}
				}
			}		
			// GESPEICHERTEN BAUM EINLESEN
			else if( cmd.equals("1) Gespeicherten Baum einlesen")){
				String ausgabe = new String();
				try{
					BufferedInputStream fis = new BufferedInputStream(new FileInputStream("Baum_praefixe.tmp"));
					ObjectInputStream ois = new ObjectInputStream(fis);
					meinSuffixBaum = (TreePrefixes)ois.readObject();
					ausgabe = "Baum wurde eingelesen!";
					myTextArea.setText(ausgabe);
					repaint();
				}catch(FileNotFoundException e){
					ausgabe = "Datei nicht gefunden!";
				}catch(IOException e){
					ausgabe = "Fehler beim Lesen der Datei!";
				}catch(Exception e){
					ausgabe = "Unbekannter Fehler!";
					e.printStackTrace();
				}finally{
					myTextArea.setText(ausgabe);
					repaint();
				}
			}
			// BAUM ZEIGEN 
			else if( cmd.equals("(Baum zeigen)")){
				String ausgabe;
				if(meinSuffixBaum.knotenListe.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthaelt)
					ausgabe = "Bitte zuerst einen Suffixbaum erzeugen";
				}
				else{
					// zuletzt erzeugten Baum anzeigen
					if(!meinSuffixBaum.dateinamenAnzeigen.equals("")){
						ausgabe = meinSuffixBaum.suffixBaumString;
					}
					else{
						ausgabe = meinSuffixBaum.ergebnisListe[0];
					}
				}
				myTextArea.setText(ausgabe);
				repaint();
			}
			// FREQUENZLISTE
			else if( cmd.equals("2) Frequenzliste der Blaetter") ){
				String ausgabe;
				if(meinSuffixBaum.knotenListe.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthaelt)
					ausgabe = "Bitte zuerst einen Suffixbaum erzeugen";
				}
				else{
					String freqListe = meinSuffixBaum.ergebnisListe[1];
					freqListe = "Frequenzliste der Blaetter:\n(Datei '" +meinSuffixBaum.dateinamenAnzeigen+ "')\n\n" 
								+ freqListe;
					ausgabe = freqListe;
				}
				myTextArea.setText(ausgabe);	
				repaint();
			}
			// LISTE DER HAEUFIGSTEN PRAEFIXE
			else if( cmd.equals("4) Haeufigkeit der Praefixe >= Schwelle") ){
				String ausgabe = new String();
				if(meinSuffixBaum.knotenListe.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthaelt)
					ausgabe = "Bitte zuerst einen Suffixbaum erzeugen";
				}
				else{
					haeufig.clear();
					StringReader stringRead = new StringReader(meinSuffixBaum.ergebnisListe[1]);
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
							// Liste der haeufigen Praefixe nach Laenge sortieren, damit jeweils der laengste Praefix fuer ein Wort gefunden wird
							Collections.sort(haeufig, new SwingComparator());
							ausgabe = "Mind. "+ schwellenWert +"mal vorkommende Praefixe:\n(Datei '" + meinSuffixBaum.dateinamenAnzeigen + "')\n\n";
							for(ListIterator it = haeufig.listIterator(); it.hasNext(); ){
								ausgabe += it.next() + "\n";
							}
						}else{
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
			// SUFFIXMENGEN DER PRAEFIXE
			else if( cmd.equals("5) Suffixmengen der haeufigsten Praefixe") ){	
				String ausgabe = new String();
				if(haeufig.isEmpty()){
					ausgabe = "Bitte erst die Liste der haeufigsten Praefixe erzeugen!";
				}	
				else{		
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					for(Iterator it = haeufig.iterator(); it.hasNext(); ){
						String praefix = (String)it.next();
						ausgabe += "\n\nFuer das Praefix \"" + praefix + "\": \n\n";
						
						if(meinSuffixBaum.suffixMengen.containsKey(praefix)){
							for(Iterator iter = meinSuffixBaum.suffixMengen.get(praefix).iterator(); iter.hasNext(); ){
								// die SUFFIXE muessen umgedreht werden
								String next = (String)iter.next();
								StringBuffer nextBuf = new StringBuffer(next);
								nextBuf.reverse();
								String nextUmgedreht = nextBuf.toString();
								ausgabe += praefix + " + " + nextUmgedreht + "\n";
							}
							ausgabe += "-------------------------------------\n\n";
						}	
					}
					String titel = "Suffixmengen der mind. " + schwellenWert + "mal vorkommenden Praefixe: \n(Datei '" +meinSuffixBaum.dateinamenAnzeigen+ "')";
					ausgabe = titel + ausgabe;
					cur = new Cursor(Cursor.DEFAULT_CURSOR); 					
					meinFenster.setCursor(cur);
				}
				myTextArea.setText(ausgabe);	
				repaint();
			}
            // PRAEFIXMENGEN DER SUFFIXE
			else if( cmd.equals("6) Praefixmengen dieser Suffixe")){
				String ausgabe = new String();
				if(haeufig.isEmpty()){
					ausgabe = "Bitte erst die Suffixmengen erzeugen!";
				}
				else{	
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					ausgabe = new String();
					TreeSet<String> zwischenMenge = new TreeSet<String>();		
					
					for(ListIterator it = haeufig.listIterator(); it.hasNext(); ){
						String next = (String)it.next();
						zwischenMenge = meinSuffixBaum.suffixMengen.get(next);
					
						//  fuer jedes der haeufigen Praefixe, alle Suffixe in allgemeineSuffixMenge speichern
						for(Iterator iter = zwischenMenge.iterator(); iter.hasNext(); ){
							String aktuellesSuffix = (String)iter.next();
							// Suffix wird vor dem Speichern umgedreht
							StringBuffer buf = new StringBuffer(aktuellesSuffix);
							buf.reverse();
							String sufUmgedreht = buf.toString();
							allgemeineSuffixMenge.add(sufUmgedreht);
						}
					}
							
					for(Iterator it = allgemeineSuffixMenge.iterator(); it.hasNext(); ){
						String aktuellesSuffix = (String)it.next();
						// neues Speicherobjekt anlegen
						temp = new TreeSet<String>();
						// sucheAnfaengeInBaum speichert alle moeglichen Praefixe des Suffixes in temp
						// diese muessen aber gleichzeitig auch in der Menge der haeufigsten Praefixe sein, sonst entsteht Schrott
						
						// Fuer die Suche im Baum muss das aktuelleSuffix umgedreht werden:
						StringBuffer buffer = new StringBuffer(aktuellesSuffix);
						buffer.reverse();
						String sufRev = buffer.toString();
						sucheAnfaengeImBaum(sufRev,0);
						if(!temp.isEmpty()){
							// Praefixmengen abspeichern
							praefixMengen.put(aktuellesSuffix,temp);
							ausgabe += "-"+ aktuellesSuffix+"\n";
							
							for(Iterator iterator = temp.iterator(); iterator.hasNext(); ){
								ausgabe += "\t"+iterator.next()+ "-\n";
							}
						}
						else{
							if(!aktuellesSuffix.equals("")){
								System.out.println("Nicht im Baum wiedergefunden: "+aktuellesSuffix);
							}
						}
					}
					ausgabe = "Fuer jedes Suffix die Menge der haeufigen Praefixe: \n(Datei '" +meinSuffixBaum.dateinamenAnzeigen+ "')\n\n" + ausgabe;
					cur = new Cursor(Cursor.DEFAULT_CURSOR); 
					meinFenster.setCursor(cur);
				}	
				myTextArea.setText(ausgabe);	
				repaint();
			}
			// KLASSEN
			else if(cmd.equals("7) Klassen")){
				String ausgabe = new String();
				if(praefixMengen.isEmpty()){
					ausgabe = "Bitte erst die Praefixmengen erzeugen!";
				}
				else{
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					ausgabe = "Klassen von Strings mit denselben (haeufigen) Praefixen: \n(Datei '" +meinSuffixBaum.dateinamenAnzeigen+ "')\n\n" + ausgabe;
					
					TreeSet<String> praefixmenge1 = new TreeSet<String>();
					TreeSet<String> praefixmenge2 = new TreeSet<String>();
					// Variablen zum Zwischenspeichern
					TreeSet<String> menge; 
					
					for(Iterator it = allgemeineSuffixMenge.iterator(); it.hasNext(); ){
						String suffix1 = (String)it.next();
						if(praefixMengen.containsKey(suffix1)){
							praefixmenge1 = praefixMengen.get(suffix1);					
							for(Iterator iter = allgemeineSuffixMenge.iterator(); iter.hasNext(); ){
								String suffix2 = (String)iter.next();
								if(!(suffix1.equals(suffix2))){
									if(praefixMengen.containsKey(suffix2)){
										praefixmenge2 = praefixMengen.get(suffix2);
										if(praefixmenge1.containsAll(praefixmenge2) && praefixmenge2.containsAll(praefixmenge1)){
											Boolean eingefuegt = false;
											if( !(klassen.isEmpty()) ){
									
												for(ListIterator iterat = klassen.listIterator(); iterat.hasNext(); ){
													menge = (TreeSet)iterat.next();
													if( menge.contains(suffix1)){
														menge.add(suffix2);
														eingefuegt = true;
														break;
													}
													else if(menge.contains(suffix2)){
														menge.add(suffix1);
														eingefuegt = true;
														break;
													}
												}
											}
											// Wenn es noch gar keine klassen-Menge gibt, wird hier die erste angelegt
											// Wenn die Suffixe noch in keiner Menge sind, wird eine neue Menge angelegt
											if(klassen.isEmpty() || !eingefuegt){
												menge = new TreeSet();
												menge.add(suffix1);
												menge.add(suffix2);
												klassen.add(menge);
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
						menge2 = praefixMengen.get(erstesPraefix);
						ausgabe += "Praefix(e) {  ";
						for(Iterator iter = menge2.iterator(); iter.hasNext(); ){
							String praefix = (String)iter.next();
							ausgabe += praefix + "-  ";
						}
						ausgabe += "}\n\t";
					
						for(Iterator iterat = menge1.iterator(); iterat.hasNext(); ){
							ausgabe += "-"+(String)iterat.next()+"  ";
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
	
	// Diese Methode speichert die Menge der moeglichen Praefixe eines Strings in 'temp'
	// und wird mit Index 0 aufgerufen
	// (diese Praefixe muessen aber gleichzeitig auch in der Menge der haeufigsten Praefixe sein)
	
	public void sucheAnfaengeImBaum(String suffix,int index){
		Node aktuellerKnoten = meinSuffixBaum.knotenListe.get(index);
		Set keys = aktuellerKnoten.kinder.keySet();
		for(Iterator it = keys.iterator(); it.hasNext(); ){
			Integer kindIndex = aktuellerKnoten.kinder.get( (Character)it.next() );
			Node kind = meinSuffixBaum.knotenListe.get( kindIndex );
			if(suffix.startsWith(kind.inhalt)){
				String restSuffix = suffix.substring( kind.inhalt.length() );
				if( restSuffix.equals("")){
					Set keys2 = kind.kinder.keySet();
					for(Iterator iter = keys2.iterator(); iter.hasNext(); ){
						Integer kindesKindIndex = kind.kinder.get( (Character)iter.next() );
						Node kindesKind = meinSuffixBaum.knotenListe.get( kindesKindIndex );
						if(kindesKind.inhalt.endsWith("$")){
							String praefixOhneDollar = kindesKind.inhalt.replaceAll("\\$","");
							// Inhalt von praefixOhneDollar muss vor dem Speichern in temp umgedreht werden:
							StringBuffer buffer = new StringBuffer(praefixOhneDollar);
							buffer.reverse();
							String praefixUmgedreht = buffer.toString();
							if(haeufig.contains(praefixUmgedreht) ){
								temp.add(praefixUmgedreht);
							}
						}
						else{
							String blatt = kindesKind.inhalt;
							sucheBlaetterImBaum(kindesKindIndex, blatt);
						}
  					}
				}
				else{
					sucheAnfaengeImBaum(restSuffix, kindIndex);
				}
			}
		}
	}
	
	public void sucheBlaetterImBaum(Integer index, String blatt){
		String leaf = blatt;
		Node aktuellerKnoten = meinSuffixBaum.knotenListe.get(index);
		Set keys = aktuellerKnoten.kinder.keySet();
		for(Iterator it = keys.iterator(); it.hasNext(); ){
			Integer kindIndex = aktuellerKnoten.kinder.get( (Character)it.next() );
			Node kind = meinSuffixBaum.knotenListe.get( kindIndex );
			if(kind.inhalt.endsWith("$")){
				leaf += kind.inhalt;
				leaf = leaf.replaceAll("\\$","");
				// leaf muss umgedreht werden, da die Strins in haeufig richtigherum drinstehen:
 				StringBuffer buf = new StringBuffer(leaf);
 				buf.reverse();
 				String blattUmgedreht = buf.toString();
				if(haeufig.contains(blattUmgedreht)){
					temp.add(blattUmgedreht);
				}
				leaf = blatt;
			}
			else{
				leaf += kind.inhalt;
				sucheBlaetterImBaum(kindIndex, leaf);
			}
		}
	}
	
	public class Menuleiste extends MenuBar{ 
		public Menuleiste(){
			super();
			this.add(new Menu("Datei"));
			MenuItem baumSpeichern = new MenuItem("Baum speichern");
			baumSpeichern.addActionListener(new MenuActionListener());
			this.getMenu(0).add(baumSpeichern);
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
				else if(cmd.equals("Baum speichern")){
					
					// Dateinamen waehlen:
					FileDialog d = new FileDialog(meinFenster, "Speichern...", FileDialog.SAVE);
					d.pack(); 
					d.setVisible(true);
					String dateiname = d.getFile();
					String pfad = d.getDirectory() + dateiname;
					d.dispose();
					
					if(!pfad.equals("nullnull")){
						try{
							BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(pfad));
							ObjectOutputStream oos = new ObjectOutputStream(fos);
							oos.writeObject(meinFenster.meinSuffixBaum);
							oos.close();
							myTextArea.setText("Der Baum wurde gespeichert unter '"+ pfad +"'\n\n\n"+
									"Soll dieser Baum mittels '1) Gespeicherten Baum einlesen' zugaenglich\n" +
									"sein, muss die Datei umbenannt werden in 'Baum_praefixe.tmp' und\n" +
									"im Programmverzeichnis liegen.");	
							repaint();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
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
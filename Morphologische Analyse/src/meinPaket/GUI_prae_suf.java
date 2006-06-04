///////////////////////////////////////
/// Projekt: Morphologische Analyse 
/// Klasse: GUI_prae_suf.java	
/// Paket: meinPaket
/// Autor: Eva Hasler				
/// Datum: 8.4.06					
///////////////////////////////////////

package meinPaket;

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

import meinPaket.GUI_suffixe_Ukkonen.Menuleiste.MenuActionListener;

import suffixComparator.SuffixComparator;


public class GUI_prae_suf extends JFrame{
	
	// aktuelles Fenster
	public static GUI_prae_suf meinFenster;
	public Tree_praefixe meinSuffixBaum1 = new Tree_praefixe();
	public Tree_suffixe meinSuffixBaum2 = new Tree_suffixe();
	
	// aus TextField
	String eingabeString1 = "";
	String eingabeString2 = "";
	Integer schwellenWert1 = null;
	Integer schwellenWert2 = null;
	
	String dateiPfad1 = "";
	String dateiPfad2 = "";
		
	private LinkedList<String> haeufig1 = new LinkedList<String>();
	private LinkedList<String> haeufig2 = new LinkedList<String>();
	
	// zum Zwischenspeichern von Mengen
	TreeSet<String> temp1 = new TreeSet<String>();
	TreeSet<String> temp2 = new TreeSet<String>();
	
	// Liste der Suffixe, die mit den haeufigsten Praefixen vorkommen
	public TreeSet<String> allgemeineSuffixMenge = new TreeSet<String>();
	// Liste der Praefixe, die mit den haeufigsten Suffixen vorkommen
	public TreeSet<String> allgemeinePraefixMenge = new TreeSet<String>();
	
	// zu jedem Suffix die Praefixmenge
	public TreeMap<String,TreeSet<String>> praefixMengen = new TreeMap<String,TreeSet<String>>();
	// zu jedem Praefix die Suffixmenge
	public TreeMap<String,TreeSet<String>> suffixMengen = new TreeMap<String,TreeSet<String>>();
	
	// hier werden die Klassen von zusammengehoerigen Woertern/Morphemen gespeichert
	LinkedList<TreeSet> klassen1 = new LinkedList<TreeSet>();
	LinkedList<TreeSet> klassen2 = new LinkedList<TreeSet>();
	
	// AnzeigeFenster
	JTextArea myTextArea;
	TextField schwellenFeld1;
	TextField schwellenFeld2;
	
	Color gelb = new Color(0.95f, 0.95f, 0.7f);
	Color weiß = new Color(1.0f, 1.0f, 1.0f);
		
	public GUI_prae_suf(String titel){
		super(titel);
		setBackground(Color.lightGray);
		//setSize(850,1010);
		//setLocation(350,0);
		setSize(750,700);
		setLocation(170,20);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// JTextArea
		myTextArea = new JTextArea();
		myTextArea.setBackground(gelb);
		myTextArea.setEditable(false);
		myTextArea.addMouseListener(new MyMouseListener());
		myTextArea.addMouseMotionListener(new MyMouseMotionListener());
		JScrollPane scrollpane = new JScrollPane(myTextArea);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
		JPanel panel = new JPanel();
		GridLayout grid2 = new GridLayout(9,1);
		grid2.setVgap(5);
		panel.setLayout(grid2);
		
		JButton knopf1 = new JButton("(Praefix-Baum zeigen)");
		knopf1.addActionListener(new MyActionListener());
		JButton knopf2 = new JButton("(Suffix-Baum zeigen)");
		knopf2.addActionListener(new MyActionListener());
		JButton knopf3 = new JButton("Frequenzliste der Blaetter(Praefixe)");
		knopf3.addActionListener(new MyActionListener());
		JButton knopf4 = new JButton("Frequenzliste der Blaetter(Suffixe)");
		knopf4.addActionListener(new MyActionListener());
		JLabel label1 = new JLabel("    1) Schwellenwert(int) eingeben:(Praefixe)");
		schwellenFeld1 = new TextField();
		schwellenFeld1.addTextListener(new MyTextListener());
		JLabel label2 = new JLabel("    2) Schwellenwert(int) eingeben:(Suffixe)");
		schwellenFeld2 = new TextField();
		schwellenFeld2.addTextListener(new MyTextListener());
		JButton knopf5 = new JButton("3) Berechnung");
		knopf5.addActionListener(new MyActionListener());
		JButton knopf6 = new JButton("4) Klassen zeigen(Praefixe)");
		knopf6.addActionListener(new MyActionListener());
		JButton knopf7 = new JButton("5) Klassen zeigen(Suffixe)");
		knopf7.addActionListener(new MyActionListener());
		
		JPanel panel2 = new JPanel();
		panel2.setBackground(Color.white);
		panel2.setLayout(new GridLayout(2,1));
		panel2.add(label1);
		panel2.add(schwellenFeld1);
		
		JPanel panel3 = new JPanel();
		panel3.setBackground(Color.white);
		panel3.setLayout(new GridLayout(2,1));
		panel3.add(label2);
		panel3.add(schwellenFeld2);
		
		panel.add(knopf1);
		panel.add(knopf2);
		panel.add(knopf3);
		panel.add(panel2);
		panel.add(knopf4);
		panel.add(panel3);
		panel.add(knopf5);
		panel.add(knopf6);
		panel.add(knopf7);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		getContentPane().add(panel, BorderLayout.EAST);
		
		Menuleiste ml = new Menuleiste();
		this.setMenuBar(ml);
		setVisible(true);
		
		// GESPEICHERTE BAEUME EINLESEN:
		String ausgabe = new String();
		try{
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream("Baum_praefixe.tmp"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			meinSuffixBaum1 = (meinPaket.Tree_praefixe)ois.readObject();
		}catch(FileNotFoundException e){
			ausgabe = "Datei Baum_praefixe.tmp nicht gefunden!\n";
			myTextArea.setText(ausgabe);
			repaint();
		}catch(IOException e){
			ausgabe = "Fehler beim Lesen der Datei Baum_praefixe.tmp!";
			myTextArea.setText(ausgabe);
			repaint();
		}catch(Exception e){
			ausgabe = "Unbekannter Fehler bei Datei Baum_praefixe.tmp!";
			myTextArea.setText(ausgabe);
			repaint();
			e.printStackTrace();
		}
		
		try{
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream("Baum_suffixe.tmp"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			meinSuffixBaum2 = (meinPaket.Tree_suffixe)ois.readObject();
		}catch(FileNotFoundException e){
			ausgabe += "Datei Baum_suffixe.tmp nicht gefunden!\n";
			myTextArea.setText(ausgabe);
			repaint();
		}catch(IOException e){
			ausgabe += "Fehler beim Lesen der Datei Baum_suffixe.tmp!";
			myTextArea.setText(ausgabe);
			repaint();
		}catch(Exception e){
			ausgabe += "Unbekannter Fehler bei Datei Baum_suffixe.tmp!";
			myTextArea.setText(ausgabe);
			repaint();
			e.printStackTrace();
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
			if(tf.equals(schwellenFeld1)){
				try{
					String eingabe = tf.getText();
					if(!eingabe.equals("")){
						schwellenWert1 = Integer.parseInt( eingabe );
					}
				}catch(Exception e){
					myTextArea.setText("Schwellenwert muss Integer sein!");	
					repaint();
				}
			}
			else if(tf.equals(schwellenFeld2)){
				try{
					String eingabe = tf.getText();
					if(!eingabe.equals("")){
						schwellenWert2 = Integer.parseInt( eingabe );
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
			// Schriftgroeße  fuer den Text setzen
			Font f = new Font ("Serif",Font.PLAIN,20);
			myTextArea.setFont(f);
			String cmd = event.getActionCommand();

			// BAUM ZEIGEN 
			if( cmd.equals("(Praefix-Baum zeigen)")){				
				String ausgabe;
				if(meinSuffixBaum1.knotenListe.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthaelt)
					ausgabe = "Bitte zuerst einen Präfixbaum erzeugen";
				}
				else{
					// zuletzt erzeugten Baum anzeigen
					if(!meinSuffixBaum1.dateinamenAnzeigen.equals("")){
						String titel = "Praefix-Baum aus Datei '" + meinSuffixBaum1.dateinamenAnzeigen + "'\n\n";
						ausgabe = titel + meinSuffixBaum1.ergebnisListe[0];
					}
					else{
						ausgabe = meinSuffixBaum1.ergebnisListe[0];
					}
				}
				myTextArea.setText(ausgabe);
				repaint();
			}
			else if( cmd.equals("(Suffix-Baum zeigen)")){				
				String ausgabe;
				if(meinSuffixBaum2.knotenListe.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthaelt)
					ausgabe = "Bitte zuerst einen Suffixbaum erzeugen";
				}
				else{
					// zuletzt erzeugten Baum anzeigen
					if(!meinSuffixBaum2.dateinamenAnzeigen.equals("")){
						String titel = "Suffix-Baum aus Datei '" + meinSuffixBaum2.dateinamenAnzeigen + "'\n\n";
						ausgabe = titel + meinSuffixBaum2.ergebnisListe[0];
					}
					else{
						ausgabe = meinSuffixBaum2.ergebnisListe[0];
					}
				}
				myTextArea.setText(ausgabe);
				repaint();
			}
			// FREQUENZLISTE
			else if( cmd.equals("Frequenzliste der Blaetter(Praefixe)") ){
				String ausgabe;
				if(meinSuffixBaum1.knotenListe.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthlt)
					ausgabe = "Bitte zuerst einen Präfixbaum erzeugen";
				}
				else{
					String freqListe = meinSuffixBaum1.ergebnisListe[1];
					freqListe = "Frequenzliste der Blaetter:\n(Datei '" +meinSuffixBaum1.dateinamenAnzeigen+ "')\n\n" 
								+ freqListe;
					ausgabe = freqListe;
				}
				myTextArea.setText(ausgabe);	
				repaint();
			}
			else if( cmd.equals("Frequenzliste der Blaetter(Suffixe)") ){
				String ausgabe;
				if(meinSuffixBaum2.knotenListe.size() == 1){
					// (dies ist der Fall, wenn die Knotenliste nur den root-Knoten enthlt)
					ausgabe = "Bitte zuerst einen Suffixbaum erzeugen";
				}
				else{
					String freqListe = meinSuffixBaum2.ergebnisListe[1];
					freqListe = "Frequenzliste der Blaetter:\n(Datei '" +meinSuffixBaum2.dateinamenAnzeigen+ "')\n\n" 
								+ freqListe;
					ausgabe = freqListe;
				}
				myTextArea.setText(ausgabe);	
				repaint();
			}
			// KOMPLETTE BERECHNUNG
			else if( cmd.equals("3) Berechnung")){
				if((meinSuffixBaum1.ergebnisListe.length > 1) && (meinSuffixBaum2.ergebnisListe.length > 1)){
				StringReader stringRead1 = new StringReader(meinSuffixBaum1.ergebnisListe[1]);
				String buffer1 = new String();
				int buchstabe1;
				Boolean weiter1 = true;
				
				StringReader stringRead2 = new StringReader(meinSuffixBaum2.ergebnisListe[1]);
				String buffer2 = new String();
				int buchstabe2;
				Boolean weiter2 = true;
				
				if(schwellenWert1 != null && schwellenWert2 != null){
					Cursor cur = new Cursor(Cursor.WAIT_CURSOR);
					meinFenster.setCursor(cur);
					while(weiter1){
						try{
							buchstabe1 = stringRead1.read();
							buffer1 = buffer1 + (char)buchstabe1;
							if(buchstabe1 == 10 || buchstabe1 == 13){
								String[] s = buffer1.split("\\s+");
								Integer anzahl = Integer.parseInt(s[0]);
								if(anzahl > schwellenWert1 || anzahl == schwellenWert1){
									String nurChars = s[1];
									haeufig1.add(nurChars);
								}
								else{
									weiter1 = false;
								}
								buffer1 = "";
							}
							else if(buchstabe1 == -1){
								weiter1 = false;
							}
						}catch(IOException e){ 
							System.out.println("Fehler beim Lesen des Strings");	
						}
					}
					Collections.sort(haeufig1, new SuffixComparator());
						
					while(weiter2){
						try{
							buchstabe2 = stringRead2.read();
							buffer2 = buffer2 + (char)buchstabe2;
							if(buchstabe2 == 10 || buchstabe2 == 13){
								String[] s = buffer2.split("\\s+");
								Integer anzahl = Integer.parseInt(s[0]);
								if(anzahl > schwellenWert2 || anzahl == schwellenWert2){
									String nurChars = s[1];
									haeufig2.add(nurChars);
								}
								else{
									weiter2 = false;
								}
								buffer2 = "";
							}
							else if(buchstabe2 == -1){
								weiter2 = false;
							}
						}catch(IOException e){ 
							System.out.println("Fehler beim Lesen des Strings");	
						}
					}
					Collections.sort(haeufig2, new SuffixComparator());
					
					// Vor dem Weiterrechnen testen, ob die haeufig-Mengen leer sind:
					if(!haeufig1.isEmpty() && !haeufig2.isEmpty()){
						TreeSet<String> zwischenMenge1 = new TreeSet<String>();	
						for(ListIterator it = haeufig1.listIterator(); it.hasNext(); ){
							String next = (String)it.next();
							zwischenMenge1 = meinSuffixBaum1.suffixMengen.get(next);
						
							//  fuer jedes der haeufigen Praefixe, alle Suffixe in allgemeineSuffixMenge speichern
							for(Iterator iter = zwischenMenge1.iterator(); iter.hasNext(); ){
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
							temp1 = new TreeSet<String>();
							StringBuffer buffer = new StringBuffer(aktuellesSuffix);
							buffer.reverse();
							String sufRev = buffer.toString();
							sucheAnfaengeImBaum(sufRev,0);
							if(!temp1.isEmpty()){
								// Praefixmengen abspeichern
								praefixMengen.put(aktuellesSuffix,temp1);
							}
						}
				
						TreeSet<String> zwischenMenge2 = new TreeSet<String>();		
						for(ListIterator it = haeufig2.listIterator(); it.hasNext(); ){
							String next = (String)it.next();
							zwischenMenge2 = meinSuffixBaum2.praefixMengen.get(next);
							// fuer jedes der haeufigen Suffixe, alle Praefixe in allgemeinePraefixMenge speichern
							for(Iterator iter = zwischenMenge2.iterator(); iter.hasNext(); ){
								String aktuellesPraefix = (String)iter.next();
								allgemeinePraefixMenge.add(aktuellesPraefix);
							}
						}
						for(Iterator it = allgemeinePraefixMenge.iterator(); it.hasNext(); ){
							String aktuellesPraefix = (String)it.next();
							// neues Speicherobjekt anlegen
							temp2 = new TreeSet<String>();
							// sucheEndungenInBaum speichert alle moeglichen Suffixe des Praefixes in temp
							// diese muessen aber gleichzeitig auch in der Menge der haeufigsten Suffixe sein, sonst entsteht Schrott
							sucheEndungenImBaum(aktuellesPraefix,0);
							// Suffixmengen abspeichern
							suffixMengen.put(aktuellesPraefix,temp2);
						}
						
						// KLASSEN
				
						TreeSet<String> praefixmenge1 = new TreeSet<String>();
						TreeSet<String> praefixmenge2 = new TreeSet<String>();
						// Variablen zum Zwischenspeichern
						TreeSet<String> Menge1; 	
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
												if( !(klassen1.isEmpty()) ){
													
													for(ListIterator iterat = klassen1.listIterator(); iterat.hasNext(); ){
														Menge1 = (TreeSet)iterat.next();
														if( Menge1.contains(suffix1)){
															Menge1.add(suffix2);
															eingefuegt = true;
															break;
														}
														else if(Menge1.contains(suffix2)){
															Menge1.add(suffix1);
															eingefuegt = true;
															break;
														}
													}
												}
												// Wenn es noch gar keine klassen-Menge gibt, wird hier die erste angelegt
												// Wenn die Suffixe noch in keiner Menge sind, wird eine neue Menge angelegt
												if(klassen1.isEmpty() || !eingefuegt){
													Menge1 = new TreeSet();
													Menge1.add(suffix1);
													Menge1.add(suffix2);
													klassen1.add(Menge1);
												}
											}
										}
									}	
								}
							}
						}
						
						TreeSet<String> suffixmenge1 = new TreeSet<String>();
						TreeSet<String> suffixmenge2 = new TreeSet<String>();
						// Variablen zum Zwischenspeichern
						TreeSet<String> Menge2; 
						
						for(Iterator it = allgemeinePraefixMenge.iterator(); it.hasNext(); ){
							String praefix1 = (String)it.next();
							if(suffixMengen.containsKey(praefix1)){
								suffixmenge1 = suffixMengen.get(praefix1);
								for(Iterator iter = allgemeinePraefixMenge.iterator(); iter.hasNext(); ){
									String praefix2 = (String)iter.next();
									if(!(praefix1.equals(praefix2))){
										if(suffixMengen.containsKey(praefix2)){
											suffixmenge2 = suffixMengen.get(praefix2);
											if(suffixmenge1.containsAll(suffixmenge2) && suffixmenge2.containsAll(suffixmenge1)){
												Boolean eingefuegt = false;
												if( !(klassen2.isEmpty()) ){
													for(ListIterator iterat = klassen2.listIterator(); iterat.hasNext(); ){
														Menge2 = (TreeSet)iterat.next();
														if( Menge2.contains(praefix1)){
															Menge2.add(praefix2);
															eingefuegt = true;
															break;
														}
														else if(Menge2.contains(praefix2)){
															Menge2.add(praefix1);
															eingefuegt = true;
															break;
														}
													}
												}
												// Wenn es noch gar keine klassen-Menge gibt, wird hier die erste angelegt
												// Wenn die Praefixe noch in keiner Menge sind, wird eine neue Menge angelegt
												if(klassen2.isEmpty() || !eingefuegt){
													Menge2 = new TreeSet();
													Menge2.add(praefix1);
													Menge2.add(praefix2);
													klassen2.add(Menge2);
												}
											}
										}
									}	
								}
							}
						}
						cur = new Cursor(Cursor.DEFAULT_CURSOR); 
						meinFenster.setCursor(cur);
						myTextArea.setText("Berechnung fertig!");
					}
					else{
						cur = new Cursor(Cursor.DEFAULT_CURSOR); 
						meinFenster.setCursor(cur);
						String ausgabe = new String();
						if(haeufig1.isEmpty()){
							ausgabe += "Keine Praefixe mit Haeufigkeit > "+schwellenWert1+ " vorhanden!\n\n";
							myTextArea.setText(ausgabe);
							repaint();
						}
						if(haeufig2.isEmpty()){
							ausgabe += "Keine Suffixe mit Haeufigkeit > "+schwellenWert2+ " vorhanden!";
							myTextArea.setText(ausgabe);
							repaint();
						}
					}
				}
				else{
					myTextArea.setText("Bitte beide Schwellenwerte eingeben!");	
					repaint();
				}
				}else{
					String ausgabe = new String();
					if(meinSuffixBaum1.ergebnisListe.length == 1){
						ausgabe += "Bitte erst den Präfixbaum erzeugen!\n";
					}
					if(meinSuffixBaum2.ergebnisListe.length == 1){
						ausgabe += "Bitte erst den Suffixbaum erzeugen!\n";
					}
					myTextArea.setText(ausgabe);
					repaint();
				}
			}
			else if( cmd.equals("4) Klassen zeigen(Praefixe)") ){
				if(!klassen1.isEmpty()){
					String ausgabe = new String();
					TreeSet<String> menge1;
					TreeSet<String> menge2;
					for(ListIterator it = klassen1.listIterator(); it.hasNext(); ){
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
					ausgabe = "Klassen von Strings mit denselben (haeufigen) Praefixen: \n(Datei '" +meinSuffixBaum1.dateinamenAnzeigen+ "')\n\n" + ausgabe;
					myTextArea.setText(ausgabe);
				}
				else{
					myTextArea.setText("Bitte zuerst die Berechnung durchführen!");
				}
			}
			else if( cmd.equals("5) Klassen zeigen(Suffixe)") ){
				if(!klassen2.isEmpty()){
					String ausgabe = new String();
					TreeSet<String> menge1;
					TreeSet<String> menge2;
					for(ListIterator it = klassen2.listIterator(); it.hasNext(); ){
						menge1 = (TreeSet)it.next();
						String erstesPraefix = menge1.first();
						menge2 = new TreeSet<String>();
						menge2 = suffixMengen.get(erstesPraefix);
						ausgabe += "Suffix(e) {  ";
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
					ausgabe = "Klassen von Strings mit denselben (haeufigen) Suffixen: \n(Datei '" +meinSuffixBaum2.dateinamenAnzeigen+ "')\n\n"+ausgabe;
					myTextArea.setText(ausgabe);
				}
				else{
					myTextArea.setText("Bitte zuerst die Berechnung durchführen!");
				}
			}
		}
	}
		
	// Diese Methode speichert die Menge der moeglichen Praefixe eines Strings in 'temp'
	// und wird mit Index 0 aufgerufen
	// (diese Praefixe muessen aber gleichzeitig auch in der Menge der haeufigsten Praefixe sein)
	
	public void sucheAnfaengeImBaum(String suffix,int index){
		Node aktuellerKnoten = meinSuffixBaum1.knotenListe.get(index);
		Set keys = aktuellerKnoten.kinder.keySet();
		for(Iterator it = keys.iterator(); it.hasNext(); ){
			Integer kindIndex = aktuellerKnoten.kinder.get( (Character)it.next() );
			Node kind = meinSuffixBaum1.knotenListe.get( kindIndex );
			if(suffix.startsWith(kind.inhalt)){
				String restSuffix = suffix.substring( kind.inhalt.length() );
				if( restSuffix.equals("")){
					Set keys2 = kind.kinder.keySet();
					for(Iterator iter = keys2.iterator(); iter.hasNext(); ){
						Integer kindesKindIndex = kind.kinder.get( (Character)iter.next() );
						Node kindesKind = meinSuffixBaum1.knotenListe.get( kindesKindIndex );
						if(kindesKind.inhalt.endsWith("$")){
							String praefixOhneDollar = kindesKind.inhalt.replaceAll("\\$","");
							// Inhalt von praefixOhneDollar muss vor dem Speichern in temp umgedreht werden:
							StringBuffer buffer = new StringBuffer(praefixOhneDollar);
							buffer.reverse();
							String praefixUmgedreht = buffer.toString();
							if(haeufig1.contains(praefixUmgedreht) ){
								temp1.add(praefixUmgedreht);
							}
						}
						else{
							String blatt = kindesKind.inhalt;
							sucheBlaetterImBaum1(kindesKindIndex, blatt);
						}
  					}
				}
				else{
					sucheAnfaengeImBaum(restSuffix, kindIndex);
				}
			}
		}
	}
	
	public void sucheBlaetterImBaum1(Integer index, String blatt){
		String leaf = blatt;
		Node aktuellerKnoten = meinSuffixBaum1.knotenListe.get(index);
		Set keys = aktuellerKnoten.kinder.keySet();
		for(Iterator it = keys.iterator(); it.hasNext(); ){
			Integer kindIndex = aktuellerKnoten.kinder.get( (Character)it.next() );
			Node kind = meinSuffixBaum1.knotenListe.get( kindIndex );
			if(kind.inhalt.endsWith("$")){
				leaf += kind.inhalt;
				leaf = leaf.replaceAll("\\$","");
				// leaf muss umgedreht werden, da die Strins in haeufig richtigherum drinstehen:
 				StringBuffer buf = new StringBuffer(leaf);
 				buf.reverse();
 				String blattUmgedreht = buf.toString();
				if(haeufig1.contains(blattUmgedreht)){
					temp1.add(blattUmgedreht);
				}
				leaf = blatt;
			}
			else{
				leaf += kind.inhalt;
				sucheBlaetterImBaum1(kindIndex, leaf);
			}
		}
	}
	
	// Diese Methode speichert die Menge der moeglichen Suffixe eines Strings in 'temp'
	// und wird mit Index 0 aufgerufen
	// (diese Suffixe muessen aber gleichzeitig auch in der Menge der haeufigsten Suffixe sein)
	
	void sucheEndungenImBaum(String praefix, Integer m){
		Node mutter = meinSuffixBaum2.knotenListe.get(m);		
		Set keys = mutter.kinder.keySet();
		for(Iterator it = keys.iterator(); it.hasNext(); ){
			Node kind = meinSuffixBaum2.knotenListe.get( mutter.kinder.get( (Character)it.next() ) );
			String label = kind.inhalt;
			if(label.endsWith("$")){
				label = label.replaceAll("\\$","");
				if(label.startsWith(praefix)){
					String endung = label.substring( praefix.length() );
					if(haeufig2.contains(endung)){
						temp2.add(endung);
					}
				}
			}
			else{
				if(praefix.equals(label)){
					kinderSpeichern(meinSuffixBaum2.knotenListe.indexOf(kind), "");
				}
				else if(praefix.startsWith(label)){
					String restPraefix = praefix.substring( label.length() );
					sucheEndungenImBaum(restPraefix, meinSuffixBaum2.knotenListe.indexOf(kind) );
				}
			}
		}
	}
	
	void kinderSpeichern(Integer m, String e){
		Node mutter = meinSuffixBaum2.knotenListe.get(m);
		Set keys = mutter.kinder.keySet();
		for(Iterator it = keys.iterator(); it.hasNext(); ){
			Node kind = meinSuffixBaum2.knotenListe.get( mutter.kinder.get( (Character)it.next() ) );
			String label = kind.inhalt;
			if(label.endsWith("$")){
				String endung = e;
				label = label.replaceAll("\\$","");
				endung += label;
				if(haeufig2.contains(endung) && !endung.equals("")){
					temp2.add(endung);
				}
			}
			else{
				String endung = e;
				endung += label;
				kinderSpeichern(meinSuffixBaum2.knotenListe.indexOf(kind), endung);
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
			MenuItem schließen = new MenuItem("Schließen");
			schließen.addActionListener(new MenuActionListener());
			this.getMenu(0).add(schließen);
		}
		
		class MenuActionListener implements ActionListener{
			public void actionPerformed(ActionEvent event){
				String cmd = event.getActionCommand();
				if(cmd.equals("Aktuellen Text speichern")){	
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
				else if(cmd.equals("Schließen")){
					setVisible(false);
					dispose();
					System.exit(0);
				}
			}
		}
	}
}


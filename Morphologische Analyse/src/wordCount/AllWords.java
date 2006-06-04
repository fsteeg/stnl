/////////////////////////////////////////////
/// Projekt: Morphologische Analyse 	  
/// Autor: Eva Hasler					   
/// Klasse: AllWords.java	
/// Paket: wordCount
/// Quelle: http://www.uni-koeln.de/rrzk  
///   /kurse/unterlagen/java/oop/samples/ 
/// Datum: 8.4.06						  
/////////////////////////////////////////////



package wordCount;

// allwords3c: wie allwords3b, verwendet statt eines Vector eine HashMap,
// gibt aber die Werte abscholiessend auf ein Array aus und sortiert dieses nach
// Worthaeufigkeiten.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
// import java.util.*;  // ab JDK 1.2

public class AllWords {
    // Parsen von Woertern in einem Text (hier der Konsoleingabe).
    // Das Beispiel parst der Einfachheit halber anhand folgender Primitivsyntax:
    // Woerter sind zusammenhaengende Folgen von Nichtleerzeichen, getrennt durch
    // ein oder mehrere Leerzeichen.
    
    static HashMap<String,WordData> wordDatas = new HashMap<String,WordData>(500); 

    static StringBuffer buff = new StringBuffer(256); 
    // Zum Aufsammeln aller Zeichen des aktuellen Worts
    
    static int chValue;     // Das aktuelle Zeichen der Eingabe.
        
    public static String nextWord(Reader in) throws IOException {
        // Liest das naechste Wort vom Eingabestrom in und gibt es als Character Array
        // minimaler Laenge zurueck. Eventuelle fuehrende Leerzeichen werden ueberlesen.
        // Es werden maximal 256 Zeichen pro Wort zurueckgegeben. Ein eventueller Rest
        // wird ueberlesen.
        // Rueckgabewert: Referenz auf das Wort oder null, wenn kein Wort mehr folgte.
        // Pre: Auf chValue liegt der Code des aktuellen Zeichens des Eingabestroms
        // in bereit. Er ist nicht -1.
        // Post: Wenn ein naechstes Wort existierte, ist es eingelesen und das naechste
        // Zeichen ist auf chValue eingelesen, oder es ist das Ende der Eingabe erreicht
        // und es ist null zurueckgegeben worden.


    	
        buff.setLength(0); // Puffer initialisieren bzw. zuruecksetzen
        
        while (0 <= chValue && chValue <= ' ')
            chValue = in.read();
        if (chValue == -1)
            return null;

        do {
            // Gelesenenes Zeichen (in chValue) in buff zwischenspeichern.
            buff.append((char)chValue);
            chValue = in.read(); //
        }
        while ( chValue != -1 && chValue > ' ' );

        return buff.toString();
    }

    public static void main(String args[]) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, "Cp850"), true);
        BufferedReader in = new BufferedReader( new InputStreamReader(System.in, "Cp850") );

        out.println("AllWords3c: HashMap, Ausgabe der Values und Sortieren");
        out.println("-----------------------------------------------------");
        out.println();
        out.println("Bitte geben Sie Woerter ein. Beenden Sie mit Ctrl-Z nach Enter.");

        // Liest in einer Schleife ueber Aufrufe der Funktion nextWord
        // alle Woerter der Eingabe ein und protokolliert sie auf der Ausgabe.
        // Spaetere Varianten sollten:
        //  - die Woerter auf einem Feld speichern,
        //  - mehrfach auftretende Woerter nur einmal speichern,
        //  - die Syntax, was unter einem Wort verstanden wird, praxisnaeher
        //    elaborieren (vgl. nextWord).

        String word; 
        try {
            chValue = in.read();
            
            WordData wd;
            while (  (word = nextWord(in)) != null ) {
                wd = (WordData) wordDatas.get(word);
                if (wd == null) { // neues Wort am Ende anhaengen
                    wordDatas.put(word, new WordData(word));
                }
                else { // Wort war schon eingetragen
                    wd.count++;
                }
            }
            
            out.println(); // gut gegen Verschlucken von Ausgabe nach Eingabe
            
            Object[] values = wordDatas.values().toArray();
            Comparator c = new WordCountComparator();
            Arrays.sort(values, c);
            
            for (int i=0; i<values.length; i++) {
                wd = (WordData) values[i];
                out.print(wd.word); out.print(' '); out.println(wd.count);
            }

            out.println(); out.println();
        }
        catch (Throwable erx) { // erx: Error or Exception
            erx.printStackTrace();
        }
        finally {
            out.println();
            out.println("Bitte ein oder mehrmals Enter, um zu beenden.");
            in = new BufferedReader( new InputStreamReader(System.in, "Cp850") );
            in.readLine();
        }
    }
    
    static int indexOfWord(String word, Vector wordDatas) {
        
        int count = wordDatas.size();
        for (int i=0; i<count; i++) {
            if (word.compareTo(((WordData)wordDatas.elementAt(i)).word) == 0) {
                return i;
            }
        }
        return -1;
    }
}

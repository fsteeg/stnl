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

import java.util.Comparator;

public class WordCountComparator implements Comparator{
	public int compare(Object obj1, Object obj2) {
        return ((WordData)obj2).count - ((WordData)obj1).count;    
    }
    
    public boolean equals(Object other) {
        return other == this;
    }
}

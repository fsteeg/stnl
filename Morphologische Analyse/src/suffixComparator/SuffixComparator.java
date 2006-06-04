///////////////////////////////////////
/// Projekt: Morphologische Analyse 
/// Klasse: SuffixComparator.java	
/// Autor: Eva Hasler				
/// Datum: 8.4.06					
///////////////////////////////////////

package suffixComparator;

import java.util.Comparator;

public class SuffixComparator implements Comparator {
	
	public int compare(Object str1, Object str2) {
    	Integer laenge1 = (Integer)( ( (String)str1 ).length() );  
    	Integer laenge2 = (Integer)( ( (String)str2 ).length() ); 
    	return laenge2.compareTo( laenge1 );    
    }   
}
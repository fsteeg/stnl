///////////////////////////////////////
/// Projekt: Morphologische Analyse 
/// Klasse: SuffixComparator.java	
/// Autor: Eva Hasler				
/// Datum: 8.4.06					
///////////////////////////////////////

package de.uni_koeln.spinfo.strings.morpho;

import java.util.Comparator;
//TODO translate comments, variable names, gui
public class SwingComparator implements Comparator {
	
	public int compare(Object str1, Object str2) {
    	Integer laenge1 = (Integer)( ( (String)str1 ).length() );  
    	Integer laenge2 = (Integer)( ( (String)str2 ).length() ); 
    	return laenge2.compareTo( laenge1 );    
    }   
}
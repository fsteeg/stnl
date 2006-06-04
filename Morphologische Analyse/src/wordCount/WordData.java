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

public class WordData {
	String word;
    int count;
    public WordData(String word) {
        this.word = word;
        count = 1;
    }
    public void incrementCount() {
        this.count++;
    }
    public String getWord(){
    	return this.word;
    }
    public int getCount(){
    	return this.count;
    }
}

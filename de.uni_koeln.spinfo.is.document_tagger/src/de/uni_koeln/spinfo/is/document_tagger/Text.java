package de.uni_koeln.spinfo.is.document_tagger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Text {
    Set<String> tags = new HashSet<String>();
    String content = "";
    public String location;
    
    @Override
    public String toString() {
       StringBuilder builder = new StringBuilder();
       for(String s : tags){
           builder.append(","+s);
       }
       return builder.substring(1) + "\n\n" + content + "\n\n";
    }
    public Text(String content, Set<String> tags, String location) {
        this.content = content;
        this.tags = tags;
        this.location = location;
    }
}

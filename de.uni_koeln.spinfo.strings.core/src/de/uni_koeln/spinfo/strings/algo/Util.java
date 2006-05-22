package de.uni_koeln.spinfo.strings.algo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for loading and saving strings to and from files.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class Util {
    /**
     * Reads a file to a string, eliminating newline-characters
     * 
     * @param file
     *            The file to read
     * @return Returns the content of the file read, as a string
     */
public static String getText(File file) {
        StringBuilder text = new StringBuilder();
        try {
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = reader.readLine()) != null) {
                text.append(line).append(" ");
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    /**
     * Saves a string to a file
     * 
     * @param location
     *            The location to save the file to
     * @param content
     *            The content to be saved
     */
    public static void saveString(String location, String content) {
        File f = new File(location);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            writer.write(content.toCharArray());
            writer.close();
            System.out.println("Wrote output "
            // + content + " "
                    + "to: " + f.getAbsolutePath() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

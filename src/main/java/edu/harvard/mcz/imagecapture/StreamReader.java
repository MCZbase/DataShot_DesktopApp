/**
 * StreamReader.java
 * edu.harvard.mcz.imagecapture
 */
package edu.harvard.mcz.imagecapture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads an InputStream in a separate thread.
 * 
 * @author Paul J. Morris
 *
 */
public class StreamReader implements java.lang.Runnable {
    InputStream inputStream;
    String type;
    
    StreamReader(InputStream anInputStream, String type) {
        inputStream = anInputStream;
        this.type = type;
    }
    
    public void run() {
        try  {      
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line=null;
            while ( (line = bufferedReader.readLine()) != null)  {
                System.out.println(type + ">" + line);    
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }
}

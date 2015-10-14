/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import magick.MagickException;

/**
 *
 * @author lucien
 */
public class JobEstimator 
{
    public static void main(String[] args) {
        if( args.length != 3)
        {
            System.out.println("Too few args -- needs input, output and logging folder");
            System.exit(1);
        }
        
        File folder = new File(args[0]);
        File outputFolder = new File(args[1]);
        Logging logging = Logging.getInstance();
        logging.setInputDir(args[2]);
        
        if( folder.exists() && outputFolder.exists() )
        {
            // List all files in input folder args[0]
            File[] allFiles = folder.listFiles();
            for(File f : allFiles)
            {
                try {
                    // Loop over the files
                    String fileName = f.getName();
                    long startTime = System.currentTimeMillis();
                    System.out.println("Processing: " + f.getName());
                    Image image = new Image(f);
                    image.processImage();
                    image.write(args[1] + "out_" + fileName);
                    
                    long endTime = System.currentTimeMillis();
                    logging.addJobToLog(fileName, endTime-startTime);
                } catch (MagickException ex) {
                    Logger.getLogger(JobEstimator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }   
       
}

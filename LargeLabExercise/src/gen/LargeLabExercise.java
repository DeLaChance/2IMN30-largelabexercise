package gen;

import gen.AWS;
import java.util.logging.Level;
import java.util.logging.Logger;
import magick.MagickException;
import master.JobPicker;
import both.Monitor;
import both.NetworkThread;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import master.Master;
import master.Scheduler;
import slave.Slave;

/**
 *
 * @author lucien
 */
public class LargeLabExercise {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) 
//    {
//        if( args[0].equals("slave") )
//        {
//            // Need to start monitor, processor
//            Slave slave = new Slave();
//            slave.run();
//        }
//        else 
//        {
//            Master master = Master.getInstance();
//            master.run();
//            System.exit(0);
//        }
//
//    }
    
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

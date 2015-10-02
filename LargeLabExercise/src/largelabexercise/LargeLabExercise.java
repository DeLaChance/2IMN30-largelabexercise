package largelabexercise;

import java.util.logging.Level;
import java.util.logging.Logger;
import magick.MagickException;
import main.JobPicker;
import main.Monitor;
import main.NetworkThread;
import main.Scheduler;

/**
 *
 * @author lucien
 */
public class LargeLabExercise {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
//        if( args[0].equals("slave") )
//        {
//            System.out.println("starting slave");
//            // Need to start monitor, processor
//        }
//        else 
//        {
//            System.out.println("starting master");
//            // Need to start scheduler, monitor and jobpicker
//            Thread mon = new Thread(Monitor.getInstance(false));
//            Thread sch = new Thread(Scheduler.getInstance());
//            Thread jp = new Thread(JobPicker.getInstance());
//            
//            jp.start();
//            sch.start();
//            mon.start();
//        }
        
        if( args[0].equals("slave") )
        {
            System.out.println("starting slave");
            NetworkThread nt = new NetworkThread("52.26.218.113");
            Thread t = new Thread(nt);   
            t.start();
            nt.appendMessage("hello world!");
        }
        else 
        {
            System.out.println("starting master");
            NetworkThread nt2 = new NetworkThread(0);
            Thread t2 = new Thread(nt2); 
            t2.start();
        }        

//        try {
//            Image im = new Image("/home/lucien/Documents/Studies/Year 5/Q1/2IMN30/pictures/40bd6a7e-2809-4008-82da-d3c68a07f1ae.jpg");
//            long start = System.currentTimeMillis();
//            im.processImage();
//            long end = System.currentTimeMillis();
//            long delta = end - start;
//            System.out.println(im.getFileSize() + " : " + delta);
//            im.write("/home/lucien/Downloads/iconjpg.jpg");
//        } catch (MagickException ex) {
//            Logger.getLogger(LargeLabExercise.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
}

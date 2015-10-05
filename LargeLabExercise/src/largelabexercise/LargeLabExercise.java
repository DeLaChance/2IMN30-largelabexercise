package largelabexercise;

import aws.AWS;
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
// Test case for networking        
//        if( args[0].equals("slave"))
//        {
//            System.out.println("starting slave");
//            NetworkThread nt = new NetworkThread("localhost");
//            Thread t = new Thread(nt);   
//            t.start();
//            nt.appendMessage("hello world!");
//        }
//        else 
//        {
//            System.out.println("starting master");
//            NetworkThread nt2 = new NetworkThread(0);
//            Thread t2 = new Thread(nt2); 
//            t2.start();
//        }
        
        try 
        {
            Thread mon = new Thread(Monitor.getInstance(false));
            mon.start();
            Thread.sleep(2000);
            
            System.out.println("Starting processing...");
            Image im = new Image(args[0]);
            long start = System.currentTimeMillis();
            im.processImage();
            long end = System.currentTimeMillis();
            long delta = end - start;
            im.write(args[1]);
            System.out.println("Done in " + delta + " milliseconds.");
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(LargeLabExercise.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

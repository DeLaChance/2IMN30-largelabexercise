package gen;

import gen.AWS;
import java.util.logging.Level;
import java.util.logging.Logger;
import magick.MagickException;
import master.JobPicker;
import both.Monitor;
import both.NetworkThread;
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
    public static void main(String[] args) 
    {
        if( args[0].equals("slave") )
        {
            // Need to start monitor, processor
            Slave slave = new Slave();
            slave.run();
        }
        else 
        {
            Master master = Master.getInstance();
        }
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

    }
    
}

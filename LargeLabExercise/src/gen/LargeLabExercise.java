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
            master.run();
            System.exit(0);
        }

    }
    
 
    
}

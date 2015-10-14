/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slave;

import both.Monitor;
import both.NetworkThread;
import gen.AWS;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author lucien
 */
public class Slave implements Runnable {
    
    private final String MASTER_IP; // tmp, master should be 52.26.218.113
    private final int PAUSE_TIME = 1000;
    private final int MAX_EXT_TIME = 30;
    
    private NetworkThread nt = null;
    private Thread networkThread = null;
    private Monitor monitor = null;
    private Thread monitorThread = null;
    
    private DateFormat dateFormat = null;
    private Date date = null;
    private String currentDate = null;
    private byte[] outputImageBytes = null;
    
    private boolean isRunning = false;
    private boolean isRunningJob = false;
    
    private ExecutionThread ext = null;
    
    private AWS aws = null;
    
    public Slave()
    {
        System.out.println("Starting slave");
        MASTER_IP = "52.26.218.113";
        
        initialize();
    }
    
    public Slave(String master_ip)
    {
        System.out.println("Starting slave");
        MASTER_IP = master_ip;
        
        initialize();
    }

    public void initialize()
    {
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        
        aws = AWS.getInstance();

        // Start slave network thread and make the connection
        nt = new NetworkThread(MASTER_IP);
        this.networkThread = new Thread(nt);
        this.networkThread.start();
        
        // Start monitor
        this.monitor = new Monitor(nt);
        this.monitorThread = new Thread(this.monitor);
        this.monitorThread.start();    
        
        this.ext = null;
    }
    
    @Override
    public void run() {
        
        isRunning = true;
        // Inform master that slave is running
        nt.appendMessage(NetworkThread.STARTUP_MSGID + NetworkThread.MSG_DEL);
         
        while( isRunning )
        {
            try 
            {
                Thread.sleep(PAUSE_TIME);
    
                if( this.ext == null )
                {
                    // Get the top message of the incoming network queue
                    // null if no message is there
                    String msg = nt.readTopMessage();

                    if( msg != null )
                    {
                        // Look up the type of the message
                        if( msg.startsWith(NetworkThread.JOB_MSGID) )
                        {
                            // Start doing job
                            String[] parts = msg.split(NetworkThread.MSG_DEL);
                            String objectKey = parts[1];
                            String imageName = this.getNewImageName(objectKey);

                            // The actual execution is done in a separate thread to let it timeout
                            // in case it does not terminate within 15 minutes
                            ext = new ExecutionThread(objectKey, imageName, nt);
                            try
                            {
                                TimeLimitedCodeBlock.runWithTimeout(ext, this.MAX_EXT_TIME, TimeUnit.MILLISECONDS);
                            }
                            catch (TimeoutException e) {
                                log("Timeout afer " + this.MAX_EXT_TIME + " seconds");
                                ext.complete();
                                ext = null;
                            }
                        }
                    }
                }
                else
                {
                    if( !ext.isRunning() )
                    {
                        ext = null;
                    }
                }

            }
            catch(Exception e)
            {
                log(" exception " + e.toString() + "\n" + e.getMessage());
                e.printStackTrace();
                stop();
            }
        }
    }
    
    public void stop()
    {
        this.isRunning = false;
        log(" stopping ");
    }
    
    public void log(String message)
    {
        System.out.println("Slave: " + message);
    }

    private String getNewImageName(String inputKey) {
        date = new Date();
        currentDate = dateFormat.format(date);
        return (currentDate.concat(inputKey.substring(inputKey.lastIndexOf("/")+1)));
    }
    
}

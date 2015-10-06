/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slave;

import both.Monitor;
import both.NetworkThread;
import gen.AWS;

/**
 *
 * @author lucien
 */
public class Slave implements Runnable {
    
    private final String MASTER_IP = "5.199.148.110"; // tmp, master should be 52.26.218.113
    private final int PAUSE_TIME = 1000;
    
    private NetworkThread nt = null;
    private Thread networkThread = null;
    private Monitor monitor = null;
    private Thread monitorThread = null;
    
    private boolean isRunning = false;
    private boolean isRunningJob = false;
    
    private AWS aws = null;
    
    public Slave()
    {
        System.out.println("Starting slave");
        aws = AWS.getInstance();
        
        initialize();
    }

    public void initialize()
    {
        // Start slave network thread and make the connection
        nt = new NetworkThread(MASTER_IP);
        this.networkThread = new Thread(nt);
        this.networkThread.start();
        
        // Start monitor
        this.monitor = new Monitor(nt);
        this.monitorThread = new Thread(this.monitor);
        this.monitorThread.start();    
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

                        log("Running job: " + objectKey);
                        // Get the actual file contents
                        //aws.getFileContentsFromBucket(objectKey);

                        // Process it 

                        // Complete

                        // Notify of completion
                        nt.appendMessage(NetworkThread.JOBCMP_MSGID + NetworkThread.MSG_DEL 
                            + objectKey);
                    }
                }
            }
            catch(Exception e)
            {
                log(" exception " + e.toString());
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
    
}

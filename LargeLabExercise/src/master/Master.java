/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import both.Monitor;
import both.NetworkThread;
import gen.JobQueue;
import java.util.ArrayList;

/**
 *
 * @author lucien
 */
public class Master implements Runnable {
    
    private final int MASTER_RATE = 100;
    private boolean isRunning = false;

    private Monitor monitor = null;
    
    private static Master instance = null;
    
    private Master()
    {
        log("starting master");
        // Need to start scheduler, monitor and jobpicker
        initialize();
    }
    
    public static Master getInstance()
    {
        if( instance == null )
        {
            instance = new Master();
        }
        
        return instance;
    }
    
    private void initialize()
    {
        
        MachineData md = new MachineData(1000, 0);
        MachineContainer.getInstance().addMachine(md);     

        monitor = new Monitor(); 
        Thread mon = new Thread(monitor);
        Thread sch = new Thread(Scheduler.getInstance());
        Thread jp = new Thread(JobPicker.getInstance());

        jp.start();
        sch.start();
        mon.start();      
    }
    
    /**
     * Updates the machine loads and completes the job
     * 
     */
    private synchronized void updateMachineStatus()
    {
        MachineContainer mc = MachineContainer.getInstance();
        for(MachineData md : mc.getData())
        {
            ArrayList<String> msgs = md.getNetworkThread().readAllMessages();
            
            for(String msg : msgs)
            {
                if( msg.startsWith(NetworkThread.MON_MSGID) )
                {
                    // Update load, currently we do not regard the cpu value
                    try 
                    {
                        String[] parts = msg.split(NetworkThread.MSG_DEL);
                        String a = parts[1];
                        String b = parts[2];

                        Double avgC = Double.parseDouble(a);
                        Double avgM = Double.parseDouble(b);
                        int mem = avgM.intValue();
                        
                        md.setCurCapacityAsPercentage(mem);
                        log("load updated " + mem);
                    }
                    catch(Exception e)
                    {
                        log(" exception " + e.toString());
                    }
                }
                
                if( msg.startsWith(NetworkThread.JOBCMP_MSGID) )
                {
                    // Register a job as completed
                    JobQueue jq = JobQueue.getInstance();
                    String[] parts = msg.split(NetworkThread.MSG_DEL);
                    String a = parts[1];                    
                    log("job completed " + a);
                    jq.completeJob(a);
                }
            }
        }
    }
    
    public void log(String msg)
    {
        System.out.println("Master: " + msg);
    }

    @Override
    public void run() {
        isRunning = true;
        
        while( isRunning )
        {
            try
            {
                Thread.sleep(MASTER_RATE);

            }
            catch(Exception e)
            {
            
            }
        }
    }
}

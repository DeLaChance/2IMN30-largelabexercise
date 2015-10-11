/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import both.Monitor;
import both.NetworkThread;
import gen.JobQueue;
import gen.ThreadLock;
import java.util.ArrayList;

/**
 *
 * @author lucien
 */
public class Master implements Runnable {
    
    private final int MASTER_RATE = 100;
    private final int STOPPED_THRESHOLD = 20; // STOPPED_THRESHOLD * MASTER_RATE determines
    // the number of milliseconds before machine is stopped due to not being alive
    private final int IDLE_THRESHOLD = 50; // Same for being idle
    
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
            NetworkThread nt1 = md.getNetworkThread();
            if(nt1 != null )
            {
                ArrayList<String> msgs = nt1.readAllMessages();

                for(String msg : msgs)
                {
                    //log("message " + msg);
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
                            log("load updated " + md.getCurCapacityAsPercentage());

                            md.resetCounter(); // the machine is still alive so 
                            // we reset the counter
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

                        // Remove the job from the job queue and the machine data's queue
                        jq.completeJob(a);
                        md.removeJob(a);
                        //log(md.numberOfJobs() + " jobs pending");
                        ThreadLock.getInstance().wakeUp(); // Wake up the scheduler
                    }

                    if( msg.startsWith(NetworkThread.STARTUP_MSGID) )
                    {
                        log(" new machine booted up");
                        md.activate();
                        ThreadLock.getInstance().wakeUp(); // Wake up the scheduler
                    }

                }

                if( md.isRunning() )
                {
                    if( md.getCounter() > STOPPED_THRESHOLD )
                    {
                        log(" machine is not responding, releasing it ");
                        md.releaseMachine();
                        ArrayList<String> uncompletedJobs = md.removeAssignedJobs();
                        JobQueue.getInstance().reassignJobs(uncompletedJobs);
                        log(uncompletedJobs.size() + " jobs need to be reassigned");
                        ThreadLock.getInstance().wakeUp(); // Wake up the scheduler
                    }

                    if( md.numberOfJobs() == 0 && md.getCurCapacityAsPercentage() > 75)
                    {
                        if( md.getIdleCounter() > IDLE_THRESHOLD )
                        {
                            log(" machine is not doing anything, releasing it ");
                            md.releaseMachine();
                            ThreadLock.getInstance().wakeUp(); // Wake up the scheduler
                        }
                        else
                        {
                            md.increaseIdleCounter();
                        }
                    }

                    md.increaseCounter();

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
                updateMachineStatus();
                
                if( Scheduler.getInstance().isRunning() == false )
                {
                    stop();
                }
            }
            catch(Exception e)
            {
                log(" error " + e.toString());
                e.printStackTrace();
            }
        }
    }
    
    public void stop()
    {
        log("stopping master program");
        MachineContainer.getInstance().stopAll();
        isRunning = false;
    }
}

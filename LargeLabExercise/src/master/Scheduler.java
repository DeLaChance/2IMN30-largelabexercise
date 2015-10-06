/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import gen.AWS;
import java.util.ArrayList;
import master.MachineData;
import gen.Job;
import gen.JobQueue;

/**
 *
 * @author lucien
 */
public class Scheduler implements Runnable {

    public final int MIN_MACHINES = 0;
    public final int MAX_MACHINES = 1;
    
    private final int SAMPLE_RATE = 500; // The sample rate in milliseconds
    
    private static Scheduler instance = null;    
    private boolean isRunning = false;
    private JobQueue jq;
    
    private Scheduler() 
    {
        log("starting scheduler");
        this.jq = JobQueue.getInstance();
    }
    
    public static Scheduler getInstance()
    {
        if( instance == null )
        {
            instance = new Scheduler();
        }
        
        return instance;
    }  
    
    
    @Override
    public void run() {
        this.isRunning = true;
        
        while( this.isRunning == true )
        {
            try 
            {
                Thread.sleep(SAMPLE_RATE);
                
                // Try to assign a job to a machine
                Job j = getJob();
                
                if( j == null )
                {
                    log("No job available currently");
                    startWaiting();
                }
                else
                {
                    MachineData m = getMachine(j);
                    
                    if( m == null )
                    {
                        if( canLeaseMachine() )
                        {
                            log(" leasing machine ");
                            
                            // tmp test
                            m = MachineContainer.getInstance().getData().get(0);
                            m.leaseMachine("5.199.148.110");
                            
                            //AWS.getInstance().leaseMachine();
                            //startWaiting();
                        }
                        else
                        {
                            log(MachineContainer.getInstance().getData().size() + " out of " + this.MAX_MACHINES 
                                + " machines have been leased");
                            startWaiting();
                        }
                    }
                    else
                    {
                        // Send call to machine
                        log("assigning job");
                    }
                }

            } 
            catch (InterruptedException ex) 
            {
                stop();
            }
        } 
    }
    
    private boolean canLeaseMachine()
    {
        if( this.isWaitingForMachine() )
        {
            return false;
        }
        
        for(MachineData md : MachineContainer.getInstance().getData())
        {
            if( !md.isRunning() )
            {
                return true;
            }
        }
        
        return false;    
    }
    
    private boolean isWaitingForMachine()
    {
        for(MachineData md : MachineContainer.getInstance().getData())
        {
            if( md.hasBeenLeased() && !md.isRunning() )
            {
                return true;
            }
        }
        
        return false;
    }
    
    private void startWaiting() throws InterruptedException
    {
        log(" waiting ");
        Thread.sleep(SAMPLE_RATE*10); // TO DO: implement wait for machine to complete    
    }
    
    public Job getJob()
    {
        Job mainJob = null;
        
        for(int i = JobQueue.MAX_PRIORITY; i > JobQueue.MIN_PRIORITY; i--)
        {
            ArrayList<Job> l = this.jq.takeJobsByPriority(i);
            long load = 100;
            
            for(Job job : l)
            {
                long jobLoad = job.getLoad();
                if( jobLoad < load )
                {
                    load = jobLoad;
                    mainJob = job;
                }
            }
            
            if( mainJob != null )
            {
                this.jq.scheduleJob(mainJob, i);
                return mainJob;
            }
        }
        
        return null;
    }
    
    public MachineData getMachine(Job job)
    {
        if( job == null )
        {
            return null;
        }
        
        int maxCapacity = 0;
        MachineData m1 = null;
        
        for(MachineData m : MachineContainer.getInstance().getData())
        {
            if( m.canRunJob(job) && m.getCurCapacityAsPercentage() > maxCapacity)
            {
                maxCapacity = m.getCurCapacityAsPercentage();
                m1 = m;
            }
        }
        
        return m1;
    }
    
    public void stop()
    {
        log("stopping scheduler");
        this.isRunning = false;
    }    
    
    public void log(String message)
    {
        System.out.println("Scheduler: " + message);
    }    
}

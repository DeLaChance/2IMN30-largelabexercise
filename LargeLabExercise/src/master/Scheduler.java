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
import gen.ThreadLock;

/**
 *
 * @author lucien
 */
public class Scheduler implements Runnable {
    
    private final int SAMPLE_RATE = 500; // The sample rate in milliseconds
    
    private static Scheduler instance = null;    
    private boolean isRunning = false;
    private JobQueue jq;
    private ArrayList<MachineData> machines;
    
    //Main instanceID: i-6e0d1bab
    //available instance-Ids: "i-c0f21804", "i-e255be26"
    
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
                
                if( j == null && this.jq.isEmpty() && JobPicker.getInstance().isRunning() == false
                    && allMachinesIdle() == true)
                {
                    log("jobqueue is empty, no job available and jobpicker no longer running");
                    stop();
                }
                
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
                            log(" Needs to lease machine ");
                            
                            // tmp test
                            m = MachineContainer.getInstance().getLeasableMachine();
                            if( m == null )
                            {
                                log("Error: no leasable machine found");
                            }
                            else 
                            {
                                m.leaseMachine();
                            }
                        }
                        else
                        {
                            log(" no machine available");
                            //log(MachineContainer.getInstance().getMachineStatistics());
                            startWaiting();
                        }
                    }
                    else
                    {
                        // Send call to machine
                        log("assigning job " + j.getKey() + ", " + j.getLoad() +  " to " + m.getIp());
                        this.jq.scheduleJob(j, j.getPriority());
                        m.assignJob(j);
                    }
                }

            } 
            catch (InterruptedException ex) 
            {
                stop();
            }
        } 
    }
    
    private boolean allMachinesIdle()
    {
        for(MachineData md : MachineContainer.getInstance().getData())
        {
            if( md.numberOfJobs() > 0 )
            {
                return false;
            }
        }
        
        return true;
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
    
    private void startWaiting() 
    {
        log(" waiting ");
        ThreadLock.getInstance().waitFor();
        //Thread.sleep(SAMPLE_RATE*10); 
    }
    
    public void stopWaiting()
    {
        log(" ended waiting ");
        ThreadLock.getInstance().wakeUp();        
    }
    
    public Job getJob()
    {
        Job mainJob = null;
        
        for(int i = JobQueue.MAX_PRIORITY; i > JobQueue.MIN_PRIORITY; i--)
        {
            ArrayList<Job> l = this.jq.takeJobsByPriority(i);
            long load = Long.MAX_VALUE;
            
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
    
    public synchronized boolean isRunning()
    {
        return this.isRunning;
    }
    
    public void log(String message)
    {
        System.out.println("Scheduler: " + message);
    }    
}

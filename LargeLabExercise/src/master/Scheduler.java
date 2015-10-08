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

    public final int MIN_MACHINES = 1;
    public final int MAX_MACHINES = 1;
    
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
        this.machines = new ArrayList<MachineData>();
        
        initialize();
    }
    
    private void initialize()
    {
        MachineData md = new MachineData(1000, 0, "");
        this.machines.add(md);     
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
                            //leaseMachine Requires InstanceId (returns ipAddress)
                           // AWS.getInstance().leaseMachine();
                            startWaiting();
                        }
                        else
                        {
                            log(this.machines.size() + " out of " + this.MAX_MACHINES 
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
        
        for(MachineData md : this.machines)
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
        for(MachineData md : this.machines)
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
        
        int maxLoad = 100;
        MachineData m1 = null;
        
        for(MachineData m : this.machines)
        {
            if( m.canRunJob(job) && m.getLoad() < maxLoad)
            {
                maxLoad = m.getLoad();
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

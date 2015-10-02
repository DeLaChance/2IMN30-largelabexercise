/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import largelabexercise.MachineData;

/**
 *
 * @author lucien
 */
public class Scheduler implements Runnable {

    public final int MIN_MACHINES = 1;
    public final int MAX_MACHINES = 5;
    
    private final int SAMPLE_RATE = 500; // The sample rate in milliseconds
    
    private static Scheduler instance = null;    
    private boolean isRunning = false;
    private JobQueue jq;
    private ArrayList<MachineData> machines;
    
    private Scheduler() 
    {
        log("starting scheduler");
        this.isRunning = false;
        this.jq = JobQueue.getInstance();
        this.machines = new ArrayList<MachineData>();
        
        MachineData m = new MachineData();
        m.updateLoad(new int[]{0,0});
        this.machines.add(m); // tmp
        
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
        while( this.isRunning == true )
        {
            try 
            {
                Thread.sleep(SAMPLE_RATE);
                Job j = getJob();
                MachineData m = getMachine(j);
                
                if( m == null && j != null && 
                    this.machines.size() < this.MAX_MACHINES )
                {
                    // No machine could be found, while a job and stand-by
                    // machine are available
                }
            } 
            catch (InterruptedException ex) 
            {
                stop();
            }
        } 
    }
    
    public Job getJob()
    {
        Job mainJob = null;
        
        for(int i = JobQueue.MAX_PRIORITY; i > JobQueue.MIN_PRIORITY; i--)
        {
            ArrayList<Job> l = this.jq.takeJobsByPriority(i);
            int load = 100;
            
            for(Job job : l)
            {
                int jobLoad = job.getLoadBottleneck();
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
        
        int maxLoad = 100;
        MachineData m1 = null;
        
        for(MachineData m : this.machines)
        {
            if( m.canRunJob(job) && m.getLoadBottleneck() < maxLoad)
            {
                maxLoad = m.getLoadBottleneck();
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import gen.Job;
import both.NetworkThread;
import gen.JobQueue;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucien
 */
public class MachineData {
    

    private String ip_address;
    
    // The max load of the machine defined as the number of MiB of memory
    private int maxMemoryCapacity = 0;
    
    // The current load of the machine
    private int curMemoryCapacity = 0;
    
    // The communication line between the master and its machine (or slave)
    private NetworkThread nt = null;
    private Thread networkThread = null; 
    
    private int index = 0; // index used to distinguish network threads
    
    private boolean isRunning = false; // true if the machine can process jobs
    private boolean hasBeenLeased = false; // true if the machine has been leased
    
    private ArrayList<String> assignedJobs = null; // a list of assigned jobs
    private int counter = 0; // a counter for measuring whether the machine is still
    // active
    private int idleCounter = 0; // a counter for measuring how long the machine is not 
    // running any job
    
    public MachineData(int memoryCapacity, int index)
    {
        this.maxMemoryCapacity = memoryCapacity;
        this.curMemoryCapacity = this.maxMemoryCapacity;
        
        this.index = index;
        this.assignedJobs = new ArrayList<String>();
    }    
    
    public MachineData(int memoryCapacity, int index, String ip_address)
    {
        this.maxMemoryCapacity = memoryCapacity;
        this.curMemoryCapacity = this.maxMemoryCapacity;
        
        this.index = index;
        this.ip_address = ip_address;
        this.assignedJobs = new ArrayList<String>();        
    }        
    
    public void leaseMachine(String ip)
    {
        System.out.println("leasing machine 2");        
        nt = new NetworkThread(this.index, ip);
        this.networkThread = new Thread(nt); 
        
        this.networkThread.start();
        this.hasBeenLeased = true;
        this.curMemoryCapacity = this.maxMemoryCapacity;        
    }
    
    public void leaseMachine()
    {
        System.out.println("leasing machine");
        nt = new NetworkThread(this.index, ip_address);
        this.networkThread = new Thread(nt); 
        
        this.networkThread.start();
        this.hasBeenLeased = true;
    }
    
    public boolean isRunning()
    {
        return this.isRunning;
    }
    
    public boolean hasBeenLeased()
    {
        return this.hasBeenLeased;
    }
    
    public void activate()
    {
        this.isRunning = true;
        counter = 0;
    }
    
    public void releaseMachine()
    {
        this.nt.stop();
        this.hasBeenLeased = false;
        this.isRunning = false;
        this.resetCounter();
        this.resetIdleCounter();
    }
    
    private void initialize()
    {
        this.curMemoryCapacity = maxMemoryCapacity; 
    }
    
    public int getCurCapacityAsAbsolute()
    {
        return this.curMemoryCapacity;
    }
    
    public int getCurCapacityAsPercentage()
    {
        double d = this.curMemoryCapacity;
        double e = this.maxMemoryCapacity;
        Double f = d/e * 100;
                
        return f.intValue();
    }

    public void setCurCapacityAsAbsolute(int curCapacity) 
    {
        if( curCapacity < 0 || curCapacity > maxMemoryCapacity)
        {
            return;
        }
        
        this.curMemoryCapacity = curCapacity;
    }
    
    public void setCurCapacityAsPercentage(int curCapacity)
    {
        if( curCapacity < 0 || curCapacity > 100)
        {
            return;
        }
        
        double d = curCapacity;
        d = d / 100;
        double e = this.maxMemoryCapacity;
        Double f = e * d;
        
        this.curMemoryCapacity = f.intValue();
    }
    
    public boolean canRunJob(Job job)
    {
        return (this.getCurCapacityAsAbsolute() > job.getLoad() || this.numberOfJobs() == 0)
            && this.isRunning == true;
    }

    /**
     * Returns the public ip-address of the machine
     * 
     * @return 
     */
    public String getIp() 
    {
        return ip_address;
    }
    
    public void assignJob(Job job)
    {
        if( job == null )
        {
            return;
        }
        
        this.assignedJobs.add(job.getKey());
        this.nt.appendMessage(NetworkThread.JOB_MSGID + NetworkThread.MSG_DEL + job.getKey());
    }
    
    public void removeJob(String jobkey)
    {
        this.assignedJobs.remove(jobkey);
    }
    
    public ArrayList<String> removeAssignedJobs()
    {
        ArrayList<String> jobs = new ArrayList<String>();
        
        for(String job : this.assignedJobs)
        {
            jobs.add(job);
        }
        
        this.assignedJobs.clear();
        
        return jobs;
    }
    
    public int numberOfJobs()
    {
        return this.assignedJobs.size();
    }
    
    public NetworkThread getNetworkThread()
    {
        return this.nt;
    }
    
    public int getCounter()
    {
        return this.counter;
    }
    
    public void increaseCounter()
    {
        this.counter++;
    }
    
    public void resetCounter()
    {
        this.counter = 0;
    }
    
    public int getIdleCounter()
    {
        return this.idleCounter;
    }
    
    public void increaseIdleCounter()
    {
        this.idleCounter++;
    }
    
    public void resetIdleCounter()
    {
        this.idleCounter = 0;
    }    
}

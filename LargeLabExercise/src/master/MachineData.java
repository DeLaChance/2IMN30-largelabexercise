/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import gen.Job;
import both.NetworkThread;

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
    
    private int index = 0;
    
    private boolean isRunning = false;
    private boolean hasBeenLeased = false;
    
    public MachineData(int memoryCapacity, int index, String ip)
    {
        this.maxMemoryCapacity = memoryCapacity;
        this.curMemoryCapacity = this.maxMemoryCapacity;
        
        this.index = index;
        nt = new NetworkThread(this.index, ip);
        this.networkThread = new Thread(nt);        
    }    
    
    public void leaseMachine()
    {
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
    }
    
    public void deactivate()
    {
        this.isRunning = false;
    }
    
    public void releaseMachine()
    {
        this.nt.stop();
        this.hasBeenLeased = false;
    }
    
    private void initialize()
    {
        this.curMemoryCapacity = maxMemoryCapacity; 
    }
    
    public int getLoad()
    {
        return this.curMemoryCapacity;
    }
    
    public void setLoad(int load) 
    {
        if( load < 0 || load > maxMemoryCapacity)
        {
            return;
        }
        
        this.curMemoryCapacity = load;
    }
    
    public boolean canRunJob(Job job)
    {
        return this.curMemoryCapacity > job.getLoad() && this.isRunning;
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
        this.nt.appendMessage(NetworkThread.JOB_MSGID + NetworkThread.MSG_DEL + job.getKey());
    }
}

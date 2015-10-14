/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import gen.Image;

/**
 *
 * @author lucien
 */
public class Job {
    
    private String key;
    private long fileSize = 0; // the size in bytes

    private long load;
    private int priority;
    
    public Job(String key, long fileSize, int priority)
    {
        this.key = key;
        this.fileSize = fileSize;
        this.priority = priority;
        
        this.estimateLoad();
    }
    
    public int getPriority()
    {
        return this.priority;
    }
    
    /**
     * The estimation of the load of a job. The load is the estimated completion
     * time in ms. We noted that a file of 7810 bytes took 250 ms to complete.
     * 
     */
    public void estimateLoad()
    {
        load = Math.max(fileSize / 7810,1)*250;
    }

    public long getLoad()
    {
        return this.load;
    }
    
    public String getKey()
    {
        return this.key;
    }
}

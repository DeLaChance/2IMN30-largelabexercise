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
     * The estimation of the load. This is based on heuristics. Memory is the
     * dominant factor and memory usage is linear with the file size.
     * 
     */
    public void estimateLoad()
    {
        load = 500; //Math.max(fileSize / 1000,1);
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

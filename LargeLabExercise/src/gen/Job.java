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
    
    public void estimateLoad()
    {
        // Based on heuristics. Memory is dominant factor and an 1800-bytes input
        // file takes about 1% of the memory
        load = Math.min(fileSize / 1800, 90);
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

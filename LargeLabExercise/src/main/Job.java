/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import largelabexercise.Image;

/**
 *
 * @author lucien
 */
public class Job {
    
    private String key;
    private long fileSize = 0; // the size in bytes
    
    private long cpuLoad;
    private long memoryLoad;
    
    public Job(String key, long fileSize)
    {
        this.key = key;
        this.fileSize = fileSize;
        
        this.estimateLoad();
    }
    
    public void estimateLoad()
    {
        cpuLoad = fileSize % 100 + 1;
        memoryLoad = fileSize % 100 + 1;
    }
    
    public long[] getLoad()
    {
        return new long[]{cpuLoad, memoryLoad};
    }
    
    public long getLoadBottleneck()
    {
        if( cpuLoad > memoryLoad )
        {
            return cpuLoad;
        }
        
        return memoryLoad;
    }
}

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
    private int fileSize = 0;
    
    private int cpuLoad;
    private int memoryLoad;
    
    public Job(String key, int fileSize)
    {
        this.key = key;
        this.fileSize = fileSize;
    }
    
    public void estimateLoad()
    {
        cpuLoad = fileSize % 100 + 1;
        memoryLoad = fileSize % 100 + 1;
    }
    
    public int[] getLoad()
    {
        return new int[]{cpuLoad, memoryLoad};
    }
    
    public int getLoadBottleneck()
    {
        if( cpuLoad > memoryLoad )
        {
            return cpuLoad;
        }
        
        return memoryLoad;
    }
}

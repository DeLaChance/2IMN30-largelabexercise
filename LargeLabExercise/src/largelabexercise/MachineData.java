/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package largelabexercise;

import main.Job;

/**
 *
 * @author lucien
 */
public class MachineData {
    
    // The ip address
    private String ip_address;
    
    // The load of the machine as a percentage (integer)
    private int cpuLoad = 0;
    private int memoryLoad = 0;
    
    // A normal leased machine has in total [x] CPU ghz and [y] MiB memory
    // If we were to lease, a machine with 2[x] CPU, then we could halve the
    // CPU-requirements of any job for this machine.
    private double memoryScaleFactor = 1.0;
    private double cpuScaleFactor = 1.0;
    
    public MachineData()
    {
        this.cpuLoad = 0;
        this.memoryLoad = 0;
    }

    public int[] getLoad() 
    {
        return new int[]{cpuLoad, memoryLoad};
    }
    
    public void updateLoad(int[] load) 
    {
        this.cpuLoad = load[0];
        this.memoryLoad = load[1];
    }
    
    public int getLoadBottleneck()
    {
        if( cpuLoad > memoryLoad )
        {
            return cpuLoad;
        }
        
        return memoryLoad;
    }
    
    public boolean canRunJob(Job job)
    {
        int[] load = job.getLoad();
        if( load[0] < this.cpuLoad && load[1] < this.memoryLoad )
        {
            return true;
        }
        
        return false;
    }

    public String getIp() {
        return ip_address;
    }
}

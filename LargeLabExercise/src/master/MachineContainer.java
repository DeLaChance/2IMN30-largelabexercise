/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.util.ArrayList;

/**
 *
 * @author lucien
 */
public class MachineContainer {
    
    private static MachineContainer instance = null;
    private ArrayList<MachineData> machinedata;
    
    private MachineContainer()
    {
        machinedata = new ArrayList<MachineData>();
        
        MachineData md = new MachineData(1000, 0, "localhost");
        addMachine(md);        
    }
    
    public static MachineContainer getInstance()
    {
        if( instance == null )
        {
            instance = new MachineContainer();
        }
        
        return instance;
    }
    
    public synchronized ArrayList<MachineData> getData()
    {
        return this.machinedata;
    }

    public synchronized void addMachine(MachineData md) {
        this.machinedata.add(md);
    }
    
    public synchronized String getMachineStatistics()
    {
        String s = "";
        for(MachineData md : this.machinedata)
        {
            s += md.getIp() + ": leased=" + md.hasBeenLeased() + ", running=" + 
                md.isRunning() + ", capacity=" + md.getCurCapacityAsPercentage();
            s += "\n";
        }
        
        return s;
    }
    
}

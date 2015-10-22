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
    private String lastLeased = "";
    
    public static final int MAX_NO_OF_SLAVES = 4;
    
    private MachineContainer()
    {
        machinedata = new ArrayList<MachineData>();
        
        for(int i = 0; i < MAX_NO_OF_SLAVES; i++)
        {
            MachineData md = new MachineData(1000, i);
            this.machinedata.add(md);
        }
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

    /**
     * Releases all amazon slave instances
     * 
     */
    public void stopAll() 
    {
        for(MachineData md : this.machinedata)
        {
            md.releaseMachine();
        }
    }

    public MachineData getLeasableMachine() {
        int l = 0;
        MachineData md1 = null;
        
        for(MachineData md : this.machinedata)
        {
            if( md.hasBeenLeased() == false && md.isRunning() == false )
            {
                if( !lastLeased.equals(md.getInstanceId()) )
                {
                    lastLeased = md.getInstanceId();
                    return md;
                }
                else
                {
                    md1 = md;
                    l += 1;
                }
            }
        }
        
        if( l >= 1 )
        {
            return md1;
        }
        
        return null;
    }
    
}

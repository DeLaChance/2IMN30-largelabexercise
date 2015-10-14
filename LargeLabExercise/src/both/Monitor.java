/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package both;

import gen.Logging;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import gen.ShellCommandHandler;

/**
 *
 * @author lucien
 */
public class Monitor implements Runnable {
    
    private final int MONITOR_RATE = 1000;
    private final int REPLY = 5;
    
    private static Monitor instance = null;    
    private boolean isRunning;
    private ShellCommandHandler sch = null;
    private boolean isSlave;

    private double avgCpu = 0.0;
    private double avgMem = 0.0;
    private int[] histCpu;
    private int[] histMem;
    
    private NetworkThread nt = null;
    
    /**
     * Constructor for monitor as a master. Does not need networkthread, 
     * because message receiving is not done here.
     * 
     */
    public Monitor()
    {
        System.out.println("starting monitor as master");
        this.isSlave = false;
        
        initialize();    
    }
    
    /**
     * Constructor for monitor as a slave. Does need networkthread, because
     * monitor messages are sent from here.
     * 
     * @param nt 
     */
    public Monitor(NetworkThread nt) 
    {
        System.out.println("starting monitor as slave");
        this.nt = nt;
        this.isSlave = true;
        
        initialize();
    }
    
    public void initialize()
    {
        this.sch = ShellCommandHandler.getInstance();
        this.histCpu = new int[10];
        this.histMem = new int[10];
    }

    @Override
    public void run() 
    {
        this.isRunning = true; 
        int reply = 0;
        
        while( isRunning )
        {
            try 
            {
                Thread.sleep(MONITOR_RATE);
                int cpu = getCPUPercentage();
                int memory = getMemoryPercentage();
                
                histCpu[reply] = cpu;
                histMem[reply] = memory;
                reply++;
                
                if( reply >= REPLY )
                {
                    reply = 0;
                    
                    double avgCpu = 0.0;
                    double avgMem = 0.0;
                    
                    for(int i = 0; i < REPLY; i++)
                    {
                        avgCpu += histCpu[i];
                        avgMem += histMem[i];
                    }
                    
                    avgCpu /= REPLY;
                    avgMem /= REPLY;
                    
                    this.writeLoad(avgCpu, avgMem);
                    
                    //log("Average cpu: " + this.avgCpu + ", average mem: " + avgMem);
                    Logging.getInstance().writeToMonitorLog(avgCpu, avgMem);
                    
                    if( isSlave )
                    {
                        // Send data to master
                        sendLoadToMaster(avgCpu, avgMem);
                    }
                    
                    // Write data to file
                }
                
            } 
            catch (InterruptedException ex) 
            {
                stop();
            }
        }
    }
    
    private synchronized void writeLoad(double avgC, double avgM)
    {
        this.avgCpu = avgC;
        this.avgMem = avgM;
    }
    
    public synchronized double[] readLoad()
    {
        return new double[]{this.avgCpu, this.avgMem};
    }
    
    public void sendLoadToMaster(double avgC, double avgM)
    {
        // Send the load to master
        String s = avgC + NetworkThread.MSG_DEL + avgM;
        this.nt.appendMessage(NetworkThread.MON_MSGID + NetworkThread.MSG_DEL + s);        
    }
    
    /**
     * This returns the percentage of Memory that is still available
     * 
     * @return 
     */
    public int getMemoryPercentage() 
    {
        String cmd = "free -m | grep 'Mem' | awk '{ print ($3/$2*100) }'";
        
        try 
        {
            String output = this.sch.runShellCommand(cmd).replaceAll("\n", "");
            Double d = Double.parseDouble(output);
            return d.intValue();
        }
        catch(Exception e)
        {
            log("error in getMemoryPercentage: " + e);
            stop();
        }
        
        return 0;
    }
    
    public int getCPUPercentage()
    {
        try 
        {
            String cmd = "top -bn 2 -d 0.01 | grep '^%Cpu' | tail -n 1 | gawk '{print $2+$4+$6}'";
            String output = this.sch.runShellCommand(cmd).replaceAll("\n", "");
            Double d = Double.parseDouble(output);
            return d.intValue();
        }
        catch(Exception e)
        {
            log("error in getCPUPercentage: " + e);
            stop();
        }
        
        return 0;
    }
    
    public void stop()
    {
        log("stopping scheduler");
        this.isRunning = false;
    }      
    
    public void log(String message)
    {
        System.out.println("Monitor: " + message);
    }    
    
}

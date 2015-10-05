/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucien
 */
public class Monitor implements Runnable {
    
    private final int MONITOR_RATE = 10;
    private final int REPLY = 10;
    
    private static Monitor instance = null;    
    private boolean isRunning;
    private ShellCommandHandler sch = null;
    private boolean isSlave;

    private double avgCpu = 0.0;
    private double avgMem = 0.0;
    private int[] histCpu;
    private int[] histMem;
    
    private Monitor(boolean isSlave) 
    {
        System.out.println("starting monitor");
        this.isRunning = true;     
        this.isSlave = isSlave;
        this.sch = ShellCommandHandler.getInstance();
        this.histCpu = new int[10];
        this.histMem = new int[10];
    }
    
    public static Monitor getInstance(boolean isSlave)
    {
        if( instance == null )
        {
            instance = new Monitor(isSlave);
        }
        
        return instance;
    }    

    @Override
    public void run() 
    {
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
                    
                    log("Average cpu: " + this.avgCpu + ", average mem: " + avgMem);
                    
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
        
    }
    
    public int getMemoryPercentage() 
    {
        String cmd = "free -m | grep 'Mem' | awk '{ print $3/$2*100 }'";
        
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
            return Integer.parseInt(output);
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

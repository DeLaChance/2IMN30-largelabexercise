/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import master.MachineContainer;
import master.MachineData;

/**
 *
 * @author lucien
 */
public class Logging {
    
    private String DIR = "/home/ubuntu/logs/";
    public static final int INCOMING_JOB = 0;
    public static final int RUNNING_JOB = 1;
    public static final int COMPLETED_JOB = 2;
    
    private static Logging instance = null;
    
    private long startTime;
    private String dateString = null;
   
    private FileWriter monitorWriter = null;
    private FileWriter normalWriter;
    private FileWriter masterEventWriter = null;
    
    private Logging()
    {
        DateFormat dateFormat = new SimpleDateFormat("HH_mm_ss_yyyy_MM_dd_");
        Date date = new Date();  
        this.dateString = dateFormat.format(date);        
        
        DIR += "/" + this.dateString + "/";
        File theDir = new File(DIR);
        if( theDir.exists() == false )
        {
            theDir.mkdir();
            System.out.println("Creating dir: " + DIR);
        }
        
        System.out.println("Logging files are in: " + DIR);
        this.startTime = System.currentTimeMillis();
    }
    
    public void setInputDir(String dir)
    {
        this.DIR = dir;
    }
    
    public synchronized static Logging getInstance()
    {
        if( instance == null)
        {
            instance = new Logging();
        }
        
        return instance;
    }
    
    public void writeToMonitorLog(Double cpu, Double mem)
    {
        int Icpu = cpu.intValue();
        int Imem = mem.intValue();
        long time = System.currentTimeMillis();
        
        String s = (time-startTime) + "," + Icpu + "," + Imem;
        writeToMonitorLog(s);
    }
    
    private void writeToMonitorLog(String s)
    {
        if( this.monitorWriter == null )
        {
            try {
                // Open file
                File file = new File(DIR + "monitorLog.csv"); // Create it if not present
                this.monitorWriter = new FileWriter(file, true);
                this.monitorWriter.append("timestampSinceStart (ms),cpu (%), mem (%) \n");
                this.monitorWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            try {
                this.monitorWriter.append(s + "\n");
                this.monitorWriter.flush();                
            } catch (IOException ex) {
                Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void writeToNormalLog(String s, boolean isSlave)
    {
        if( this.normalWriter == null )
        {
            try {
                // Open file
                File file = new File(DIR + "normalLog.csv"); // Create it if not present
                this.normalWriter = new FileWriter(file, true);
                
                if( isSlave )
                {
                    this.normalWriter.append("timestampSinceStart (ms),jobkey, status (running/completed)\n");
                }
                else
                {
                    String s1 = "timestamp,totalNumberOfJobs,";
                    int noOfMachines = MachineContainer.getInstance().getData().size();
                    
                    for(int i = 0; i < noOfMachines; i++)
                    {
                        s1 += "memCapacity" + i + ",numberOfJobs" + i + ",setOfJobs" 
                            + i + ",expectedLoad" + i + ",IsRunning" + i + ",cpuUsage"
                            + i + ",";
                    }
                    
                    if( s1.length() > 0)
                        s1 = s1.substring(0, s1.length()-1);
                        
                    this.normalWriter.append(s1 + "\n");
                    this.normalWriter.flush();   
                }
                this.normalWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            try {
                this.normalWriter.append(s + "\n");
                this.normalWriter.flush();                
            } catch (IOException ex) {
                Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
    }
    
    public void addJobToLog(String jobkey, long timeDelta)
    {
        String s = timeDelta + "," + jobkey;
        
        this.writeToNormalLog(s, true);
    }
    
    public void addMachineStatusToLog()
    {
        long timeDelta = System.currentTimeMillis() - this.startTime; 
        String s = timeDelta + ",";
        s += JobQueue.getInstance().numberOfJobs() + ",";
        for(int i = 0; i < MachineContainer.getInstance().getData().size(); i++)
        {
            MachineData md = MachineContainer.getInstance().getData().get(i);
            s += md.getSummary();
            s += ",";
        }
        
        if( s.length() > 0 )
            s = s.substring(0, s.length()-1);
        
        this.writeToNormalLog(s, false);
    }
    
    /**
     * Adds event to events.csv file.
     * 
     * 0 for leasing new machine
     * 1 for receiving boot message machine
     * 2 for releasing machine
     * 3 for assigning job to machine
     * 4 for receiving completion of job
     * 
     * @param eventId 
     */
    public void addEvent(int eventId, String msg)
    {

            try 
            {
                if( masterEventWriter == null )
                {                
                    // Open file
                    File file = new File(DIR + "events.csv"); // Create it if not present
                    this.masterEventWriter = new FileWriter(file, true);
                    masterEventWriter.append("timeSinceStart (ms), eventId, msg" + "\n");
                }
                else
                {
                    long timeDelta = System.currentTimeMillis() - this.startTime;         
                    String s = timeDelta + "," + eventId + "," + msg;
                    masterEventWriter.append(s + "\n");
                }
                
                masterEventWriter.flush();                
                
            }
            catch(Exception ex)
            {
                System.out.println(ex.toString());
            }
        

        
    }
    
}

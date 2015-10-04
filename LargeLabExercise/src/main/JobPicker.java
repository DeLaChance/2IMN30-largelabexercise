/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import aws.AWS;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucien
 */
public class JobPicker implements Runnable {
    
    private final int SAMPLE_RATE = 1000; // The sample rate in milliseconds
    
    private static JobPicker instance = null;   
    private boolean isRunning = false;
    
    private ArrayList<String> availableJobs;
    private JobQueue jobqueue;
    
    private JobPicker() 
    {
        log("starting jobpicker");
        this.isRunning = true;
        this.jobqueue = JobQueue.getInstance();
        
        initialize();
    }
    
    public void initialize()
    {
        this.availableJobs = new ArrayList<String>();  

        ArrayList<String> l = AWS.listFilesInBucket();
        for(String s : l)
        {
            this.availableJobs.add(s);
        }
    }
    
    public static JobPicker getInstance()
    {
        if( instance == null )
        {
            instance = new JobPicker();
        }
        
        return instance;
    }    

    public void stop()
    {
        log("stopping jobpicker");
        this.isRunning = false;
    }
    
    @Override
    public void run() {
        Random rand = new Random();
        
        while( this.isRunning == true )
        {
            try 
            {
                Thread.sleep(SAMPLE_RATE);
                
                if( this.availableJobs.size() > 0 )
                {
                    // Randomly pick a job
                    int j = rand.nextInt(this.availableJobs.size());
                    String s = this.availableJobs.get(j);
                    int l = rand.nextInt(100);
                    int p = rand.nextInt(3);
                    log("Picked job " + s + " with load " + l + " priority " + p);

                    Job job = new Job(s, l);
                    this.jobqueue.addJob(job, p);
                    
                    // Remove job from available jobs
                    this.availableJobs.remove(j);
                }
                else
                {
                    log("All jobs completed");
                    stop();
                }
            } 
            catch (InterruptedException ex) 
            {
                stop();
            }
        }
    }
    
    public void log(String message)
    {
        System.out.println("JobPicker: " + message);
    }
    
}

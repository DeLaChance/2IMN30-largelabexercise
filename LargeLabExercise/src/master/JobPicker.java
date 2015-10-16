/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import gen.AWS;
import gen.S3ObjectMeta;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import gen.Job;
import gen.JobQueue;
import gen.ThreadLock;

/**
 *
 * @author lucien
 */
public class JobPicker implements Runnable {
    
    private final int SAMPLE_RATE = 100; // The sample rate in milliseconds
    
    private static JobPicker instance = null;   
    private boolean isRunning = false;
    
    private ArrayList<S3ObjectMeta> availableJobs;
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
        this.availableJobs = AWS.getInstance().listFilesInBucket(); 
        log("There are " + this.availableJobs.size() + " jobs in total");
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
        
        int i=0;
        while( this.isRunning == true )
        {
            try 
            {
                //ElasticityTestCode-Start
                i++;
                if(i == 10){ //1 second or more (10 images) 
                    Thread.sleep(50000);
                    i = 0;
                }
                else
                {
                    Thread.sleep(50);
                }
                //ElasticityTestCode-End

                //Thread.sleep(SAMPLE_RATE); //uncomment after removing ElasticityTestCode
                
                if( this.availableJobs.size() > 0 )
                {
                    // Randomly pick a job
                    int j = rand.nextInt(this.availableJobs.size());
                    S3ObjectMeta s3o = this.availableJobs.get(j);
                    String s = s3o.getKey();
                    long l = s3o.getSize();
                    int p = 3;
                    log("Picked job " + s + " with load " + l + " priority " + p);

                    Job job = new Job(s, l, p);
                    this.jobqueue.addJob(job, p);
                    ThreadLock.getInstance().wakeUp();
                    
                    // Remove job from available jobs
                    this.availableJobs.remove(j);
                }
                else
                {
                    log("All jobs are in the queue");
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

    public synchronized boolean isRunning() {
        return this.isRunning;
    }
    
}

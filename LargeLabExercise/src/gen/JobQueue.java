/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import gen.Job;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author lucien
 */
public class JobQueue {
    
    public static int MIN_PRIORITY = 0;
    public static int MAX_PRIORITY = 3;
    
    private static JobQueue instance = null;
    private HashMap<Integer, ArrayList<Job>> l;
    private ArrayList<Job> scheduledJobs = null;
    
    private JobQueue()
    {
        l = new HashMap<Integer, ArrayList<Job>>();
        for(int i = MIN_PRIORITY; i <= MAX_PRIORITY; i++)
        {
            l.put(i, new ArrayList<Job>());
        }
        
        scheduledJobs = new ArrayList<Job>();
    }
    
    public synchronized static JobQueue getInstance()
    {
        if( instance == null )
        {
            instance = new JobQueue();
        }
        
        return instance;
    }
    
    public synchronized void addJob(Job j, int priority)
    {
        ArrayList<Job> l1 = this.l.get(priority);
        l1.add(j);
    }
   
    public synchronized ArrayList<Job> takeJobsByPriority(int priority)
    {
        ArrayList<Job> l1 = this.l.get(priority);
        return l1;
    }
    
    public synchronized void scheduleJob(Job job, int priority)
    {
        ArrayList<Job> l1 = this.l.get(priority);
        l1.remove(job);
        scheduledJobs.add(job);
    }
    
    public synchronized void completeJob(String key)
    {
        Job tobeRemoved = null;
        
        for(Job job : scheduledJobs)
        {
            if( job.getKey().equals(key) )
            {
                tobeRemoved = job;
            }
        }
        
        scheduledJobs.remove(tobeRemoved);
        
    }
    
    public synchronized boolean isEmpty()
    {
        for(int i = MIN_PRIORITY; i <= MAX_PRIORITY; i++)
        {
            if( !l.get(i).isEmpty() )
            {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * When a machine crashes, its pending jobs need to be reassigned.
     * 
     * @param jobs 
     */
    public synchronized void reassignJobs(ArrayList<String> jobs)
    {
        for(String jobkey : jobs)
        {
            for(Job job : this.scheduledJobs)
            {
                if( job.getKey().equals(jobkey) )
                {
                    ArrayList<Job> l1 = this.l.get(job.getPriority());
                    l1.add(job);
                }
            }
        }
    }
    
    public synchronized int numberOfJobs()
    {
        int total = 0;
        
        for(int i = MIN_PRIORITY; i <= MAX_PRIORITY; i++)
        {
            total += this.l.get(i).size();
        }        
        
        return total;
    }    
}

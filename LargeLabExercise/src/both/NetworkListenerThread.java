/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package both;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucien
 */
public class NetworkListenerThread implements Runnable{
    
    private BufferedReader in = null;
    private boolean isRunning = false;
    private ArrayList<String> incomingQueue;
    
    /**
     * This thread is used by the NetworkThread to solely do the listening
     * part. This is because the listening blocks the thread until input
     * has been retrieved.
     * 
     * @param in 
     */
    public NetworkListenerThread(BufferedReader in)
    {
        this.in = in;
        log("starting NetworkListenerThread");
    }

    @Override
    public void run() {
        try {
            String s = null;
            isRunning = true;
            incomingQueue = new ArrayList<String>();
            
            log("starting up");
            
            while((s = in.readLine()) != null)
            {
                if( isRunning == false )
                {
                    break;
                }
                
                //log("s: " + s);
                if( s != null )
                {
                    if( !s.equals("") )
                    {
                        appendToQueue(s);
                        log("received " + s);
                    }
                }
            }
        } catch (Exception ex) {
           log("exception: " + ex.toString());
           ex.printStackTrace();
        }
    }
    
    public void log(String msg)
    {
        System.out.println("NetworkListenerThread " + msg);
    }
    
    private synchronized void appendToQueue(String msg)
    {
        this.incomingQueue.add(msg);
    }
    
    public ArrayList<String> readAllMessages()
    {
        ArrayList<String> l = new ArrayList<String>();
        
        for(String s : this.incomingQueue)
        {
            l.add(s);
        }
        
        this.incomingQueue.clear();
        return l;
    }
    
    public synchronized String readTopMessage()
    {
        if( this.incomingQueue.size() > 0 )
        {
            return this.incomingQueue.remove(0);
        }
        
        return null;
    }    

    public void stop() 
    {
        log(" stopping ");
        isRunning = false;
    }
    
}

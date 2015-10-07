/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucien
 */
public class ThreadLock {
    private final Object lockObject;
    private static ThreadLock instance = null;
    
    private ThreadLock() 
    {
        this.lockObject = new Object();
    }    
    
    public static ThreadLock getInstance()
    {
        if( instance == null )
        {
            instance = new ThreadLock();
        }
        
        return instance;
    }
    
    public void waitFor()
    {
        synchronized (lockObject)
        {
            try {
                lockObject.wait();
            } catch (InterruptedException ex) {
                
            }
        }
    }
    
    public void wakeUp()
    {
        synchronized (lockObject) 
        {
            lockObject.notify();
        }    
    }
}

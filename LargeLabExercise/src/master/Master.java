/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import both.Monitor;
import both.NetworkThread;

/**
 *
 * @author lucien
 */
public class Master {
    
    public Master()
    {
        log("starting master");
        // Need to start scheduler, monitor and jobpicker
        NetworkThread nt = new NetworkThread(0, "");
        Thread net = new Thread(nt);

        Monitor monitor = new Monitor(false, nt);
        Thread mon = new Thread(monitor);
        Thread sch = new Thread(Scheduler.getInstance());
        Thread jp = new Thread(JobPicker.getInstance());

        net.start();
        jp.start();
        sch.start();
        mon.start();    
    }
    
    public void log(String msg)
    {
        System.out.println("Master: " + msg);
    }
}

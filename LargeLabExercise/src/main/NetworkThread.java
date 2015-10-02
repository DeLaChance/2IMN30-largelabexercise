/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucien
 */
public class NetworkThread implements Runnable {
    
    public static final int PORT_NO = 4040;
    public final int RATE = 10;
    
    private boolean isRunning = false;
    private boolean isMaster = false;
    private int id = 0;
    
    private Socket socket;
    private DataOutputStream out;
    private BufferedReader in;    
    private String ip;    
    
    private ArrayList<String> queue;

    /**
     * Constructor for master, no ip needed
     * 
     */
    public NetworkThread(int id)
    {
        isMaster = true;
        log("starting networkthread as master with id " + id);
        this.id = id;
        this.queue = new ArrayList<String>();
    }
    
    /**
     * Constructor for slave, ip needed
     * 
     * @param ip 
     */
    public NetworkThread(String ip) 
    {
        this.isRunning = true;
        this.ip = ip;
        isMaster = false;
        log("starting networkthread as slave with master ip " + ip);
        this.queue = new ArrayList<String>();
    }
    
    public void stop()
    {
        log("stopping now");
        this.isRunning = false;
    }
    
    @Override
    public void run() 
    {
        if( isMaster == true )
        {
            try 
            {
                ServerSocket welcomeSocket = new ServerSocket(PORT_NO);
                socket = welcomeSocket.accept();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());         
                ip = socket.getRemoteSocketAddress().toString();
                
                Thread nt = new Thread(new NetworkThread(id+1));
                nt.start();
            } 
            catch (Exception ex) 
            {
                stop();
                Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
        else
        {
            try 
            {
                socket = new Socket(ip, PORT_NO);
                out = new DataOutputStream(socket.getOutputStream());        
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } 
            catch (Exception ex) 
            {
                stop();                
                Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try
        {
            while(isRunning)
            {
                Thread.sleep(RATE);

                if( socket.isClosed() )
                {
                    stop();
                }

                String s = in.readLine();
                if( !s.equals("") )
                {
                    log("received " + s);
                }
                
                sendAllMessages();
            } 
        }
        catch (Exception ex) 
        {
            stop();                
            Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public synchronized void appendMessage(String message)
    {
        this.queue.add(message);
    }

    private synchronized void sendAllMessages()
    {
        try
        {
            for(String s : this.queue)
            {
                out.writeBytes(s + "\n");
            }
        }
        catch(Exception e)
        {
            stop();
            log(" messages could not be sent");
        }
    }
    
    public void log(String message)
    {
        if( this.isMaster )
        {
            System.out.println("ListenerThread[master, " + this.id + ", " + 
                this.ip + "]: " + message);        
        }
        else
        {
            System.out.println("ListenerThread[slave, " + this.ip + "]: " + message);        
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package both;

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
    
    public final static String JOB_MSGID = "1";
    public final static String MON_MSGID = "2";
    public final static String JOBCMP_MSGID = "3";
    public final static String MSG_DEL = ":";    
    
    private boolean isRunning = false;
    private boolean isMaster = false;
    private int id = 0;
    
    private Socket socket;
    private DataOutputStream out;
    private BufferedReader in;    
    private String ip;    
    private int port = PORT_NO;
    
    private ArrayList<String> inqueue;
    private ArrayList<String> outqueue;

    /**
     * Constructor for slave
     * 
     * @param ip 
     */
    public NetworkThread(String ip)
    {
        log("starting networkthread as slave with master ip " + ip);
        this.ip = ip;
        this.port = PORT_NO;
        this.isMaster = false;
        this.inqueue = new ArrayList<String>();
        this.outqueue = new ArrayList<String>();
    }
    
    /**
     * Constructor for master.
     * 
     * @param id
     * @param ip 
     */
    public NetworkThread(int id, String ip)
    {
        log("starting networkthread as master with id " + id);
        this.isMaster = true;
        this.id = id;
        this.port = PORT_NO;
        this.ip = ip;
        this.inqueue = new ArrayList<String>();
        this.outqueue = new ArrayList<String>();
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
                ServerSocket welcomeSocket = new ServerSocket(port);
                socket = welcomeSocket.accept();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());         
                ip = socket.getRemoteSocketAddress().toString();
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
                socket = new Socket(ip, port);
                out = new DataOutputStream(socket.getOutputStream());        
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } 
            catch (Exception ex) 
            {
                stop();                
                Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        isRunning = true;
        
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
                    readMessage(s);
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
        this.inqueue.add(message);
    }

    private synchronized void sendAllMessages()
    {
        try
        {
            for(String s : this.inqueue)
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
    
    private synchronized void readMessage(String s)
    {
        log(" incoming message: " + s);
        this.outqueue.add(s);
    }
    
    public synchronized String readTopMessage()
    {
        return this.outqueue.remove(0);
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
    
    public boolean isRunning()
    {
        return this.isRunning;
    }
    
}

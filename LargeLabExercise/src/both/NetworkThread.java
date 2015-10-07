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
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucien
 */
public class NetworkThread implements Runnable {
    
    public static final int PORT_NO = 4040;
    public final int RATE = 100;
    
    public final static String JOB_MSGID = "1";
    public final static String MON_MSGID = "2";
    public final static String JOBCMP_MSGID = "3";
    public final static String STARTUP_MSGID = "4";
    public final static String MSG_DEL = ":";    
    
    private boolean isRunning = false;
    private boolean isMaster = false;
    private int id = 0;
    
    private Socket socket;
    private DataOutputStream out;
    private BufferedReader in;    
    private String ip;    
    private int port = PORT_NO;
    
    private ArrayList<String> outgoingQueue;
    
    private NetworkListenerThread nlt = null;
    private Thread nltThread = null;

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
        this.outgoingQueue = new ArrayList<String>();
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
        this.outgoingQueue = new ArrayList<String>();
    }
    
    public void stop()
    {
        log("stopping now");
        this.isRunning = false;
        
        if( this.nlt != null )
        {
            this.nlt.stop();
        }
    }
    
    @Override
    public void run() 
    {
        if( isMaster == true )
        {
            try 
            {
                log("Accepting connections.");
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
            int attempts = 1;
            while( tryConnect() == false && attempts < 10 )
            {
                try
                {
                    Thread.sleep(1000);
                    attempts += 1;
                    log("trying to connect, attempt" + attempts);
                }
                catch(Exception e)
                {
                
                }
            }
        }
        
        log("Starting up...");
        isRunning = true;
        
        this.nlt = new NetworkListenerThread(this.in);
        this.nltThread = new Thread(this.nlt);
        this.nltThread.start();
        
        try
        {
            String s = null;
            while(isRunning)
            {
                Thread.sleep(RATE);
                sendAllMessages();
            } 
        }
        catch (Exception ex) 
        {
            stop();                
            Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
        log("terminating...");
    }
    
    private boolean tryConnect()
    {
        try 
        {
            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());        
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } 
        catch (ConnectException e) 
        {
            log("Error while connecting. " + e.getMessage());
        } 
        catch (SocketTimeoutException e) 
        {
            log("Connection: " + e.getMessage() + ".");
        } 
        catch (IOException e) 
        {
            stop();
        }    
        
        return false;
    }
    
    /**
     * Adds a message to a queue. Later this message will be sent.
     * 
     * @param message 
     */
    public synchronized void appendMessage(String message)
    {
        //log("appending message " + message);
        this.outgoingQueue.add(message);
    }

    private synchronized void sendAllMessages()
    {
        try
        {
            for(String s : this.outgoingQueue)
            {
                log("sending message " + s);
                out.writeBytes(s + "\n");
            }
            
            outgoingQueue.clear();
        }
        catch(Exception e)
        {
            stop();
            log(" messages could not be sent");
        }
    }
    
    /**
     * Reads top message of incoming queue messages and removes that message
     * from the queue.
     * 
     * @return 
     */
    public synchronized String readTopMessage()
    {
        if( this.nlt == null )
        {
            return null;
        }
        
        return this.nlt.readTopMessage();
    }
    
    /**
     * Reads all the incoming messsages and returns it as a list. Then clears
     * the list.
     * 
     * @return 
     */
    public synchronized ArrayList<String> readAllMessages()
    {
        return this.nlt.readAllMessages();
    }
    
   
    
    public void log(String message)
    {
        if( this.isMaster )
        {
            System.out.println("NetworkThread[master, " + this.id + ", " + 
                this.ip + "]: " + message);        
        }
        else
        {
            System.out.println("NetworkThread[slave, " + this.ip + "]: " + message);        
        }
    }
    
    public boolean isRunning()
    {
        return this.isRunning;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import largelabexercise.Machine;
import largelabexercise.MachineData;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lucien
 */
public class MessageSender {
    
    public final int PORT_NO = NetworkThread.PORT_NO;

    private Socket clientSocket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    
    private String ip;
    
    public MessageSender(String ip)
    {
        try {
            this.ip = ip;
            
            clientSocket = new Socket(ip, PORT_NO);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());        
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void close()
    {
        try {
            log("closing sender-socket for ip " + ip);
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(String message)
    {
        try {
            outToServer.writeBytes(message + '\n');
        } catch (IOException ex) {
            Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void log(String message)
    {
        System.out.println("MessageSender: " + message);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author lucien
 */
public class ShellCommandHandler {
    
    private static ShellCommandHandler instance = null;
    
    private ShellCommandHandler() 
    {
    
    }
    
    public static ShellCommandHandler getInstance()
    {
        if( instance == null )
        {
            instance = new ShellCommandHandler();
        }
        
        return instance;
    }
    
    public String runShellCommand(String command) throws IOException, InterruptedException, Exception
    {
        return runShellCommand(command, "/");
    }
    
    public String runShellCommand(String command, String directory) throws IOException, InterruptedException, Exception
    {
        String[] cmd = {"/bin/bash", "-c", command};
        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
        int rc = p.exitValue();
        String output = loadStream(p.getInputStream());
        String error  = loadStream(p.getErrorStream());
        
        if( rc != 0)
        {
            log(error);
            return error;
        }
        
        return output;
    }
    
    public void log(String message)
    {
        System.out.println("SCH: " + message);
    }
    
    private static String loadStream(InputStream s) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line=br.readLine()) != null)
            sb.append(line).append("\n");
        return sb.toString();
    }    
    
}

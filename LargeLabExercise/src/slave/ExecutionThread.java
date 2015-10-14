/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slave;

import both.NetworkThread;
import gen.AWS;
import gen.Image;
import gen.Logging;
import gen.S3ObjectData;
import java.util.logging.Level;
import java.util.logging.Logger;
import magick.MagickException;

/**
 *
 * @author lucien
 */
public class ExecutionThread implements Runnable {

    private String jobkey = null;
    private String imageName = null;
    private boolean isRunning;
    private NetworkThread nt = null;
    
    public ExecutionThread(String jobkey, String imageName, NetworkThread nt)
    {
        log(" creating executionthread for: " + jobkey);
        this.jobkey = jobkey;
        this.imageName = imageName;
        this.nt = nt;
    }
    
    @Override
    public void run() {
        try {
            isRunning = true;
            String objectKey = this.jobkey;
            AWS aws = AWS.getInstance();
            
            log("ExecutionThread: Running job: " + objectKey);
            long startTime = System.currentTimeMillis();
            
            // Get the actual file contents
            S3ObjectData s3od = null;
            s3od = aws.getFileContentsFromBucket(objectKey);
            
            s3od.setImageName(this.imageName);
            
            // Process it
            Image image = new Image(s3od.getInputImageData());
            image.processImage();
            byte[] outputImageBytes = image.write(s3od.getImageName());
            s3od.setOutputImageData(outputImageBytes);
            s3od.setOutputImageSize(outputImageBytes.length);
            aws.writeFileContentsToBucket(s3od);
            
            // Complete
            long stopTime = System.currentTimeMillis();
            log("ExecutionThread: Completing job: " + objectKey);
                                
            Logging.getInstance().addJobToLog(objectKey, stopTime-startTime);
            this.complete();
            isRunning = false;
        } catch (MagickException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized boolean isRunning()
    {
        return this.isRunning;
    }

    public void complete() {
        // Notify of completion
        nt.appendMessage(NetworkThread.JOBCMP_MSGID + NetworkThread.MSG_DEL 
            + this.jobkey);  
     
    }
    
    public void log(String msg)
    {
        System.out.println("ExecutionThread: " + msg);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.ArrayList;

/**
 *
 * @author lucien
 */
public class AWS {
    
    private static AWS instance = null;
    private static String bucketName = "2imn30imguplsk";
    private static String prefix = "InputImage/";
    private static AmazonS3 s3client;
    
    private AWS()
    {
        s3client = new AmazonS3Client(new ProfileCredentialsProvider());
    }
    
    public static synchronized AWS getInstance()
    {
        if( instance == null )
        {
            instance = new AWS();
        }
        
        return instance;
    }
    
    public String getBucketName() 
    {
        return this.bucketName;
    }    
    
    /**
     * List all object keys available in the S3-storage defined by bucketName,
     * using the already defined bucketname
     * 
     * @return 
     */
    public ArrayList<S3Object> listFilesInBucket()
    {
        return listFilesInBucket(bucketName, prefix);
    }
    
    /**
     * List all object keys and object sizes available in the S3-storage defined by bucketName
     * and matching a certain prefix
     * 
     * @param bucketName
     * @param prefix
     * @return list with S3Object
     */
    public ArrayList<S3Object> listFilesInBucket(String bucketName, String prefix)
    {
        ArrayList<S3Object> keys = new ArrayList<S3Object>();
        
        try 
        {
            ListObjectsRequest request = new ListObjectsRequest().withBucketName(bucketName)
                .withPrefix(prefix);
            ObjectListing listing = null;

            while((listing == null) || (request.getMarker() != null)) 
            {
                listing = s3client.listObjects(request);
              
                for (S3ObjectSummary o : listing.getObjectSummaries()) {
                    if( o.getKey().length() > prefix.length() )
                    {
                        S3Object s3o = new S3Object(o.getKey(), o.getSize());
                        keys.add(s3o);
                    }
                }              
              
                request.setMarker(listing.getNextMarker());
            }           
        }
        catch (AmazonServiceException ase) 
        {
            System.out.println("Caught an AmazonServiceException, " +
            		"which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) 
        {
            System.out.println("Caught an AmazonClientException, " +
            		"which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        
        return keys;
    }

    /**
     * Given a bucket and an object key, this method returns the actual contents
     * of the file.
     * 
     * @param bucketName
     * @param key
     * @return 
     */
    public String getFileContentsFromBucket(String bucketName, String key)
    {
        return null;
    }        
    
    /**
     * Writes a file to the S3-storage to a specific bucket and using a specific
     * key.
     * 
     * @param bucketName
     * @param key
     * @param file 
     */
    public void writeFileContentsToBucket(String bucketName, String key, Object file)
    {
    
    }
    
    /**
     * Methods for leasing and releasing a machine. Needs investigation.
     */
    public void leaseMachine()
    {
    
    }
    
    public void releaseMachine()
    {
    
    }
    
}

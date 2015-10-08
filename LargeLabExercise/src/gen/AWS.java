/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author lucien
 */
public class AWS {
    
    private static AWS instance = null;
    private final String bucketName = "2imn30imguplsk";
    private final String prefix = "InputImage/";
    private final String outputProcessedFolder = "ProcessedImage/";
    private AmazonS3 s3client;
    
    private AWS()
    {
        s3client = new AmazonS3Client(new ProfileCredentialsProvider().getCredentials());
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        s3client.setRegion(usWest2);
    }
    
    public synchronized static AWS getInstance()
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
    
    public String getPrefix() 
    {
        return this.prefix;
    }
    
    /**
     * List all object keys available in the S3-storage defined by bucketName,
     * using the already defined bucketname
     * 
     * @return 
     */
    public ArrayList<S3ObjectMeta> listFilesInBucket()
    {
        return listFilesInBucket(this.getBucketName(), this.getPrefix());
    }
    
    /**
     * List all object keys and object sizes available in the S3-storage defined by bucketName
     * and matching a certain prefix
     * 
     * @param bucketName
     * @param prefix
     * @return list with S3ObjectMeta
     */
    public ArrayList<S3ObjectMeta> listFilesInBucket(String bucketName, String prefix)
    {
        ArrayList<S3ObjectMeta> keys = new ArrayList<S3ObjectMeta>();
        
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
                        S3ObjectMeta s3o = new S3ObjectMeta(o.getKey(), o.getSize());
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

    public S3ObjectData getFileContentsFromBucket(String key)
    {
        return getFileContentsFromBucket(this.getBucketName(), key);
    }
    
    /**
     * Given a bucket and an object key, this method returns the actual contents
     * of the file.
     * 
     * @param bucketName
     * @param key
     * @return 
     */
    public S3ObjectData getFileContentsFromBucket(String bucketName, String key) 
    {
        
        S3ObjectData s3od = null;
        try 
        {
            S3Object object = s3client.getObject(new GetObjectRequest(bucketName, key)); 
            s3od = new S3ObjectData();
            object.getObjectContent();
            s3od.setInputImageData(IOUtils.toByteArray((InputStream)object.getObjectContent()));
            s3od.setContentType(object.getObjectMetadata().getContentType());
            s3od.setInputKey(object.getKey());
            s3od.setInputImageSize(object.getObjectMetadata().getContentLength());
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
        catch (IOException ioe)
        {
            System.out.println("Caught an IOException at getobjectFromS3 ");
        }
        return s3od;
    }        
    
    public void writeFileContentsToBucket(S3ObjectData s3od)
    {
        writeFileContentsToBucket(this.getBucketName(), s3od);
    }
    
    
    /**
     * Writes a file to the S3-storage to a specific bucket and using a specific
     * key.
     * 
     * @param bucketName
     * @param s3od
     */
    public void writeFileContentsToBucket(String bucketName, S3ObjectData s3od)
    {
        InputStream imageStream;
        ObjectMetadata meta;
        try{
            imageStream = new ByteArrayInputStream(s3od.getOutputImageData());
            meta = new ObjectMetadata();
            meta.setContentLength(s3od.getOutputImageData().length);
            meta.setContentType(s3od.getContentType());
            s3od.setOutputKey(this.outputProcessedFolder+s3od.getImageName());
            s3client.putObject(new PutObjectRequest(bucketName, s3od.getOutputKey(), imageStream, meta)); 
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
        finally
        {
            imageStream = null;
            s3od = null;
            meta = null;
        }
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

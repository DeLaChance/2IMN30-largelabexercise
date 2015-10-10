/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

/**
 *
 * @author lucien
 */
public class S3ObjectMeta {
    
    private String key;
    private long size;
    
    public S3ObjectMeta(String key, long size)
    {
        this.key = key;
        this.size = size;
    }
    
    public String getKey()
    {
        return this.key;
    }
    
    public long getSize()
    {
        return this.size;
    }
    
}

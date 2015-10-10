/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

import java.io.File;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

/**
 * A wrapper class for MagickImage
 * 
 */
public class Image 
{
    private MagickImage instance;
    private ImageInfo info;
    private byte[] bytes;
    //private File file;
    
    public Image(byte[] bytes) throws MagickException
    {
        this.info = new ImageInfo(); //image not taken from absolute path, default instance
        this.bytes = bytes; // image in byte array
        this.instance = new MagickImage(this.info, bytes);
        //this.file = new File(fileName);
    }
    
    public void processImage() throws MagickException
    {
        double radius = 3.0;
        double sigma = 1.0;
        
        this.instance = this.instance.blurImage(radius, sigma);
        this.instance = this.instance.charcoalImage(radius, sigma);
    }
    
    public byte[] write(String fileOut) throws MagickException
    {
        this.instance.setFileName(fileOut);
        return this.instance.imageToBlob(this.info);
        //this.instance.writeImage(this.info);
    }
}

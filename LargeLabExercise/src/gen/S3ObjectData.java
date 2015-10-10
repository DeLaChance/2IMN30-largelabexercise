/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

/**
 *
 * @author shoaib
 */
public class S3ObjectData {
    private String inputKey;
    private long inputImageSize;
    private byte[] inputImageData;
    private String contentType;
    private String imageName;
    private String outputKey;
    private byte[] outputImageData;
    private long outputImageSize;

    public byte[] getInputImageData() {
        return inputImageData;
    }

    public String getContentType() {
        return contentType;
    }
    
    public String getInputKey()
    {
        return this.inputKey;
    }
    
    public long getInputImageSize()
    {
        return this.inputImageSize;
    }

    public String getImageName() {
        return imageName;
    }

    public String getOutputKey() {
        return outputKey;
    }

    public byte[] getOutputImageData() {
        return outputImageData;
    }

    public long getOutputImageSize() {
        return outputImageSize;
    }

    public void setOutputImageSize(long outputImageSize) {
        this.outputImageSize = outputImageSize;
    }

    public void setOutputImageData(byte[] outputImageData) {
        this.outputImageData = outputImageData;
    }

    public void setOutputKey(String outputKey) {
        this.outputKey = outputKey;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    public void setInputImageSize(long inputImageSize) {
        this.inputImageSize = inputImageSize;
    }

    public void setInputImageData(byte[] inputImageData) {
        this.inputImageData = inputImageData;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

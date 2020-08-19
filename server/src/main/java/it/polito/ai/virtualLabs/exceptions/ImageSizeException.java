package it.polito.ai.virtualLabs.exceptions;

public class ImageSizeException extends VLServiceException {
    public ImageSizeException() {
        super("Image size is over the limit!");
    }
}

package it.polito.ai.virtualLabs.exceptions;

public class PhotoVersionHMNotFoundException extends VLServiceException {
    public PhotoVersionHMNotFoundException() {
        super("Homework version not found");
    }
}
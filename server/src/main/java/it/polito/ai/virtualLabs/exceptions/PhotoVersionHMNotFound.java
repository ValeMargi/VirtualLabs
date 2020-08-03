package it.polito.ai.virtualLabs.exceptions;

public class PhotoVersionHMNotFound extends VLServiceException {
    public PhotoVersionHMNotFound() {
        super("Homework version not found");
    }
}
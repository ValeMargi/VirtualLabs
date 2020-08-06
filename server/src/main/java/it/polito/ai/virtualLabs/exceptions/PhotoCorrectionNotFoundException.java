package it.polito.ai.virtualLabs.exceptions;

public class PhotoCorrectionNotFoundException extends VLServiceException {
    public PhotoCorrectionNotFoundException() {
        super("Photo ccorrection not found");
    }
}
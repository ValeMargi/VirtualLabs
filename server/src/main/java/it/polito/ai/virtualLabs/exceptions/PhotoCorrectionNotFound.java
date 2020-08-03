package it.polito.ai.virtualLabs.exceptions;

public class PhotoCorrectionNotFound extends VLServiceException {
    public PhotoCorrectionNotFound() {
        super("Photo ccorrection not found");
    }
}
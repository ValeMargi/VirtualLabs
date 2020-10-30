package it.polito.ai.virtualLabs.exceptions;

public class PhotoCorrectionNotFoundException extends VLServiceException {
    public PhotoCorrectionNotFoundException() {
        super("Revisione non trovata");
    }
}
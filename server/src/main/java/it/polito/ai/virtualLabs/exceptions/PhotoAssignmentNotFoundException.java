package it.polito.ai.virtualLabs.exceptions;

public class PhotoAssignmentNotFoundException extends VLServiceException {
    public PhotoAssignmentNotFoundException() {
        super("Contenuto consegna non presente");
    }
}
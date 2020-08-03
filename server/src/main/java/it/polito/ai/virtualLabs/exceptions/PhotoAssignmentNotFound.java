package it.polito.ai.virtualLabs.exceptions;

public class PhotoAssignmentNotFound extends VLServiceException {
    public PhotoAssignmentNotFound() {
        super("PhotoAssignment not found");
    }
}
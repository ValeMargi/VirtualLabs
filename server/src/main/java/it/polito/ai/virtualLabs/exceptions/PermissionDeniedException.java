package it.polito.ai.virtualLabs.exceptions;

public class PermissionDeniedException extends VLServiceException {
    public PermissionDeniedException() {
        super("User is NOT authorized!");
    }
}

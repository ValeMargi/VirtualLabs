package it.polito.ai.virtualLabs.exceptions;

public class AssignmentNotFoundException extends VLServiceException {
    public AssignmentNotFoundException() {
        super("Elaborato non presente");
    }
}

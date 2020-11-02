package it.polito.ai.virtualLabs.exceptions;

public class AssignmentNotFoundException extends VLServiceException {
    public AssignmentNotFoundException() {
        super("Consegna non presente");
    }
}

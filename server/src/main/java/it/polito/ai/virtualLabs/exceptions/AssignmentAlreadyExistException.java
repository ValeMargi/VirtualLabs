package it.polito.ai.virtualLabs.exceptions;

public class AssignmentAlreadyExistException extends VLServiceException {
    public AssignmentAlreadyExistException() {
        super("Elaborato già presente");
    }
}

package it.polito.ai.virtualLabs.exceptions;

public class AssignmentAlreadyExistException extends VLServiceException {
    public AssignmentAlreadyExistException() {
            super("Consegna già presente");
    }
}

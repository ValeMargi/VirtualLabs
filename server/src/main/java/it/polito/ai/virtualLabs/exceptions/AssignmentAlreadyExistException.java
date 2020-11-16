package it.polito.ai.virtualLabs.exceptions;

public class AssignmentAlreadyExistException extends VLServiceException {
    public AssignmentAlreadyExistException() {
            super("E' già presente una consegna con lo stesso nome");
    }
}

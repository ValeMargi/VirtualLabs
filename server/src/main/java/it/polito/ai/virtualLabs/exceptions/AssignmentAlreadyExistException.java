package it.polito.ai.virtualLabs.exceptions;

public class AssignmentAlreadyExistException extends VLServiceException {
    public AssignmentAlreadyExistException() {
        super("Assignment alreasy exist!");
    }
}

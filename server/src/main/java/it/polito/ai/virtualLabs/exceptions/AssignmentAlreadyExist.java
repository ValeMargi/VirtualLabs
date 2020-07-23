package it.polito.ai.virtualLabs.exceptions;

public class AssignmentAlreadyExist extends VLServiceException {
    public AssignmentAlreadyExist() {
        super("Assignment alreasy exist!");
    }
}

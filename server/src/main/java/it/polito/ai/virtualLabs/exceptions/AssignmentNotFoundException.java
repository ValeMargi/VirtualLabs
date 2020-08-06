package it.polito.ai.virtualLabs.exceptions;

public class AssignmentNotFound  extends VLServiceException {
    public AssignmentNotFound() {
        super("Assignment not found!");
    }
}

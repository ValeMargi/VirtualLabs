package it.polito.ai.virtualLabs.exceptions;

public class HomeworkIsPermanentException extends VLServiceException {
    public HomeworkIsPermanentException() {
        super("Homework is permanent!");
    }
}

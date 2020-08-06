package it.polito.ai.virtualLabs.exceptions;

public class HomeworkIsPermanent  extends VLServiceException {
    public HomeworkIsPermanent() {
        super("Homework is permanent!");
    }
}

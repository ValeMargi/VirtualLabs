package it.polito.ai.virtualLabs.exceptions;

public class StudentDuplicateException extends VLServiceException {
    public StudentDuplicateException() {
        super("Duplicate members");
    }
}

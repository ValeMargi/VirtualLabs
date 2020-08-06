package it.polito.ai.virtualLabs.exceptions;

public class StudentNotEnrolledToCourseException extends VLServiceException {
    public StudentNotEnrolledToCourseException() {
        super("At least one student is not enrolled in the course.");
    }
}


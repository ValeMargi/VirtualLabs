package it.polito.ai.virtualLabs.exceptions;

public class StudentNotEnrolledToCourseExcpetion extends VLServiceException {
    public StudentNotEnrolledToCourseExcpetion() {
        super("At least one student is not enrolled in the course.");
    }
}


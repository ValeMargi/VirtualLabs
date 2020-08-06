package it.polito.ai.virtualLabs.exceptions;

public class StudentAlreadyInCourseException extends VLServiceException {
    public StudentAlreadyInCourseException() {
        super("The student is aalready present in the course!");
    }
}

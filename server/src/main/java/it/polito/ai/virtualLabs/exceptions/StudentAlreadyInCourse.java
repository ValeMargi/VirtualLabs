package it.polito.ai.virtualLabs.exceptions;

public class StudentAlreadyInCourse extends VLServiceException {
    public StudentAlreadyInCourse() {
        super("The student is aalready present in the course!");
    }
}

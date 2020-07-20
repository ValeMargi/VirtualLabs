package it.polito.ai.virtualLabs.exceptions;

public class CourseAlreadyEnabledException extends VLServiceException {
    public CourseAlreadyEnabledException() {
        super("Selected course is already enabled/disabled!");
    }
}
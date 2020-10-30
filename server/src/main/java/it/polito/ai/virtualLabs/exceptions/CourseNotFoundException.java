package it.polito.ai.virtualLabs.exceptions;

public class CourseNotFoundException extends VLServiceException {
    public CourseNotFoundException() {
        super("Corso non presente");
    }
}

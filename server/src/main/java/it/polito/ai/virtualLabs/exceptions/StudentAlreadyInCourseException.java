package it.polito.ai.virtualLabs.exceptions;

public class StudentAlreadyInCourseException extends VLServiceException {
    public StudentAlreadyInCourseException() {
        super("Studente già iscritto al corso");
    }
}

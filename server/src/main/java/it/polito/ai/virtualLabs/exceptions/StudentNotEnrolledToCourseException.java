package it.polito.ai.virtualLabs.exceptions;

public class StudentNotEnrolledToCourseException extends VLServiceException {
    public StudentNotEnrolledToCourseException() {
        super("Almeno uno studente non è iscritto al corso");


    }
}


package it.polito.ai.virtualLabs.exceptions;

public class ProfessorAlreadyPresentInCourseException extends VLServiceException {
    public ProfessorAlreadyPresentInCourseException() {
        super("Professore già presente nel corso");
    }
}

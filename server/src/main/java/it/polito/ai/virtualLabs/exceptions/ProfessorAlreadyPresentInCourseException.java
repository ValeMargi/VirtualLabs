package it.polito.ai.virtualLabs.exceptions;

public class ProfessorAlreadyPresentInCourseException extends VLServiceException {
    public ProfessorAlreadyPresentInCourseException() {
        super("Professor is already present in the course");
    }
}

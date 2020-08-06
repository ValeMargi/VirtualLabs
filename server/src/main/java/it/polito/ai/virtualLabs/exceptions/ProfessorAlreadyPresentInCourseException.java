package it.polito.ai.virtualLabs.exceptions;

public class ProfessorAlreadyPresentInCourse extends VLServiceException {
    public ProfessorAlreadyPresentInCourse() {
        super("Professor is already present in the course");
    }
}

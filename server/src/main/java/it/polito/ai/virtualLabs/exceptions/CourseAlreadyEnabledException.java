package it.polito.ai.virtualLabs.exceptions;

public class CourseAlreadyEnabledException extends VLServiceException {
    public CourseAlreadyEnabledException() {
        super("Il corso selezionato è già abilitato/disabilitato");
    }
}
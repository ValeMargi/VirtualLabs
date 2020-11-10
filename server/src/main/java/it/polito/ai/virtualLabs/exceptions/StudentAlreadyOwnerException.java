package it.polito.ai.virtualLabs.exceptions;

public class StudentAlreadyOwnerException extends VLServiceException {
    public StudentAlreadyOwnerException() {
        super("Uno o più studenti sono già owner della VM");
    }
}
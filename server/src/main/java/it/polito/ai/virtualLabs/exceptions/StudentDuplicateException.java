package it.polito.ai.virtualLabs.exceptions;

public class StudentDuplicateException extends VLServiceException {
    public StudentDuplicateException() {
        super("Membri duplicati nella creazione del team");
    }
}

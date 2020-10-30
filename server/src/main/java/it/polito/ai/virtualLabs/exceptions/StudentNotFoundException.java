package it.polito.ai.virtualLabs.exceptions;

public class StudentNotFoundException extends VLServiceException {
    public StudentNotFoundException() {
        super("Studente non presente");
    }
}

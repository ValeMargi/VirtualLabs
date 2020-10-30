package it.polito.ai.virtualLabs.exceptions;

public class GradeNotValidException extends VLServiceException {
    public GradeNotValidException() {
        super("Voto inserito non valido");
    }
}
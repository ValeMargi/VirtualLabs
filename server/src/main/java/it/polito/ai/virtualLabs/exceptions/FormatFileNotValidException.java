package it.polito.ai.virtualLabs.exceptions;


public class FormatFileNotValidException extends VLServiceException {
    public FormatFileNotValidException() {
        super("Formato e/o contenuto del file non valido");
    }
}
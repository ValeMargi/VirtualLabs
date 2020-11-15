package it.polito.ai.virtualLabs.exceptions;

public class InfoStudentsCSVWrongException extends VLServiceException {
    public InfoStudentsCSVWrongException() {
        super("Informazioni studenti non corrette");
    }
}

package it.polito.ai.virtualLabs.exceptions;

public class TimeoutNotValidException extends VLServiceException {
    public TimeoutNotValidException() {  super("Data inserita non valida");  }
}
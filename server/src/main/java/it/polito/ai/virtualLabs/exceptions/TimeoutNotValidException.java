package it.polito.ai.virtualLabs.exceptions;

public class TimeoutNotValidException extends VLServiceException {
    public TimeoutNotValidException() {  super("Timeout not valid");  }
}
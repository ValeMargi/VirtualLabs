package it.polito.ai.virtualLabs.exceptions;

public class InvalidOldPasswordException extends VLServiceException {
    public InvalidOldPasswordException() {
        super("Old password inserted not correct");
    }
}
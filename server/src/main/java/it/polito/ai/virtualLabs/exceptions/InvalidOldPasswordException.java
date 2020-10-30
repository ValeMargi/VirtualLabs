package it.polito.ai.virtualLabs.exceptions;

public class InvalidOldPasswordException extends VLServiceException {
    public InvalidOldPasswordException() {
        super("Password attuale non corretta");
    }
}
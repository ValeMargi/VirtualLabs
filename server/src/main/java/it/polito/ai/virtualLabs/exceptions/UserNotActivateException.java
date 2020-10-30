package it.polito.ai.virtualLabs.exceptions;

public class UserNotActivateException extends VLServiceException {
    public UserNotActivateException() {  super("L'utente non ha confermato la registrazione");  }
}
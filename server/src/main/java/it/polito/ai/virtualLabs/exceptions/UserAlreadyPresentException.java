package it.polito.ai.virtualLabs.exceptions;

public class UserAlreadyPresentException extends VLServiceException {
    public UserAlreadyPresentException() {  super("Utente già presente");  }
}

package it.polito.ai.virtualLabs.exceptions;

public class UserAlreadyPresentException extends VLServiceException {
    public UserAlreadyPresentException() {  super("The user is already present");  }
}

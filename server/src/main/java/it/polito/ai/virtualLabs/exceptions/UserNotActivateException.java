package it.polito.ai.virtualLabs.exceptions;

public class UserNotActivateException extends VLServiceException {
    public UserNotActivateException() {  super("The user has not activated the accocunt!");  }
}
package it.polito.ai.virtualLabs.exceptions;

public class AvatarNotPresentException extends VLServiceException {
    public AvatarNotPresentException() {
        super("Avatar user not present!");
    }
}
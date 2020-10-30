package it.polito.ai.virtualLabs.exceptions;

public class AvatarNotPresentException extends VLServiceException {
    public AvatarNotPresentException() {
        super("Avatar profilo non presente");
    }
}
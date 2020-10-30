package it.polito.ai.virtualLabs.exceptions;

public class NameTeamIntoCourseAlreadyPresentException extends VLServiceException {
    public NameTeamIntoCourseAlreadyPresentException() {
        super("Nome team già presente nel corso");
    }
}

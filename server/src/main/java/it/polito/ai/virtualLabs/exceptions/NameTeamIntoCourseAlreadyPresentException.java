package it.polito.ai.virtualLabs.exceptions;

public class NameTeamIntoCourseAlreadyPresentException extends VLServiceException {
    public NameTeamIntoCourseAlreadyPresentException() {
        super("Name team already present in that course");
    }
}

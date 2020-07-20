package it.polito.ai.virtualLabs.exceptions;

public class NameTeamIntoCourseAlreadyPresent  extends VLServiceException {
    public NameTeamIntoCourseAlreadyPresent() {
        super("Name team already present in that course");
    }
}

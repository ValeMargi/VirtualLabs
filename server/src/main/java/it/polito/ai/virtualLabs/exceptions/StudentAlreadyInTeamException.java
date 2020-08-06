package it.polito.ai.virtualLabs.exceptions;

public class StudentAlreadyInTeamException extends VLServiceException {
    public StudentAlreadyInTeamException() {
        super("Member already present in an other Team for the same course.");
    }
}

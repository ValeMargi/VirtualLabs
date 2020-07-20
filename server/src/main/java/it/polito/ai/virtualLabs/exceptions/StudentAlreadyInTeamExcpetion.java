package it.polito.ai.virtualLabs.exceptions;

public class StudentAlreadyInTeamExcpetion extends VLServiceException {
    public StudentAlreadyInTeamExcpetion() {
        super("Member already present in an other Team for the same course.");
    }
}

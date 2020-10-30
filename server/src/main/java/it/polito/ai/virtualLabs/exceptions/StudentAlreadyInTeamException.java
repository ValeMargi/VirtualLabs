package it.polito.ai.virtualLabs.exceptions;

public class StudentAlreadyInTeamException extends VLServiceException {
    public StudentAlreadyInTeamException() {
        super("Almeno uno studente appartiene a un altro team del corso");
    }
}

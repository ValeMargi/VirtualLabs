package it.polito.ai.virtualLabs.exceptions;

public class ProfessorNotFoundException extends VLServiceException {
    public ProfessorNotFoundException() {
        super("Almeno un professore non esiste");
    }
}


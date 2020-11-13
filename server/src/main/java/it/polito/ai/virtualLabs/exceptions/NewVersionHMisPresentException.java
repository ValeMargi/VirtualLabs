package it.polito.ai.virtualLabs.exceptions;

public class NewVersionHMisPresentException extends VLServiceException {
    public NewVersionHMisPresentException() {
        super("Non è possibile assegnare un voto. E' disponibile una nuova versione");
    }
}

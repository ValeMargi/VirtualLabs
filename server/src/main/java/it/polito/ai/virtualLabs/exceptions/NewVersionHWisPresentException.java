package it.polito.ai.virtualLabs.exceptions;

public class NewVersionHWisPresentException extends VLServiceException {
    public NewVersionHWisPresentException() {
        super("Non è possibile assegnare un voto. E' disponibile una nuova versione");
    }
}

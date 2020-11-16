package it.polito.ai.virtualLabs.exceptions;

public class ModelVMAlreadyPresentException extends VLServiceException {
    public ModelVMAlreadyPresentException() {
        super("Modello della VM già presente");
    }
}

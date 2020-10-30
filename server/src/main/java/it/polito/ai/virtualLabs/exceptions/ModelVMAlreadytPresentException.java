package it.polito.ai.virtualLabs.exceptions;

public class ModelVMAlreadytPresentException extends VLServiceException {
    public ModelVMAlreadytPresentException() {
        super("Modello della VM già presente");
    }
}

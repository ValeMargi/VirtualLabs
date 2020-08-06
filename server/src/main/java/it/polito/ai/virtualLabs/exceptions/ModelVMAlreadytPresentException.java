package it.polito.ai.virtualLabs.exceptions;

public class ModelVMAlreadytPresentException extends VLServiceException {
    public ModelVMAlreadytPresentException() {
        super("ModelVM is already present!");
    }
}

package it.polito.ai.virtualLabs.exceptions;

public class ModelVMAlreadytPresent extends VLServiceException {
    public ModelVMAlreadytPresent() {
        super("ModelVM is already present!");
    }
}

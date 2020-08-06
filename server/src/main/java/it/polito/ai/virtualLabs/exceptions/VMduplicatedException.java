package it.polito.ai.virtualLabs.exceptions;

public class VMduplicatedException extends VLServiceException {
    public VMduplicatedException() {
        super("Duplicate VM");
    }

}

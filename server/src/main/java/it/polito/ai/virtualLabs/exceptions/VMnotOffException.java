package it.polito.ai.virtualLabs.exceptions;

public class VMnotOffException extends VLServiceException {
    public VMnotOffException() {
        super("VM must be off!");
    }

}
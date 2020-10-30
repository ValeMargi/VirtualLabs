package it.polito.ai.virtualLabs.exceptions;

public class VMnotOffException extends VLServiceException {
    public VMnotOffException() {
        super("VM accessa: deve essere spenta per modificare i parametri");
    }

}
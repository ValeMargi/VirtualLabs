package it.polito.ai.virtualLabs.exceptions;

public class VMNotFound  extends VLServiceException {
    public VMNotFound() {
        super("VM not found!");
    }
}

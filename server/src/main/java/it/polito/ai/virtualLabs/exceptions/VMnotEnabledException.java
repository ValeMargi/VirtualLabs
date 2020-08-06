package it.polito.ai.virtualLabs.exceptions;

public class VMnotEnabled  extends VLServiceException {
    public VMnotEnabled() {
        super("VM is not enabled!");
    }

}
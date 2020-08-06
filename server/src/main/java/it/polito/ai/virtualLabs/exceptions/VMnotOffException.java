package it.polito.ai.virtualLabs.exceptions;

public class VMnotOff  extends VLServiceException {
    public VMnotOff() {
        super("VM must be off!");
    }

}
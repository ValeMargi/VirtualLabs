package it.polito.ai.virtualLabs.exceptions;

public class VMnotEnabledException extends VLServiceException {
    public VMnotEnabledException() {
        super("VM spenta");
    }

}
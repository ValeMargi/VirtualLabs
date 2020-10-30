package it.polito.ai.virtualLabs.exceptions;

public class VMNotFoundException extends VLServiceException {
    public VMNotFoundException() {
        super("VM non presente");
    }
}

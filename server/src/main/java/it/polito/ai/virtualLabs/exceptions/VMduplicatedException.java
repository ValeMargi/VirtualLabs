package it.polito.ai.virtualLabs.exceptions;

public class VMduplicated extends VLServiceException {
    public VMduplicated() {
        super("Duplicate VM");
    }

}

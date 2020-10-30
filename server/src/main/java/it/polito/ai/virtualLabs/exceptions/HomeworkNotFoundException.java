package it.polito.ai.virtualLabs.exceptions;

public class
HomeworkNotFoundException extends VLServiceException {
    public HomeworkNotFoundException() {
        super("Consegna non presente");
    }
}

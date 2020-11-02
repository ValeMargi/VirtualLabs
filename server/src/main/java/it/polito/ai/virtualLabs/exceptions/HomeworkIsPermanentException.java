package it.polito.ai.virtualLabs.exceptions;

public class HomeworkIsPermanentException extends VLServiceException {
    public HomeworkIsPermanentException() {
        super("Non è più possibile caricare un elaborato");
    }
}

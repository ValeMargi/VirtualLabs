package it.polito.ai.virtualLabs.exceptions;

public class HomeworkVersionIdNotFoundException extends VLServiceException {
    public HomeworkVersionIdNotFoundException() {
        super("Homework version id not found");
    }
}
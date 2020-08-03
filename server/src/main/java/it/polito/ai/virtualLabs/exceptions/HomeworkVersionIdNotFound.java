package it.polito.ai.virtualLabs.exceptions;

public class HomeworkVersionIdNotFound extends VLServiceException {
    public HomeworkVersionIdNotFound() {
        super("Homework version id not found");
    }
}
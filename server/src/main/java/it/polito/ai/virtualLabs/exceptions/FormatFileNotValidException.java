package it.polito.ai.virtualLabs.exceptions;


public class FormatFileNotValidException extends VLServiceException {
    public FormatFileNotValidException() {
        super("Format/content file is not valid!");
    }
}
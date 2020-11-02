package it.polito.ai.virtualLabs.exceptions;

public class PhotoVersionHMNotFoundException extends VLServiceException {
    public PhotoVersionHMNotFoundException() {
        super("Versione elaborato non trovata");
    }
}
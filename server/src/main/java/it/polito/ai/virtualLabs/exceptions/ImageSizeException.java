package it.polito.ai.virtualLabs.exceptions;

public class ImageSizeException extends VLServiceException {
    public ImageSizeException() {
        super("Dimensione immagine non supportata (Massimo 15 MB)");
    }
}

package it.polito.ai.virtualLabs.exceptions;

public class CardinalityNotAcceptableException extends VLServiceException {
    public CardinalityNotAcceptableException() {
        super("Limiti dimensione team non rispettati");
    }
}
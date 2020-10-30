package it.polito.ai.virtualLabs.exceptions;

public class CardinalityNotAccetableException extends VLServiceException {
    public CardinalityNotAccetableException() {
        super("Limiti dimensione team non rispettati");
    }
}
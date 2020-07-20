package it.polito.ai.virtualLabs.exceptions;

public class CardinalityNotAccetableException extends VLServiceException {
    public CardinalityNotAccetableException() {
        super("Cardinality not acceptable.");
    }
}
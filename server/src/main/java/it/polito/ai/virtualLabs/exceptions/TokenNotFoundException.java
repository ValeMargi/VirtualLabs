package it.polito.ai.virtualLabs.exceptions;

public class TokenNotFoundException extends VLServiceException {
    public TokenNotFoundException() {  super("Token non trovato");  }
}
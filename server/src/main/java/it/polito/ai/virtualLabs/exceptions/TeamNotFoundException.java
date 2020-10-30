package it.polito.ai.virtualLabs.exceptions;

public class TeamNotFoundException extends VLServiceException {
    public TeamNotFoundException() {  super("Team non trovato");  }
}

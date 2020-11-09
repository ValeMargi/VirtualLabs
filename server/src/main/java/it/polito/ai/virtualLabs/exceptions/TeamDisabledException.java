package it.polito.ai.virtualLabs.exceptions;

public class TeamDisabledException extends VLServiceException {
    public TeamDisabledException() {  super("Team disabilitato");  }
}
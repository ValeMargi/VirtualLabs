package it.polito.ai.virtualLabs.exceptions;

public class TeamNotEnabledException extends VLServiceException {
    public TeamNotEnabledException() {  super("Team not enabled.");  }
}
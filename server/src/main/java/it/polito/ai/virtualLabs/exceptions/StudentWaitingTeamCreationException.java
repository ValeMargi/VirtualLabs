package it.polito.ai.virtualLabs.exceptions;

public class StudentWaitingTeamCreationException extends VLServiceException {
    public StudentWaitingTeamCreationException() {  super("Student is waiting for Team creation");  }
}
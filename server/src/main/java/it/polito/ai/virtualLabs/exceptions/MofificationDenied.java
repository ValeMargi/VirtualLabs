package it.polito.ai.virtualLabs.exceptions;

public class MofificationDenied  extends VLServiceException {
    public MofificationDenied() {
        super("It is not possible to modify the homework!");
    }
}

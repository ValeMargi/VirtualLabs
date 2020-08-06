package it.polito.ai.virtualLabs.exceptions;

public class ModificationDeniedException extends VLServiceException {
    public ModificationDeniedException() {
        super("It is not possible to modify the homework!");
    }
}

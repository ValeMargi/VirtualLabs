package it.polito.ai.virtualLabs.exceptions;

public class ModificationDenied extends VLServiceException {
    public ModificationDenied() {
        super("It is not possible to modify the homework!");
    }
}

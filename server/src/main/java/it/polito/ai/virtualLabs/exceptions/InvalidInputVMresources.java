package it.polito.ai.virtualLabs.exceptions;

public class InvalidInputVMresources extends VLServiceException {
    public InvalidInputVMresources() {
        super(" Cannot insert negative or equal 0 value for the VM resources!");
    }
}
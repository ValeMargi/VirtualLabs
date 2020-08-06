package it.polito.ai.virtualLabs.exceptions;

public class ResourcesVMNotRespectedException extends VLServiceException {
    public ResourcesVMNotRespectedException() {
        super("VM resources are not respected!");
    }
}

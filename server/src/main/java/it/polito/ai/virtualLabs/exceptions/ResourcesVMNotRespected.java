package it.polito.ai.virtualLabs.exceptions;

public class ResourcesVMNotRespected extends VLServiceException {
    public ResourcesVMNotRespected() {
        super("VM resources are not respected!");
    }
}

package it.polito.ai.virtualLabs.exceptions;

public class ResourcesVMNotRespectedException extends VLServiceException {
    public ResourcesVMNotRespectedException() {
        super("Vincolo risorse VM non rispettato");
    }
}

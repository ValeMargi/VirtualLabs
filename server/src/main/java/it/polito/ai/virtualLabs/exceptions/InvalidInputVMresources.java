package it.polito.ai.virtualLabs.exceptions;

public class InvalidInputVMresources extends VLServiceException {
    public InvalidInputVMresources() {
        super(" Impossibile inserire valori negativi o nulli per le risorse della VM");
    }
}
package it.polito.ai.virtualLabs.exceptions;

public class ModelVMNotSettedException extends VLServiceException {
        public ModelVMNotSettedException() {
            super("Modello della VM non definito");
        }
}

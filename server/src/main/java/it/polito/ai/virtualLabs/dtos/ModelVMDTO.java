package it.polito.ai.virtualLabs.dtos;

import lombok.Data;

@Data
public class ModelVMDTO {
    private String id;
    private int maxVcpu, diskSpace, ram, runningInstances, totInstances;
}

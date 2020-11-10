package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {
    private String name, acronym;
    private int min, max;
    private boolean enabled;
    private int maxVcpu, diskSpace, ram, runningInstances, totInstances;

}

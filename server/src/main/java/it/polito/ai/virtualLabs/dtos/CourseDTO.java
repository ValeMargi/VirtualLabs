package it.polito.ai.virtualLabs.dtos;

import org.springframework.hateoas.RepresentationModel;

public class CourseDTO extends RepresentationModel<CourseDTO> {
    private String name, acronym;
    private int min, max;
    private boolean enabled;
}

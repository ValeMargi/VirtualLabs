package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;


@Data
public class AssignmentDTO  extends RepresentationModel<AssignmentDTO> {
    private  Long id;
    private String assignmentName;
    private String releaseDate, expiration;
    private Boolean alreadyExpired;
}

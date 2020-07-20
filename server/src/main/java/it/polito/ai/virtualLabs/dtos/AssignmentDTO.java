package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@Data
public class AssignmentDTO  extends RepresentationModel<AssignmentDTO> {
    private  String id, content;
    private Date release, expiration;

}

package it.polito.ai.virtualalbs.dtos;

import it.polito.ai.virtualalbs.entities.Professor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Properties;

@Data
public class AssignmentDTO  extends RepresentationModel<AssignmentDTO> {
    private  String id, content;
    private Date release, expiration;

}

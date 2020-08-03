package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class AssignmentDTO  extends RepresentationModel<AssignmentDTO> {
    private  Long id;
    private String assignmentName;
    private Date releaseDate, expiration;

}

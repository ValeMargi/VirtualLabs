package it.polito.ai.virtualalbs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ProfessorDTO extends RepresentationModel<ProfessorDTO> {
    private String id;
    private String name, firstName, email, photoId;
}

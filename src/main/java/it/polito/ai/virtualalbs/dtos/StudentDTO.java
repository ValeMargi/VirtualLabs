package it.polito.ai.virtualalbs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class StudentDTO extends RepresentationModel<StudentDTO> {
    private String id;
    private String name, firstName, email;
}

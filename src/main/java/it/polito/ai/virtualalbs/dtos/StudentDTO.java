package it.polito.ai.virtualalbs.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
public class StudentDTO extends RepresentationModel<StudentDTO> {
    private String id;
    private String name, firstName, email;

    public StudentDTO(String id, String firstName, String name, String email) {
        this.id = id;
        this.firstName = firstName;
        this.name = name;
        this.email = email;
    }
}

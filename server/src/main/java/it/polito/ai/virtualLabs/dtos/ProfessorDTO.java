package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
public class ProfessorDTO extends RepresentationModel<ProfessorDTO> {
    private String id;
    private String name, firstName, email;

    public ProfessorDTO(String id, String firstName, String name, String email) {
        this.id = id;
        this.firstName = firstName;
        this.name = name;
        this.email = email;
         }


}

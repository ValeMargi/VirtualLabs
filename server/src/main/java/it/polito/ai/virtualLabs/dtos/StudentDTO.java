package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
public class StudentDTO extends RepresentationModel<StudentDTO> {
    private String id;
    private String name, firstName, email;

    private String nameFile;
    private String type;
    private byte[] picByte;

    public StudentDTO(String id, String firstName, String name, String email, String nameFile, String type, byte[] picByte) {
        this.id = id;
        this.firstName = firstName;
        this.name = name;
        this.email = email;
        this.nameFile = nameFile;
        this.type = type;
        this.picByte = picByte;

    }
}

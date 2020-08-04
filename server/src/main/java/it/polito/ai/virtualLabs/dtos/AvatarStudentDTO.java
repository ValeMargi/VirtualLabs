package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class AvatarStudentDTO extends RepresentationModel<AvatarStudentDTO> {

    private  Long id;
    private String nameFile;
    private String type;
    private byte[] picByte;
}

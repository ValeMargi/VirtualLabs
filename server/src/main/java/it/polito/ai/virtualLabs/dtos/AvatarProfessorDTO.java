package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class AvatarProfessorDTO extends RepresentationModel<AvatarProfessorDTO> {

    private  Long id;
    private String nameFile;
    private String type;
    private byte[] picByte;
}

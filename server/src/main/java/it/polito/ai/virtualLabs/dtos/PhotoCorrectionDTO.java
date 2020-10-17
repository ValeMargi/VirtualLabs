package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class PhotoCorrectionDTO extends RepresentationModel<PhotoCorrectionDTO> {
    Long id, idVersionHomework;
    String idProfessor;
    String timestamp;
    private String nameFile;
    private String type;
    private byte[] picByte;
}

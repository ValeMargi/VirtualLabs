package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class PhotoCorrectionDTO extends RepresentationModel<PhotoCorrectionDTO> {
    private Long id, idVersionHomework;
    private String idProfessor;
    private  String timestamp;
    private String nameFile;
    private String type;
    private byte[] picByte;
}

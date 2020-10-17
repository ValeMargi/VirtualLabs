package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
@Data
public class PhotoVersionHomeworkDTO extends RepresentationModel<PhotoVersionHomeworkDTO> {
    private String timestamp;
    private  Long id;
    private String nameFile;
    private String type;
    private byte[] picByte;

}

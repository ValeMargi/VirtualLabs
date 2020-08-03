package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Data
public class PhotoVersionHomeworkDTO extends RepresentationModel<PhotoVersionHomeworkDTO> {
    private String timestamp;
    private  Long id;
    private String name;
    private String type;
    private byte[] picByte;

}

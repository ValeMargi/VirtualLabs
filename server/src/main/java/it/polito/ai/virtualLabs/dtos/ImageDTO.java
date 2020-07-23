package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Data
public class ImageDTO extends RepresentationModel<ImageDTO> {
    private Long id;
    private String name,type;
    private Timestamp timestamp;
    private byte[] picByte;


}

package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ImageDTO extends RepresentationModel<ImageDTO> {
    private String name,type;
    private byte[] picByte;


}

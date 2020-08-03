package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class PhotoVMDTO  extends RepresentationModel<PhotoVMDTO> {
    private Long id;
    private String name;
    private String type;
    private byte[] picByte;

}

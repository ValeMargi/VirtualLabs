package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Data
public class PhotoAssignmentDTO extends RepresentationModel<PhotoAssignmentDTO> {
    private Long id;
    private String name;
    private String type;
    private byte[] picByte;
    private String timestamp;

}

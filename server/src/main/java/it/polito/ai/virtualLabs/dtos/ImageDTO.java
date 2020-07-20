package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Data
public class ImageDTO extends RepresentationModel<ImageDTO> {
    private String id;
    private Timestamp timestamp;


}

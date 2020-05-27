package it.polito.ai.virtualalbs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
public class ImageDTO extends RepresentationModel<ImageDTO> {
    private String id;
    private Timestamp timestamp;


}

package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Data
public class PhotoCorrectionDTO extends RepresentationModel<PhotoCorrectionDTO> {

    Long id, idVersionHomework;
    String idProfessor;
    Timestamp timestamp;
}
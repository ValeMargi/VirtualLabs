package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class HomeworkDTO extends RepresentationModel<HomeworkDTO> {
    private Long id;
    private  String status;
    private boolean permanent;
    private String grade;

}

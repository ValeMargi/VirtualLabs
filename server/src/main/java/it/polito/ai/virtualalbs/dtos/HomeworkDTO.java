package it.polito.ai.virtualalbs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
public class HomeworkDTO extends RepresentationModel<HomeworkDTO> {
    private String id;
    private  String status;
    private boolean permanent;
    private String grade;

}

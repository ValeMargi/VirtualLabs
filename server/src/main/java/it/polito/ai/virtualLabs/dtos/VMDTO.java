package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;


@Data
@EqualsAndHashCode(callSuper = true)
public class VMDTO extends RepresentationModel<VMDTO> {
    private Long id;
    private String nameVM;
    private int  numVcpu, diskSpace, ram;
    private String status;


}

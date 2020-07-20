package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = true)
public class VMDTO extends RepresentationModel<VMDTO> {
    private String id;
    private int  maxVpcu, diskSpace, ram;
    private String status;
}

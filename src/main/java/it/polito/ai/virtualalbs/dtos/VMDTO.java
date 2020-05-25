package it.polito.ai.virtualalbs.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class VMDTO extends RepresentationModel<VMDTO> {
    private String id;
    private int  maxVpcu, diskSpace, ram;
    private String status;
}

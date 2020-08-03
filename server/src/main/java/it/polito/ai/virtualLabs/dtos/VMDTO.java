package it.polito.ai.virtualLabs.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper = true)
public class VMDTO extends RepresentationModel<VMDTO> {
    private String id;
    private int  numVcpu, diskSpace, ram;
    private String status;

    //AGGIUNTI
    private Timestamp timestamp;
    private String name;
    private String type;
    private byte[] picByte;
}

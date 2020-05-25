package it.polito.ai.virtualalbs.dtos;

import org.springframework.hateoas.RepresentationModel;

public class TeamDTO extends RepresentationModel<TeamDTO> {
    private Long id;
    private  String name;
    private int status, maxVpcu, diskSpace, ram, runningInstances, totInstances;

}

package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.CourseDTO;
import it.polito.ai.virtualLabs.dtos.ProfessorDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ModelHelper {
    public static CourseDTO enrich(CourseDTO courseDTO){
        Link selfLink= linkTo(CourseController.class).slash(courseDTO.getName()).withSelfRel();
        Link enrolledLink = linkTo(CourseController.class).slash(courseDTO.getName()).slash("enrolled").withRel("enrolled");
        courseDTO.add(enrolledLink);
        return courseDTO.add(selfLink);
    }


    public static StudentDTO enrich(StudentDTO studentDTO){
        Link selfLink= linkTo(StudentController.class).slash(studentDTO.getId()).withSelfRel();
        return studentDTO.add(selfLink);
    }

    public static ProfessorDTO enrich(ProfessorDTO professorDTO){
        Link selfLink= linkTo(UserController.class).slash("professors").slash(professorDTO.getId()).withSelfRel();
        return professorDTO.add(selfLink);
    }
}

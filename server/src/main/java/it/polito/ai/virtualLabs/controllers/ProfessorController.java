package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.CourseDTO;
import it.polito.ai.virtualLabs.services.VLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/API/professors")
public class ProfessorController {

    @Autowired
    VLService vlService;
    /**
     * Metodo: GET
     * Authority: Docente
     * @param professorId: riceve dal path l'id di un professore
     * @return: ritorna una lista di DTO dei corsi di cui il professore con professorId indicato è titolare
     */
    @GetMapping("/{professorId}")
    public List<CourseDTO> getCoursesForProfessor(@PathVariable String professorId){
        return vlService.getCoursesForProfessor(professorId).stream().map(c-> ModelHelper.enrich(c)).collect(Collectors.toList());
    }
}

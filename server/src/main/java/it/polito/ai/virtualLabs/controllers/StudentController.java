package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.VLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/students")
public class StudentController {
    @Autowired
    VLService vlService;
    @GetMapping({"", "/"})
    public List<StudentDTO> all() {
        return vlService.getAllStudents().stream().map(s -> ModelHelper.enrich(s)).collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public StudentDTO getOne(@PathVariable String id) {
        Optional<StudentDTO> studentDTO = vlService.getStudent(id);
        if (!studentDTO.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id:"+id+" not present");
        else
            return ModelHelper.enrich(studentDTO.get());
    }

    /**
     * Metodo: GET
     * Authority: Studente
     * @param studentId: riceve dal path l'id di uno studente
     * @return: ritorna una lista di DTO dei corsi a cui lo studente con studentId indicato è iscritto
     */
      /*GET mapping request to see the list of courses enrolled by a student with studentId*/
    @GetMapping("/{studentId}/courses")
    public List<CourseDTO> getCourses(@PathVariable String studentId) {
        try {
            return  vlService.getCoursesForStudent(studentId).stream().map(c-> ModelHelper.enrich(c)).collect(Collectors.toList());

        } catch (StudentNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, studentId);
        }
    }

    /* GET mapping request to see the list of students who are part of a team in a given course*/
    @GetMapping("/inTeam/{courseName}")
    public List<StudentDTO> getStudentsInTeams(@PathVariable String courseName) {
        try {
            return vlService.getStudentsInTeams(courseName).stream().map(s-> ModelHelper.enrich(s)).collect(Collectors.toList());
        } catch (CourseNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+courseName+" not present");
        }
    }

    /* GET mapping request to see the list of students who are not yet part of a team in a given course*/
    @GetMapping("/notInTeam/{courseName}")
    public List<StudentDTO> getAvailableStudents(@PathVariable String courseName) {
        try {
            return vlService.getAvailableStudents(courseName).stream().map(s-> ModelHelper.enrich(s)).collect(Collectors.toList());
        } catch (CourseNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+courseName+" not present");
        }
    }

    /**
     * Authrority: Studente
     * @param courseName
     * @return: ritrona lista di VM dto con le informazioni
     *          di tutte le VM del team di cui lo studente autenticato è membro
     */
    @GetMapping("/VM/{courseName}")
    public List<VMDTO> allVMforStudent(@PathVariable String courseName) {
        try{
            return vlService.allVMforStudent(courseName);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Authrority: Studente
     * @param courseName
     * @param VMid
     * @return: ritrona  VM dto con le informazioni della VM con id pari a VMid
     */
    @GetMapping("/VM/{courseName}/{VMid}")
    public PhotoVMDTO getVMforStudent(@PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlService.getVMforStudent(courseName, VMid);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }



    @GetMapping("/VM/{courseName}/{VMid}/owner")
    public boolean isOwner(  @PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlService.isOwner( VMid);
        } catch (PermissionDeniedException | VMNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }



    /**
     * Metodo: GET
     * Authority: Studente
     * @param courseName: riceve dal path il nome del corso di cui si vuole elencare le consegne associate
     * @return: ritorna la lista di consegne associate al corso con nome pari a CourseName
     */
    @GetMapping("/{courseName}/assignment")
    public List<AssignmentDTO> allAssignment(@PathVariable String courseName) {
        try{
            return  vlService.allAssignmentStudent(courseName);
        } catch (CourseNotFoundException  | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: Get
     * Authority: Studente
     * @param courseName
     * @param assignmentId
     * @return informazioni assignment dello studente
     */
    @GetMapping("/{courseName}/{assignmentId}/getAssignment")
    public PhotoAssignmentDTO getAssignment(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlService.getAssignmentStudent( assignmentId);
        } catch (CourseNotFoundException  | StudentNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

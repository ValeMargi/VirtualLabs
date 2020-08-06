package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.VLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/teams")
public class TeamController {
    @Autowired
    VLService vlService;

    /**
     * Metodo: POST
     * Authority: Studente
     * @param courseName: riceve dal path il nome di un Corso a cui lo studente autenticato è iscritto
     * @param object: Nel corso della richiesta viene passato il nome del Team proposto dallo studente e la lista degli id degli studenti che formeranno il gruppo
     *              Esempio Body: {"nameTeam": "Team1",
     *                             "membersId": {"s1", "s2", "s2"}
     *                            }
     * @return: ritorna il DTO del Team appena creato (Long id;String name;int status;)
     */
    @PostMapping("/{courseName}/proposeTeam")
    public TeamDTO proposeTeam(@PathVariable String courseName, @RequestBody Map<String, Object> object) {
        try {
            String nameTeam = object.get("nameTeam").toString();
            List<String> membersId= (List<String>)object.get("membersId");
            return vlService.proposeTeam(courseName, nameTeam, membersId);
        } catch (  CourseNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(StudentNotEnrolledToCourseException | StudentAlreadyInTeamException
                | CardinalityNotAccetableException
                | StudentDuplicateException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());

        }
    }

    /**
     * Authority: Student and Professor
     * @param courseName
     * @return ritorna la lista di teamDTO per un dato corso
     */
    /*GET mapping request to see the list of groups of a given course*/
    @GetMapping("/{courseName}/forCourse")
    public List<TeamDTO> getTeamsForCourse(@PathVariable String courseName) {
        try {
            return vlService.getTeamForCourse(courseName).stream().map(t -> ModelHelper.enrich(t)).collect(Collectors.toList());
        }catch(CourseNotFoundException cnfe){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, cnfe.getMessage());
        }
    }

    /*POST mapping request to see the list of students enrolled in a team with id=teamId*/
    /**
     * Metodo: GET
     * @param teamId: riceve dal path L'ID di un determinato Team
     * @return: ritorna la lista di DTO degli studenti iscritti a team con id pari a teamId
     */
    @GetMapping("/{teamId}/members")
    public List<StudentDTO> getMembersTeam(@PathVariable Long teamId) {
        try{
            return vlService.getMembers(teamId).stream().map(s -> ModelHelper.enrich(s)).collect(Collectors.toList());
        }catch (TeamNotFoundException tnfe){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team " +teamId.toString() +"not present!");
        }
    }

    /**
     *
     * @param studentId
     * @return ritorna la lista di team DTO a cui lo studente con id=studentId è iscritto
     */
    /*GET mapping request to see the list of teams a particular student is enrolled in*/
    @GetMapping("/{studentId}/forStudent")
    public List<TeamDTO> getTeamsForStudent(@PathVariable String studentId) {
        try {
            return vlService.getTeamsForStudent(studentId).stream().map(s -> ModelHelper.enrich(s)).collect(Collectors.toList());
        } catch (StudentNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Student with id:"+studentId+" not present");
        }catch(PermissionDeniedDataAccessException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  p.getMessage());

        }
    }

    /* GET mapping request to see the list of students who are part of a team in a given course*/
    @GetMapping("/inTeam/{courseName}")
    public List<StudentDTO> getStudentsInTeams(@PathVariable String courseName) {
        try {
            return vlService.getStudentsInTeams(courseName).stream().map(s-> ModelHelper.enrich(s)).collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /* GET mapping request to see the list of students who are not yet part of a team in a given course*/
    @GetMapping("/notInTeam/{courseName}")
    public List<StudentDTO> getAvailableStudents(@PathVariable String courseName) {
        try {
            return vlService.getAvailableStudents(courseName).stream().map(s-> ModelHelper.enrich(s)).collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

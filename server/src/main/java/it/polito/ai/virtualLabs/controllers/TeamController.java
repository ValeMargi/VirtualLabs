package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.dtos.VMDTO;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.VLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
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
        if ( !object.containsKey("nameTeam") ||  !object.containsKey("timeout") || !object.containsKey("membersId"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try {
            String nameTeam = object.get("nameTeam").toString();
            Timestamp timeout = Timestamp.valueOf(object.get("timeout").toString());
            List<String> membersId= (List<String>)object.get("membersId");
            return vlService.proposeTeam(courseName, nameTeam, membersId, timeout);
        } catch (  CourseNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(StudentNotEnrolledToCourseException | StudentAlreadyInTeamException
                | CardinalityNotAccetableException
                | StudentDuplicateException
                | CourseDisabledException
                | TimeoutNotValidException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());

        }
    }

    @GetMapping("/{courseName}/getProposals")
    public List<Map<String, Object>> getProposal(@PathVariable String courseName) {
        try {
            return vlService.getProposals(courseName);
        }catch(StudentWaitingTeamCreationException | StudentAlreadyInTeamException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch (TeamNotFoundException | StudentNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
            return vlService.getTeamForCourse(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
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
            return vlService.getMembers(teamId).stream().map(ModelHelper::enrich).collect(Collectors.toList());
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
    @GetMapping("/{studentId}/teams")
    public List<TeamDTO> getTeamsForStudent(@PathVariable String studentId) {
        try {
            return vlService.getTeamsForStudent(studentId).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        } catch (StudentNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Student with id:"+studentId+" not present");
        }catch(PermissionDeniedDataAccessException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  p.getMessage());

        }
    }

    /**
     *
     * @param courseId, studentId
     * @return ritorna il team DTO per il corso con id=courseId a cui lo studente con id=studentId è iscritto
     */
    @GetMapping("/{courseId}/{studentId}/team")
    public TeamDTO getTeamForStudent(@PathVariable String courseId, @PathVariable String studentId) {
        try {
            return vlService.getTeamForStudent(courseId, studentId);
        } catch (CourseNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Course with id:" + courseId + " not present");
        } catch (StudentNotFoundException snfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Student with id:" + studentId + " not present");
        } catch(PermissionDeniedDataAccessException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  p.getMessage());
        }
    }

    /* GET mapping request to see the list of students who are part of a team in a given course*/
    @GetMapping("/{courseName}/inTeam")
    public List<StudentDTO> getStudentsInTeams(@PathVariable String courseName) {
        try {
            return vlService.getStudentsInTeams(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /* GET mapping request to see the list of students who are not yet part of a team in a given course*/
    @GetMapping("/{courseName}/notInTeam")
    public List<StudentDTO> getAvailableStudents(@PathVariable String courseName) {
        try {
            return vlService.getAvailableStudents(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/{teamId}/getVM")
    public List<VMDTO> getAllVMTeam(@PathVariable String courseName, @PathVariable Long teamId) {
        try{
            return vlService.getAllVMTeam(teamId);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (TeamNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}

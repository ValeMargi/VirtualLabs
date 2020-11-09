package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.dtos.VMDTO;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.NotificationService;
import it.polito.ai.virtualLabs.services.VLService;
import it.polito.ai.virtualLabs.services.VLServiceStudent;
import it.polito.ai.virtualLabs.services.VLServiceStudentImpl;
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
    @Autowired
    VLServiceStudent vlServiceStudent;

    /**
     * Metodo: POST
     * Authority: Student
     * @param courseName: nome del corso a cui lo studente autenticato è iscritto
     * @param object:   mappa contenente il nome del Team proposto dallo studente e la lista degli ID degli studenti che formeranno il gruppo
     *                  {
     *                      "nameTeam": "...",
     *                      "membersId": {...}
     *                  }
     * @return: ritorna il DTO del Team appena creato
     */
    @PostMapping("/{courseName}/proposeTeam")
    public Map<String,Object> proposeTeam(@PathVariable String courseName, @RequestBody Map<String, Object> object) {
        //controllo che tutti i campi della mappa siano presenti
        if ( !object.containsKey("nameTeam") ||  !object.containsKey("timeout") || !object.containsKey("membersId"))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Parametri non conformi con la richiesta");
        try {
            String nameTeam = object.get("nameTeam").toString();
            Timestamp timeout = Timestamp.valueOf(object.get("timeout").toString());
            List<String> membersId= (List<String>)object.get("membersId");
            membersId.forEach(member -> member = member.trim());
            return vlServiceStudent.proposeTeam(courseName, nameTeam, membersId, timeout);
        } catch (CourseNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(StudentNotEnrolledToCourseException | StudentAlreadyInTeamException
                | CardinalityNotAccetableException
                | StudentDuplicateException
                | CourseDisabledException
                | TimeoutNotValidException
                | NameTeamIntoCourseAlreadyPresentException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());

        }
    }

    /**
     * Metodo: GET
     * Authority: Student
     * @param courseName: nome del corso
     * @return: ritorna una lista di mappe contenenti il nome del team, i dati del creatore del team, lo stato del team,
     *           l'ID del token dell studente autenticato collegato al team, lo stato del token, la scadenza del token
     *           e una ulteriore lista di mappe contenenti i dati degli studenti invitati al gruppo e lo stato delle loro richieste
     *           {
     *             teamname:"...",
     *             creator:"...",
     *             teamStatus."...",
     *             tokenId:"...",
     *             status:"...",
     *             scadenza:"...",
     *             students: {
     *                 {
     *                   student:"...",
     *                   status:"..."
     *                 }
     *             }
     *           }
     */
    @GetMapping("/{courseName}/getProposals")
    public List<Map<String, Object>> getProposal(@PathVariable String courseName) {
        try {
            return vlServiceStudent.getProposals(courseName);
        }catch( StudentAlreadyInTeamException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch (TeamNotFoundException | StudentNotFoundException | TokenNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * @param courseName:nome del corso
     * @return: ritorna la lista di DTO dei team del corso
     */
    @GetMapping("/{courseName}/forCourse")
    public List<TeamDTO> getTeamsForCourse(@PathVariable String courseName) {
        try {
            return vlService.getTeamForCourse(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * @param teamId: ID del team richiesto
     * @return: ritorna la lista di DTO degli studenti appartenenti al team
     */
    @GetMapping("/{teamId}/members")
    public List<StudentDTO> getMembersTeam(@PathVariable Long teamId) {
        try{
            return vlService.getMembers(teamId).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch (TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * @param studentId: ID dello studente richiesto
     * @return: ritorna la lista di DTO dei team a cui lo studente è iscritto
     */
    @GetMapping("/{studentId}/teams")
    public List<TeamDTO> getTeamsForStudent(@PathVariable String studentId) {
        try {
            return vlServiceStudent.getTeamsForStudent(studentId).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedDataAccessException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  p.getMessage());

        }
    }

    /**
     * Authority: Student
     * @param courseName: nome del corso
     * @param studentId: ID dello studente richiesto
     * @return: ritorna il DTO del team al quale lo studente appartiene per quel corso
     */
    @GetMapping("/{courseName}/{studentId}/team")
    public TeamDTO getTeamForStudent(@PathVariable String courseName, @PathVariable String studentId) {
        try {
            return vlServiceStudent.getTeamForStudent(courseName, studentId);
        } catch (CourseNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  e.getMessage());
        } catch (StudentNotEnrolledToCourseException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  e.getMessage());
        } catch(PermissionDeniedDataAccessException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  p.getMessage());
        }
    }

    /**
     * Authority: Student
     * @param courseName: nome del corso
     * @return: ritorna la lista di DTO degli studenti appartenenti a un team nel corso
     */
    @GetMapping("/{courseName}/inTeam")
    public List<StudentDTO> getStudentsInTeams(@PathVariable String courseName) {
        try {
            return vlServiceStudent.getStudentsInTeams(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * @param courseName: nome del corso
     * @return: ritorna la lista di DTO degli studenti non appartenenti a un team nel corso
     */
    @GetMapping("/{courseName}/notInTeam")
    public List<StudentDTO> getAvailableStudents(@PathVariable String courseName) {
        try {
            return vlServiceStudent.getAvailableStudents(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * @param courseName: nome del corso
     * @param teamId: ID del team richiesto
     * @return: ritorna la lista di DTO delle VM create dal team
     */
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

    /**
     * @param token: token collegato alla creazione del team
     * @return: ritorna un intero che identifica l'esito della richiesta di adesione al team:
     *          - 0 se il token non è più presente
     *          - 1 se l'adesione è andata a buon fine ma il team non è ancora attivo
     *          - 2 se l'adesione è andata a buon fine e il team viene attivato
     */
    @GetMapping("/confirm/{token}")
    @ResponseBody
    public int confirmationPage(@PathVariable String token) {
        try {
            return vlService.confirm(token);
        }catch (TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (CourseDisabledException | TeamDisabledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * @param token: token collegato alla creazione del team
     * @return: ritorna un intero che identifica l'esito della richiesta di rifiuto di adesione al team:
     *          - 0 se il token non è più presente
     *          - 1 se il rifiuto è andato a buon fine
     */
    @GetMapping("/reject/{token}")
    @ResponseBody
    public int rejectionPage(@PathVariable String token) {
        try {
            return vlService.reject(token);
        }catch(TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (CourseDisabledException | TeamDisabledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}

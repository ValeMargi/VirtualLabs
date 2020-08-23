package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.VLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/API/courses")
public class CourseController {

    /*Metodi non implementati:
    *  - GetModelVM
    * */


    @Autowired
    VLService vlService;

    @GetMapping({"", "/"})
    public List<CourseDTO> all(){
        return vlService.getAllCourses().stream().map(c-> ModelHelper.enrich(c)).collect(Collectors.toList());
    }

    /**
     * Metodo: GET
     * @param name: riceve dal path il nome di un Corso
     * @return: ritorna il DTO (String name, acronym;
     *                          int min, max; boolean enabled;)
     *          del corso associato (tramite link cliccabile)
     */
    @GetMapping("/{name}")
    public CourseDTO getOne(@PathVariable String name){
        Optional<CourseDTO > courseDTO= vlService.getCourse(name);
        if( !courseDTO.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+name+" not present");
        else
            return  ModelHelper.enrich(courseDTO.get());
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseDTO: parametro acquisito dal corpo della richiesta
     *                 (String name, acronym;  int min, max; boolean enabled;)
     * @return: ritorna il DTO del corso
     */
    @PostMapping({"", "/"})
    public CourseDTO addCourse(@RequestPart("course") CourseDTO courseDTO, @RequestPart("professors") String[] professorsId ) {
        try {
            if (vlService.addCourse(courseDTO, Arrays.asList(professorsId))) {
                return ModelHelper.enrich(courseDTO);
            } else
                throw new ResponseStatusException(HttpStatus.CONFLICT, courseDTO.getName());
        }catch (ProfessorNotFoundException p){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, p.getMessage());
        }
    }

    /*POST mapping request to enable / disable course "name"*/
    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseName: riceve dal path il nome di un Corso
     * @param enabled: flag (true/false) che indica se il Professore deve abilitare/disabilitare il dato corso
     */
    @PostMapping("/{courseName}/enable")
    public void enableCourse(@PathVariable String courseName, @RequestBody Boolean enabled){
        try {
            if(enabled)
                vlService.enableCourse(courseName);
            else
                vlService.disableCourse(courseName);
        }catch(CourseNotFoundException cntfe ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, cntfe.getMessage());
        }catch (PermissionDeniedException permissionException){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, permissionException.getMessage());
        }catch (CourseAlreadyEnabledException enabledException){
            throw new ResponseStatusException(HttpStatus.CONFLICT, enabledException.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseName:riceve dal path il nome di un Corso da rimuovere
     * @return: ritorna l'esito della rimozione del corso indicato
     */
    @DeleteMapping("/{courseName}/remove")
    public boolean removeCourse(@PathVariable String courseName){
        try{
            return vlService.removeCourse(courseName);
        }catch(CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch (ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseName:riceve dal path il nome di un Corso da modificare
     * @param courseDTO:  Nel corpo della richiesta viene passato il DTO del corso modificato"
     *      *               Esempio Body: {"name": "...",
     *                                     "acronym": "...",
     *                                     "min":"...",
     *                                     "max":"...",
     *                                     "enabled":"...."}
     * @return: ritorna l'esito della modifica del corso indicato
     */
    @PostMapping("/{courseName}/modify")
    public boolean modifyCourse(@PathVariable String courseName, @RequestBody CourseDTO courseDTO){
        try{
            return vlService.modifyCourse(courseDTO);
        }catch(CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch (CardinalityNotAccetableException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }


    /**
     * Metodo: POST
     * Authority: Docente
     * @param professorsId: parametro acquisito dal corspo della richiesta (riceve una lista di String idProfessor MATRICOLA es p1,p2,p3;)
     * @param courseName:  riceve dal path il nome di un Corso
     * @return: ritorna il DTO del professore aggiunto al corso con CourseName indicato
     * @return: ritorna il DTO del professore aggiaddAssunto al corso con CourseName indicato
     */
    @PostMapping({"/{courseName}/addProfessors"})
    public List<ProfessorDTO> addProfessorsToCourse(@RequestBody String[] professorsId, @PathVariable String courseName){
        try{
            return vlService.addProfessorsToCourse(courseName, Arrays.asList(professorsId)).stream().map(p-> ModelHelper.enrich(p)).collect(Collectors.toList());
        }catch(CourseNotFoundException | ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(ProfessorAlreadyPresentInCourseException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param memberId: id dello studente da aggiungere
     * @param courseName: riceve dal path il nome di un Corso
     */
    @PostMapping("/{courseName}/enrollOne")
    @ResponseStatus(HttpStatus.CREATED)
    public void enrollOne(@RequestBody String memberId, @PathVariable String courseName){
        try{
            if (!vlService.addStudentToCourse(memberId, courseName))
                throw new StudentAlreadyInCourseException();
        }catch (StudentNotFoundException | CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());

        }
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseName: riceve dal path il nome di un Corso
     * @param file: riceve un file in cui sono presenti gli id degli studenti da iscrivere al corso con nome pari a courseName
     * @return: ritorna una lista di boolean per tener traccia se l'aggiunta di ogni studente al dato corso ha avuto successo o meno
     */
    @PostMapping("/{courseName}/enrollMany")
    public List<Boolean> enrollStudents(@PathVariable String courseName, @RequestParam("file") MultipartFile file){
        if( !file.getContentType().equals("text/csv") && !file.getContentType().equals("application/vnd.ms-excel"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File provided is type "+file.getContentType()+" not text/csv");
        else
            try {
                return vlService.EnrollAllFromCSV(new BufferedReader(new InputStreamReader(file.getInputStream())), courseName);
            }catch(FormatFileNotValidException | IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
    }

    @PostMapping("/{courseName}/enrollAll")
    public List<Boolean> enrollAll(@RequestBody String[] membersId, @PathVariable String courseName){
        try{
             return vlService.enrollAll(Arrays.asList(membersId), courseName);
        }catch (StudentNotFoundException | CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
    @PostMapping("/{courseName}/removeStudent")
    public List<Boolean> deleteStudentsFromCourse(@PathVariable String courseName, @PathVariable String[] studentsId){
        try{
            return vlService.deleteStudentsFromCourse(Arrays.asList(studentsId), courseName);
        }catch (StudentNotFoundException | CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());

        }
    }


    /**
     * Metodo: GET
     * @param courseName: riceve dal path il nome di un Corso
     * @return: ritorna la lista di StudentDTO degli studenti iscritti al dato Corso
     */
    /*GET mapping request to see the list of students enrolled in the course "name"*/
    @GetMapping("/{courseName}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable  String courseName){
        try {
            return vlService.getEnrolledStudents(courseName).stream().map(s -> ModelHelper.enrich(s)).collect(Collectors.toList());
        }catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }




    @GetMapping("/{courseName}/getProfessors")
    public List<ProfessorDTO> getProfessorsForCourse(@PathVariable String courseName){
        try{
            return vlService.getProfessorsForCourse(courseName);
        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }




    /**
     * Metodo: POST
     * @param courseName: riceve dal path il nome del corso
     * @param homeworkId: riceve dal path l'id dell'elaborata di cui si vuole modificare lo stato
     * @param status: valore che può essere LETTO, CONSEGNATO, RIVISTO
     */
    //@PostMapping("/{courseName}/{homeworkId}")
   /* public void updateStatusHomework(@PathVariable String courseName,@PathVariable Long homeworkId, @RequestParam String status) {
        try{
            vlService.updateStatusHomework(homeworkId, status );
        } catch (HomeworkNotFoundException   e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
*/



    /**
     * Metodo:GET
     * Authority: Docente e Studente
     * @param courseName
     * @param assignmentId
     * @param homeworkId
     * @param versionId
     * @return DTO di PhotoVersionHomework per un dato homework
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/{versionId}/version")
    public PhotoVersionHomeworkDTO getVersionHM(@PathVariable String courseName, @PathVariable Long assignmentId,
                                                @PathVariable Long homeworkId, @PathVariable Long versionId) {
        try{
            return  vlService.getVersionHM(versionId);
        } catch (  PhotoVersionHMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo:GET
     * Authority: Docente e Studente
     * @param courseName
     * @param assignmentId
     * @param homeworkId
     * @param correctionId
     * @return DTO di PhotoCorrection per un dato homework
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/{correctionId}/correction")
    public PhotoCorrectionDTO getCorrectionHM(@PathVariable String courseName, @PathVariable Long assignmentId,
                                                @PathVariable Long homeworkId, @PathVariable Long correctionId) {
        try{
            return  vlService.getCorrectionHM( correctionId);
        } catch ( PhotoCorrectionNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

}

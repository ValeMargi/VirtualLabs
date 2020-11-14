package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.Image;
import it.polito.ai.virtualLabs.entities.PhotoModelVM;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.VLService;
import it.polito.ai.virtualLabs.services.VLServiceProfessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@CrossOrigin
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    VLService vlService;
    @Autowired
    VLServiceProfessor vlServiceProfessor;

    /**
     * Metodo: GET
     * @return: ritorna la lista di DTO dei corsi presenti nel sistema
     */
    @GetMapping({"", "/"})
    public List<CourseDTO> all(){
        return vlService.getAllCourses().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    /**
     * Metodo: GET
     * @param name: nome del corso richiesto
     * @return: ritorna il DTO del corso avente il nome ricevuto come parametro del metodo
     */
    @GetMapping("/{name}")
    public CourseDTO getOne(@PathVariable String name){
        Optional<CourseDTO > courseDTO= vlService.getCourse(name);
        if( !courseDTO.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Corso non presente");
        else
            return  ModelHelper.enrich(courseDTO.get());
    }

    /**
     * Metodo: Post
     * @param courseDTO: DTO del corso contenente i dati del corso e del modello VM
     * @param professorsId: lista degli ID dei professori da rendere gestori del corso
     * @param file: immagine del modello VM
     * @return
     */
    @PostMapping({"", "/"})
    public CourseDTO addCourse(@RequestPart("course") CourseDTO courseDTO,
                               @RequestPart("professors") String[] professorsId,
                               @RequestPart("file") @Valid @NotNull MultipartFile file) {
        //controllo validità del contenuto del file
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        //controllo formato del file
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Formato "+file.getContentType()+" non valido: richiesto jpg/jpeg/png");
        try {
            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            PhotoModelVM photoModelVM = new PhotoModelVM(image);
            if (vlServiceProfessor.addCourse(courseDTO, Arrays.asList(professorsId), photoModelVM)) {
                return ModelHelper.enrich(courseDTO);
            } else
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Esiste già un corso con lo stesso nome");
        }catch (ProfessorNotFoundException | CourseNotFoundException p){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, p.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(ImageSizeException | ModelVMAlreadytPresentException |
                CardinalityNotAccetableException | ResourcesVMNotRespectedException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch(IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Professor
     * @param courseName: nome del corso da modificare
     * @param enabled: flag (true/false) che indica se il Professore deve abilitare/disabilitare il dato corso
     */
    @PostMapping("/{courseName}/enable")
    public void enableCourse(@PathVariable String courseName, @RequestBody String enabled){
        try {
            if(Boolean.parseBoolean(enabled))
                vlServiceProfessor.enableCourse(courseName);
            else
                vlServiceProfessor.disableCourse(courseName);
        }catch(CourseNotFoundException e ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch (CourseAlreadyEnabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Professor
     * @param courseName:nome del corso da rimuovere
     * @return: ritorna l'esito della rimozione del corso indicato
     */
    @DeleteMapping("/{courseName}/remove")
    public boolean removeCourse(@PathVariable String courseName){
        try{
            return vlServiceProfessor.removeCourse(courseName);
        }catch(CourseNotFoundException | ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Profesor
     * @param courseName: nome del Corso da modificare
     * @param courseDTO: DTO del corso da modificare contenente i nuovi parametri
     * @return: ritorna l'esito della modifica del corso indicato
     */
    @PostMapping("/{courseName}/modify")
    public boolean modifyCourse(@PathVariable String courseName, @RequestBody CourseDTO courseDTO){
        try{
            return vlServiceProfessor.modifyCourse(courseDTO);
        }catch(CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch (CardinalityNotAccetableException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Professor
     * @param professorsId: lista degli ID dei professori da rendere gestori del corso
     * @param courseName:  nome del corso al quale aggiungere i professori
     * @return: ritorna la lista di DTO deik professori aggiunti al corso con CourseName indicato
     */
    @PostMapping({"/{courseName}/addProfessors"})
    public List<ProfessorDTO> addProfessorsToCourse(@RequestBody String[] professorsId, @PathVariable String courseName){
        try{
            return vlServiceProfessor.addProfessorsToCourse(courseName, Arrays.asList(professorsId)).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(CourseNotFoundException | ProfessorNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(ProfessorAlreadyPresentInCourseException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Professor
     * @param memberId: ID dello studente da iscrivere al corso
     * @param courseName: nome del corso al quale iscrivere lo studente
     */
    @PostMapping("/{courseName}/enrollOne")
    @ResponseStatus(HttpStatus.CREATED)
    public void enrollOne(@RequestBody String memberId, @PathVariable String courseName){
        try{
            if (!vlServiceProfessor.addStudentToCourse(memberId, courseName))
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
     * Authority: Professor
     * @param courseName: nome del corso al quale iscrivere gli studenti
     * @param file: file in cui sono presenti gli ID degli studenti da iscrivere al corso
     * @return: ritorna una lista di boolean per tenere traccia degli studenti aggiunti con successo
     */
    @PostMapping("/{courseName}/enrollMany")
    public List<StudentDTO> enrollStudents(@PathVariable String courseName, @RequestPart("file") MultipartFile file){
        //controllo sulla validità del contenuto del file
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        //controllo sulla validità del formato del file
        if( !file.getContentType().equals("text/csv") && !file.getContentType().equals("application/vnd.ms-excel"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Formato "+file.getContentType()+" non valido: richiesto text/csv");
        else
            try {
                return vlServiceProfessor.EnrollAllFromCSV(new BufferedReader(new InputStreamReader(file.getInputStream())), courseName);
            }catch (CourseNotFoundException | StudentNotFoundException e){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }catch (CourseDisabledException e){
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }catch(FormatFileNotValidException | IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
    }

    /**
     * Metodo: POST
     * @param membersId: lista degli ID da iscrivere al corso
     * @param courseName: nome del corso al quale iscrivere gli studenti
     * @return: ritorna una lista di boolean per tenere traccia degli studenti aggiunti con successo
     */
    @PostMapping("/{courseName}/enrollAll")
    public List<Boolean> enrollAll(@RequestBody String[] membersId, @PathVariable String courseName){
        try{
             return vlServiceProfessor.enrollAll(Arrays.asList(membersId), courseName);
        }catch (StudentNotFoundException | CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * @param courseName: nome del corso dal quale rimuovere studenti
     * @param studentsId: lista degli ID degli studenti da rimuovere dal corso
     * @return: ritorna la lista dei DTO degli studenti rimossi dal corso
     */
    @PostMapping("/{courseName}/removeStudents")
    public List<StudentDTO> deleteStudentsFromCourse(@PathVariable String courseName, @RequestBody String[] studentsId){
        try{
            return vlServiceProfessor.deleteStudentsFromCourse(Arrays.asList(studentsId), courseName);
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
     * @param courseName: nome del corso del quale si vuole ottenere la lista degli studenti iscritti
     * @return: ritorna la lista dei DTO degli studenti iscritti al corso
     */
    @GetMapping("/{courseName}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable  String courseName){
        try {
            return vlService.getEnrolledStudents(courseName).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * @param courseName: nome del corso dal quale ricavare le informazioni
     * @return: ritorna una lista di mappe composte dalla coppia
     *          {
     *              student:"...",
     *              teamName:"..."
     *          }
     */
    @GetMapping("/{courseName}/enrolledInfo")
    List<Map<String, Object>> getEnrolledStudentsAllInfo(@PathVariable String courseName){
        try{
            return vlServiceProfessor.getEnrolledStudentsAllInfo(courseName);
        }catch(CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * @param courseName: nome del corso dal quale ricavare le informazioni
     * @return: ritorna la lista dei DTO dei professori che gestiscono il corso
     */
    @GetMapping("/{courseName}/getProfessors")
    public List<ProfessorDTO> getProfessorsForCourse(@PathVariable String courseName){
        try{
            return vlService.getProfessorsForCourse(courseName);
        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo:GET
     * @param courseName: nome del corso dal quale ricavare le informazioni
     * @param assignmentId: ID della consegna collegata all'elaborato
     * @param homeworkId: ID dell'elaborato del quale si vuole ricavare l'immagine
     * @param versionId: ID della versione dell'elaborato
     * @return: ritorna il DTO dell'immagine della versione dell'elaborato richiesta
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/{versionId}/version")
    public PhotoVersionHomeworkDTO getVersionHW(@PathVariable String courseName, @PathVariable Long assignmentId,
                                                @PathVariable Long homeworkId, @PathVariable Long versionId) {
        try{
            return  vlService.getVersionHW(versionId);
        } catch (  PhotoVersionHMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo:GET
     * @param courseName: nome del corso dal quale ricavare le informazioni
     * @param assignmentId: ID della consegna collegata all'elaborato
     * @param homeworkId: ID dell'elaborato del quale si vuole ricavare l'immagine
     * @param correctionId: ID della correzione
     * @return: ritorna il DTO dell'immagine della correzione dell'elaborato richiesta
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/{correctionId}/correction")
    public PhotoCorrectionDTO getCorrectionHW(@PathVariable String courseName, @PathVariable Long assignmentId,
                                                @PathVariable Long homeworkId, @PathVariable Long correctionId) {
        try{
            return  vlService.getCorrectionHW( correctionId);
        } catch ( PhotoCorrectionNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * @param courseName: nome del corso dal quale ricavare le informazioni
     * @return: ritorna una mappa per avere il massimo di risorse utilizzate per ogni tipo tra i vari team
     * {
     *     vcpu:"...",
     *     diskSpace:"...",
     *     ram:"...",
     *     running:"...",
     *     total:"..."
     * }
     */
    @GetMapping("/{courseName}/maxResources")
    public Map<String, Object> getMaxResources(@PathVariable String courseName){
        try{
            return vlServiceProfessor.getMaxResources(courseName);
        }catch(ProfessorNotFoundException | CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

}

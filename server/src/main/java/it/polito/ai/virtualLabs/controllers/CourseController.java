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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/API/courses")
public class CourseController {
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
     * Metodo: GET
     * @param courseName: riceve dal path il nome di un Corso
     * @return: ritorna la lista di StudentDTO degli studenti iscritti al dato Corso
     */
    /*GET mapping request to see the list of students enrolled in the course "name"*/
    @GetMapping("/{courseName}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable  String courseName){
        try {
            return vlService.getEnrolledStudents(courseName).stream().map(s -> ModelHelper.enrich(s)).collect(Collectors.toList());
        }catch(CourseNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+courseName+" not present");
        }
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseDTO: parametro acquisito dal corpo della richiesta
     *                 (String name, acronym;  int min, max; boolean enabled;)
     * @return: ritorna il DTO del corso
     */
    @PostMapping({"", "/"})
    public CourseDTO addCourse(@RequestBody CourseDTO courseDTO) {
        try {
            if (vlService.addCourse(courseDTO)) {
                return ModelHelper.enrich(courseDTO);
            } else
                throw new ResponseStatusException(HttpStatus.CONFLICT, courseDTO.getName());
        }catch (ProfessorNotFoundException p){
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor not found");
        }
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param professorDTO: parametro acquisito dal corspo della richiesta (String id, name, firstName, email;)
     * @param courseName:  riceve dal path il nome di un Corso
     * @return: ritorna il DTO del professore aggiunto al corso con CourseName indicato
     * @return: ritorna il DTO del professore aggiaddAssunto al corso con CourseName indicato
     */
    @PostMapping({"/{courseName}/addProfessor"})
    public ProfessorDTO addProfessorToCourse(@RequestBody ProfessorDTO professorDTO, @PathVariable String courseName){
        if(vlService.addProfessorToCourse(courseName, professorDTO)){
            return ModelHelper.enrich(professorDTO);
        }else
            throw new ResponseStatusException(HttpStatus.CONFLICT, professorDTO.getName());
    }


    /**
     * Metodo: POST
     * Authority: Docente
     * @param input: Nel corpo della richiesta vengono passati gli id degli studenti da iscrivere al corso con nome courseName."
     *               Esempio Body: {"id": "s1", "id": "s2"}
     * @param courseName: riceve dal path il nome di un Corso
     */
    @PostMapping("/{courseName}/enrollOne")
    @ResponseStatus(HttpStatus.CREATED)
    public void enrollOne(@RequestBody Map<String, String> input, @PathVariable String courseName){
        if( !input.containsKey("id"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, input.get("id"));
        try {
            if (!vlService.addStudentToCourse(input.get("id"), courseName))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+courseName+" not present");
        }catch (PermissionDeniedException permissionException){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, permissionException.getMessage());
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
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, enabledException.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Studente
     * @param studentId: riceve dal path l'id di uno studente
     * @return: ritorna una lista di DTO dei corsi a cui lo studente con studentId indicato è iscritto
     */
    @GetMapping("/{studentId}")
    public List<CourseDTO>  getCoursesForStudent(@PathVariable String studentId){
            return vlService.getCoursesForStudent(studentId).stream().map(c-> ModelHelper.enrich(c)).collect(Collectors.toList());
    }

    /**
     * Metodo: GET
     * Authority: Docente
     * @param professorId: riceve dal path l'id di un professore
     * @return: ritorna una lista di DTO dei corsi di cui il professore con professorId indicato è titolare
     */
    @GetMapping("/{professorId}")
    public List<CourseDTO>  getCoursesForProfessor(@PathVariable String professorId){
        return vlService.getCoursesForProfessor(professorId).stream().map(c-> ModelHelper.enrich(c)).collect(Collectors.toList());
    }


    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseName:riceve dal path il nome di un Corso da rimuovere
     * @return: ritorna l'esito della rimozione del corso indicato
     */
    @PostMapping("/{courseName}/remove")
    public boolean removeCourse(@PathVariable String courseName){
        try{
            return vlService.removeCourse(courseName);
        }catch(PermissionDeniedException |CourseNotFoundException |ProfessorNotFoundException  e){
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
        }catch(PermissionDeniedException |CourseNotFoundException |CardinalityNotAccetableException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

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
        } catch (StudentNotEnrolledToCourseExcpetion | CourseNotFoundException
                | StudentAlreadyInTeamExcpetion | CardinalityNotAccetableException
                | StudentDuplicateException | PermissionDeniedException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    /*POST mapping request to see the list of students enrolled in a team with id=teamId*/

    /**
     * Metodo: GET
     * @param teamdId: riceve dal path L'ID di un determinato Team
     * @return: ritorna la lista di DTO degli studenti iscritti a team con id pari a teamId
     */
    @GetMapping("/{teamId}/membersTeam")
    public List<StudentDTO> getMembersTeam(@PathVariable Long teamdId) {
        try{
            return vlService.getMembers(teamdId).stream().map(s -> ModelHelper.enrich(s)).collect(Collectors.toList());
        }catch (TeamNotFoundException tnfe){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team " +teamdId.toString() +"not present!");
        }
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseName: riceve dal path il nome del corso
     * @param file: nella richiesta viene inviata l'immagine associata al modello creato dal docente
     * @param input: nella richiesta vengono inviati tutti i parametri associati al nuovo modello di VM creato
     * @return: ritorna il DTO del modello VM appena creato
     */
    @PostMapping("/{courseName}/addModel")
    public CourseDTO addModelVM( @PathVariable String courseName,  @RequestPart("file") @Valid @NotNull MultipartFile file,
                                  @RequestPart("modelVM")  Map<String, Object> input) {
        if (!input.containsKey("modelVMid") || !input.containsKey("maxVcpu") || !input.containsKey("diskSpace")
           || !input.containsKey("ram") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");

        try{
            Date date= new Date(System.currentTimeMillis());

            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setMaxVcpu((int)input.get("maxVcpu"));
            courseDTO.setDiskSpace((int)input.get("dispSpace"));
            courseDTO.setRam((int)input.get("ram"));
            courseDTO.setRunningInstances(0);
            courseDTO.setTotInstances(0);

            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            PhotoModelVM photoModelVM = new PhotoModelVM(image);
           // photoModelVM.setTimestamp((Timestamp) date);
            vlService.addModelVM(courseDTO, courseName, photoModelVM);
            return courseDTO;
        }catch (CourseNotFoundException | ModelVMAlreadytPresent | IOException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    /*ADDvm*/

    /**
     * Metodo: POST
     * Authority: Studente
     * @param courseName:courseName: riceve dal path il nome del corso
     * @param file:nella richiesta viene inviata l'immagine associata alla VM creata dallo studente
     * @param input: nella richiesta vengono inviati tutti i parametri associati alla VM creata
     * @return: ritorna il DTO della VM appena creata
     */

    @PostMapping("/{courseName}/addVM")
    public VMDTO addVM( @PathVariable String courseName,  @RequestPart("file") @Valid @NotNull MultipartFile file,
                                  @RequestPart("VM")  Map<String, Object> input) {
        if (!input.containsKey("VMid") || !input.containsKey("numVcpu") || !input.containsKey("diskSpace")
                || !input.containsKey("ram") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");

        try{
            Date date= new Date(System.currentTimeMillis());
            VMDTO vmdto = new VMDTO();
            vmdto.setDiskSpace((int)input.get("dispSpace"));
            vmdto.setNumVcpu((int)input.get("numVcpu"));
            vmdto.setId(input.get("VMid").toString());
            vmdto.setRam((int)input.get("ram"));
            vmdto.setStatus("off");

            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            PhotoVM photoVM = new PhotoVM(image);
            photoVM.setTimestamp((Timestamp)date);
            vlService.addVM(vmdto, courseName, photoVM);
            return vmdto;
        }catch (CourseNotFoundException | ModelVMAlreadytPresent | IOException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


    /**
     * Metodo: POST
     * Authority: Studente
     * @param courseName: riceve dal path il nome del corso
     * @param VMid: riceve dal path l'id della VM
     * @param input: nel body della richiesta vengono inviati gli id dei membri del team che divenntano owner della VM
     */
    @PostMapping("/{courseName}/{VMid}/addOwner")
    public void addOwner(  @PathVariable String courseName, @PathVariable String VMid,@RequestBody Map<String, Object> input) {
        if (!input.containsKey("id"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, input.get("id").toString());
        try {
            List<String> membersId = (List<String>) input.get("id");
            vlService.addOwner(VMid, courseName, membersId);
        } catch (VMNotFound | CourseNotFoundException | StudentNotFoundException | PermissionDeniedException | ModelVMNotSetted e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Studente
     * @param courseName: riceve dal path il nome del corso
     * @param VMid: riceve dal path l'id della VM da attiavare
     */
    @GetMapping("/{courseName}/{VMid}/activateVM")
    public void activateVM(  @PathVariable String courseName, @PathVariable String VMid) {
     try{
            vlService.activateVM(VMid);
        } catch (VMNotFound  | PermissionDeniedException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Studente
     * @param courseName: riceve dal path il nome del corso
     * @param VMid: riceve dal path l'id della VM da disattiavare
     */
    @GetMapping("/{courseName}/{VMid}/disableVM")
    public void disableVM(  @PathVariable String courseName, @PathVariable String VMid) {
        try{
            vlService.disableVM(VMid);
        } catch (VMNotFound  | PermissionDeniedException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Studente
     * @param courseName: riceve dal path il nome del corso
     * @param VMid: riceve dal path l'id della VM da rimuovere
     */
    @GetMapping("/{courseName}/{VMid}/removeVM")
    public void removeVM(  @PathVariable String courseName, @PathVariable String VMid) {
        try{
            vlService.removeVM(VMid);
        } catch (VMNotFound  | PermissionDeniedException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // addAssignment ->inserimento consegna Prof
    /*
    * Ass: id, release, expiration
    * Im: id, times, name, type*/

    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseName: riceve dal path il nome del corso
     * @param file: nella richiesta viene inviata l'immagine associata alla consegna inserita dal professore per il corso con nome pari a courseName
     * @param input: nella richiesta vengono inviati tutti i parametri associati ad una nuova consegna (assignmentId, expirationDate)
     * @throws IOException
     */
    @PostMapping("/{courseName}/addAssignment")
    public void addAssignment(@PathVariable String courseName, @RequestPart("file") @Valid @NotNull MultipartFile file,
                              @RequestPart("assignment")  Map<String, Object> input ) throws IOException {
        if (!input.containsKey("assignmentId") || !input.containsKey("expirationDate") || !input.containsKey("image"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try {
            AssignmentDTO assignmentDTO = new AssignmentDTO();
            assignmentDTO.setId((Long)input.get("assignmentId"));
            Date date= new Date(System.currentTimeMillis());
            assignmentDTO.setReleaseDate(date);
            assignmentDTO.setExpiration((Date)input.get("expirationDate"));

            assignmentDTO.setName(file.getOriginalFilename());
            assignmentDTO.setType(file.getContentType());
            assignmentDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            assignmentDTO.setTimestamp((Timestamp) date);

           // Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));




            vlService.addAssignment(assignmentDTO, courseName);


        }catch (PermissionDeniedException | CourseNotFoundException | AssignmentAlreadyExist e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    /**
     * Metodo: GET
     * Authority: Docente
     * @param courseName: riceve dal path il nome del corso di cui si vuole elencare le consegne associate
     * @return: ritorna la lista di consegne associate al corso con nome pari a CourseName
     */
    @GetMapping("/{courseName}/assignments")
    public List<AssignmentDTO> allAssignment(@PathVariable String courseName) {
        try{
            return  vlService.allAssignment(courseName);
        } catch (CourseNotFoundException  | ProfessorNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

     /**
     * Metodo: POST
     * Authority: Studente
     * @param courseName: riceve dal path il nome del corso
     * @param assignmentId: riceve dal path l'id della consegna per cui lo studente vuole aggiungere un elaborato
     * @param homeworkId: riceve dal path l'id dell'eleborato a cui lo studente vuole caricare un'immagine
     * @param file: nella richiesta viene inviata l'immagine caricata dallo studente
     * @throws IOException
     */
    @PostMapping("/{courseName}/{assignmentId}/{homeworkId}/uploadHomework")
    public void uploadVersionHomework(@PathVariable String courseName, @PathVariable String assignmentId,
                               @PathVariable String homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file ) throws IOException {
        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
           PhotoVersionHomework photoVersionHomework = new PhotoVersionHomework(image);
           photoVersionHomework.setTimestamp(timestamp);
            vlService.uploadVersionHomework(homeworkId,photoVersionHomework);

        }catch (HomeworkIsPermanent | PermissionDeniedException | HomeworkNotFound e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    /**
     * Metodo: POST
     * @param courseName: riceve dal path il nome del corso
     * @param homeworkId: riceve dal path l'id dell'elaborata di cui si vuole modificare lo stato
     * @param status: valore che può essere LETTO, CONSEGNATO, RIVISTO
     */
    @PostMapping("/{courseName}/{homeworkId}")
    public void updateStatusHomework(@PathVariable String courseName,@PathVariable String homeworkId, @RequestParam String status) {
        try{
            vlService.updateStatusHomework(homeworkId, status );
        } catch (HomeworkNotFound  | PermissionDeniedException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    //uploadCorrection

    /**
     * Metodo: POST
     * Authority: Docente
     * @param courseName: riceve dal path il nome del corso
     * @param assignmentId: riceve dal path l'id della consegna
     * @param homeworkId: riceve dal path l'id dell'alaborato di uno studente che il docente vuole corregere
     * @param file: nella richiesta viene inviata l'immagine della correzione
     * @throws IOException
     */
    @PostMapping("/{courseName}/{assignmentId}/{homeworkId}/uploadCorrection")
    public void uploadCorrection(@PathVariable String courseName, @PathVariable String assignmentId,
                               @PathVariable String homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file,
                                @RequestPart("permanent") @NotNull Boolean permanent) throws IOException {
        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            PhotoCorrection photoCorrection= new PhotoCorrection(image);
            photoCorrection.setTimestamp(timestamp);
            vlService.uploadCorrection(homeworkId,photoCorrection,permanent );

        }catch (HomeworkIsPermanent | PermissionDeniedException | HomeworkNotFound e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }



    /*allHomework  per professore */
    /**
     * Metodo: GET
     * Authority: Docente
     * @param courseName: riceve dal path il nome del corso di cui si vuole elencare gli elaborati per una certa consegna con id pari a assignmentId
     * @param assignmentId: riceve dal path l'id della consegna
     * @return: ritorna la lista di elaborati svolti dagli studenti per la consegna indicata
     */
    @GetMapping("/{courseName}/{assignmentId}/allHomework")
    public List<Homework> allHomework(@PathVariable String courseName, @PathVariable String assignmentId) {
        try{
            return  vlService.allHomework(courseName, assignmentId);
        } catch (CourseNotFoundException  | ProfessorNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Docente
     * @param courseName
     * @param assignmentId
     * @param homeworkId
     * @return : ritorna la lista di versioni di Homerwork per la consegna con assignmentId indicato e per il courso con courseName indicato
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/getVersions")
    public List<PhotoVersionHomework> getVersionsHomework(@PathVariable String courseName, @PathVariable String assignmentId, @PathVariable String homeworkId) {
        try{
            return  vlService.getVersionsHomework(homeworkId);
        } catch ( PermissionDeniedException |  HomeworkNotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Studente
     * @param courseName
     * @param assignmentId
     * @param homeworkId
     * @return ritorna la lista di versioni di Homerwork per la consegna con assignmentId indicato e per il courso con courseName indicato
     */
    @GetMapping("/{courseName}/{assignmentId}/getVersions")
    public List<PhotoVersionHomework> getHomeworkForAssignment(@PathVariable String courseName, @PathVariable Long assignmentId, @PathVariable String homeworkId) {
        try{
            return  vlService.getHomeworkForAssignment(assignmentId);
        } catch ( PermissionDeniedException |  HomeworkNotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Docente
     * @param courseName
     * @param assignmentId
     * @param homeworkId
     * @return : ritorna la lista di correzioni di Homerwork per la consegna con assignmentId indicato e per il courso con courseName indicato
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/getCorrections")
    public List<PhotoCorrection> getCorrectionsHomework(@PathVariable String courseName, @PathVariable String assignmentId, @PathVariable String homeworkId) {
        try{
            return  vlService.getCorrectionsHomework(homeworkId);
        } catch ( PermissionDeniedException |  HomeworkNotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Studente
     * @param courseName
     * @param assignmentId
     * @param homeworkId
     * @return ritorna la lista di correzioni di Homerwork per la consegna con assignmentId indicato e per il courso con courseName indicato
     */
    @GetMapping("/{courseName}/{assignmentId}/getCorrections")
    public List<PhotoCorrection> getCorrectionsForAssignment(@PathVariable String courseName, @PathVariable Long assignmentId, @PathVariable String homeworkId) {
        try{
            return  vlService.getCorrectionsForAssignment(assignmentId);
        } catch ( PermissionDeniedException |  HomeworkNotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseName}/{assignmentId}/getAssignment")
/*Fare GetAssign per studente*/
    public AssignmentDTO getAssignment(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlService.getAssignment(courseName, assignmentId);
        } catch (CourseNotFoundException  | StudentNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }




}

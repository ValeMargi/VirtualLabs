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
    * - ModifyResourcesVM da parte docente
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
     * @param professorId: parametro acquisito dal corspo della richiesta (String idProfessor MATRICOLA es p1;)
     * @param courseName:  riceve dal path il nome di un Corso
     * @return: ritorna il DTO del professore aggiunto al corso con CourseName indicato
     * @return: ritorna il DTO del professore aggiaddAssunto al corso con CourseName indicato
     */
    @PostMapping({"/{courseName}/addProfessor"})
    public ProfessorDTO addProfessorToCourse(@RequestBody String professorId, @PathVariable String courseName){
        try{
            return ModelHelper.enrich(vlService.addProfessorToCourse(courseName, professorId));
        }catch(CourseNotFoundException | ProfessorNotFoundException  | ProfessorAlreadyPresentInCourseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Docente
     * @param input: Nel corpo della richiesta viene passato  id dello studente da iscrivere al corso con nome courseName."
     *               Esempio Body: {"id": "s1"}
     * @param courseName: riceve dal path il nome di un Corso
     */
    @PostMapping("/{courseName}/enrollOne")
    @ResponseStatus(HttpStatus.CREATED)
    public void enrollOne(@RequestBody Map<String, String> input, @PathVariable String courseName){
        if( !input.containsKey("id"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, input.get("id"));
        try{
            if (!vlService.addStudentToCourse(input.get("id"), courseName))
                throw new StudentAlreadyInCourseException();
        }catch (StudentNotFoundException | CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
     * Metodo: POST
     * Authority: Docente
     * @param courseName:riceve dal path il nome di un Corso da rimuovere
     * @return: ritorna l'esito della rimozione del corso indicato
     */
    @DeleteMapping("/{courseName}/remove")
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
     * Authority: Docente
     * @param courseName: riceve dal path il nome del corso
     * @param file: nella richiesta viene inviata l'immagine associata al modello creato dal docente
     * @param input: nella richiesta vengono inviati tutti i parametri associati al nuovo modello di VM creato
     * @return: ritorna il DTO del modello VM appena creato
     */
    @PostMapping("/{courseName}/addModel")
    public CourseDTO addModelVM( @PathVariable String courseName,  @RequestPart("file") @Valid @NotNull MultipartFile file,
                                  @RequestPart("modelVM")  Map<String, Object> input) {
        if (!input.containsKey("maxVcpu") || !input.containsKey("diskSpace")
           || !input.containsKey("ram") ||  !input.containsKey("maxRunning")
            || !input.containsKey("maxTotal") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try{
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setMaxVcpu((int)input.get("maxVcpu"));
            courseDTO.setDiskSpace((int)input.get("diskSpace"));
            courseDTO.setRam((int)input.get("ram"));
            courseDTO.setRunningInstances((int)input.get("maxRunning"));
            courseDTO.setTotInstances((int)input.get("maxTotal"));

            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            PhotoModelVM photoModelVM = new PhotoModelVM(image);
            return vlService.addModelVM(courseDTO, courseName, photoModelVM);
        }catch (CourseNotFoundException | ModelVMAlreadytPresentException | IOException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    /**
     * Metodo per modificare le risorse (no photoModello) associate al modelloVM per il corso con courseName indicato
     * Metodo: POST
     * Authority: Docente
     * @param courseName: riceve dal path il nome del corso
     * @param input: nella richiesta vengono inviati tutti i parametri associati al nuovo modello di VM creato
     * @return: ritorna il DTO del modello VM modificato
     */
    @PostMapping("/{courseName}/update")
    public CourseDTO updateModelVM( @PathVariable String courseName, @RequestPart("modelVM")  Map<String, Object> input) {
        if (!input.containsKey("maxVcpu") || !input.containsKey("diskSpace")
                || !input.containsKey("ram") ||  !input.containsKey("maxRunning")
                || !input.containsKey("maxTotal") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try{
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setMaxVcpu((int)input.get("maxVcpu"));
            courseDTO.setDiskSpace((int)input.get("diskSpace"));
            courseDTO.setRam((int)input.get("ram"));
            courseDTO.setRunningInstances((int)input.get("maxRunning"));
            courseDTO.setTotInstances((int)input.get("maxTotal"));
            return vlService.updateModelVM(courseDTO, courseName);
        }catch(ModelVMNotSettedException
                | ResourcesVMNotRespectedException | CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch ( PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }

    }



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
        if ( !input.containsKey("nameVM") ||  !input.containsKey("numVcpu")
                || !input.containsKey("diskSpace")
                || !input.containsKey("ram") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");

        try{
            //Date date= new Date(System.currentTimeMillis());
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());

            VMDTO vmdto = new VMDTO();
            vmdto.setDiskSpace((int)input.get("diskSpace"));
            vmdto.setNumVcpu((int)input.get("numVcpu"));
            vmdto.setNameVM(input.get("nameVM").toString());
            vmdto.setRam((int)input.get("ram"));
            vmdto.setStatus("off");

            PhotoVMDTO photoVMDTO = new PhotoVMDTO();
            photoVMDTO.setNameFile(file.getOriginalFilename());
            photoVMDTO.setType(file.getContentType());
            photoVMDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            vmdto.setTimestamp( timestamp.toString());

            return  vlService.addVM(vmdto, courseName,photoVMDTO);
        }catch (CourseNotFoundException | ModelVMAlreadytPresentException |
                ResourcesVMNotRespectedException | TeamNotFoundException  |
                VMduplicatedException | IOException  e){
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
    public void addOwner(  @PathVariable String courseName, @PathVariable Long VMid,@RequestBody Map<String, Object> input) {
        if (!input.containsKey("id"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, input.get("id").toString());
        try {
            List<String> membersId = (List<String>) input.get("id");
            vlService.addOwner(VMid, courseName, membersId);
        } catch (VMNotFoundException | CourseNotFoundException | StudentNotFoundException | PermissionDeniedException | ModelVMNotSettedException e) {
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
    public void activateVM(  @PathVariable String courseName, @PathVariable Long VMid) {
     try{
            vlService.activateVM(VMid);
        } catch (VMNotFoundException | PermissionDeniedException  e) {
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
    public void disableVM(  @PathVariable String courseName, @PathVariable Long VMid) {
        try{
            vlService.disableVM(VMid);
        } catch (VMNotFoundException | PermissionDeniedException  e) {
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
    public void removeVM(  @PathVariable String courseName, @PathVariable Long VMid) {
        try{
            vlService.removeVM(VMid);
        } catch (VMNotFoundException | PermissionDeniedException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo per aggiornare le risorse si una VM, può farlo solo lo studente owner della VM,
     * VM deve essere off e i nuovi valori delle risorse devono rispettare i vincoli del team
     * @param courseName
     * @param VMid
     * @param input
     * @return
     */
    @PostMapping("/{courseName}/{VMid}/update")
    public VMDTO updateVMresources( @PathVariable String courseName,  @PathVariable Long VMid,
                        @RequestPart("VM")  Map<String, Object> input) {
        if (  !input.containsKey("numVcpu") || !input.containsKey("diskSpace")   || !input.containsKey("ram") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try{
            VMDTO vmdto = new VMDTO();
            vmdto.setDiskSpace((int)input.get("diskSpace"));
            vmdto.setNumVcpu((int)input.get("numVcpu"));
            vmdto.setRam((int)input.get("ram"));
            return  vlService.updateVMresources(VMid, vmdto);
        }catch (VMnotOffException | PermissionDeniedException  | ResourcesVMNotRespectedException | VMNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    /**
     * Authrority: Docente
     * @param courseName
     * @return: ritrona lista di VM dto con le informazioni
     *          di tutte le VM di un dato corso
     */
    @GetMapping("/VM/{courseName}")
    public List<VMDTO> allVMforCourse(@PathVariable String courseName) {
        try{
            return vlService.allVMforCourse(courseName);
        } catch (PermissionDeniedException| CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    /**
     * Metodo per utilizzare una VM, carico una nuova immagine andando a sovrascrivere
     * quella vecchia (id invariato= e aggiorno timestamp in vm
     * @param courseName
     * @param VMid
     * @param file
     */
    @PostMapping("/{courseName}/{VMid}/use")
    public void useVM( @PathVariable String courseName, @PathVariable Long VMid,
                        @RequestPart("file") @Valid @NotNull MultipartFile file) {

        try{
            //Date date= new Date(System.currentTimeMillis());
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());

            PhotoVMDTO photoVMDTO = new PhotoVMDTO();
            photoVMDTO.setNameFile(file.getOriginalFilename());
            photoVMDTO.setType(file.getContentType());
            photoVMDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            vlService.useVM(VMid, timestamp.toString(), photoVMDTO);
        }catch (  VMnotEnabledException | PermissionDeniedException | VMNotFoundException | IOException  e){
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
        if (!input.containsKey("assignmentName") || !input.containsKey("expirationDate") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try {
            AssignmentDTO assignmentDTO = new AssignmentDTO();
            assignmentDTO.setAssignmentName(input.get("assignmentName").toString());
            //Date date= new Date(System.currentTimeMillis());
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());

            assignmentDTO.setReleaseDate(timestamp.toString());
            assignmentDTO.setExpiration(input.get("expirationDate").toString());

            PhotoAssignmentDTO photoAssignmentDTO = new PhotoAssignmentDTO();
            photoAssignmentDTO.setNameFile(file.getOriginalFilename());
            photoAssignmentDTO.setType(file.getContentType());
            photoAssignmentDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            photoAssignmentDTO.setTimestamp( timestamp.toString());
            vlService.addAssignment(assignmentDTO, photoAssignmentDTO, courseName);
        }catch (PermissionDeniedException | CourseNotFoundException | AssignmentAlreadyExistException e){
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
        } catch (PermissionDeniedException  | ProfessorNotFoundException  e) {
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
                               @PathVariable Long homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file ) throws IOException {
        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            PhotoVersionHomeworkDTO photoVersionHomeworkDTO = new PhotoVersionHomeworkDTO();
            photoVersionHomeworkDTO.setNameFile(file.getOriginalFilename());
            photoVersionHomeworkDTO.setType(file.getContentType());
            photoVersionHomeworkDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            photoVersionHomeworkDTO.setTimestamp(timestamp.toString());
            vlService.uploadVersionHomework(homeworkId,photoVersionHomeworkDTO);

        }catch (HomeworkIsPermanentException | PermissionDeniedException | HomeworkNotFoundException e){
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
    public void updateStatusHomework(@PathVariable String courseName,@PathVariable Long homeworkId, @RequestParam String status) {
        try{
            vlService.updateStatusHomework(homeworkId, status );
        } catch (HomeworkNotFoundException | PermissionDeniedException  e) {
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
     * @param versionHMid: versione homerwork da correggere
     * @param permanent ; se true -> versione definitiva, studente non può più caricare versioni homework, professore assegna voto
     * @param grade:  se permanent è true-> professore carica voto (grade da ricevere a NULL se permanent é false)
     * @throws IOException
     */
    @PostMapping("/{courseName}/{assignmentId}/{homeworkId}/{versionHMid}/uploadCorrection")
    public void uploadCorrection(@PathVariable String courseName, @PathVariable String assignmentId,
                               @PathVariable Long homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file,
                                @RequestPart("permanent") @NotNull Boolean permanent, @PathVariable Long versionHMid,
                                 @RequestPart("grade") String grade) throws IOException {
        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());

            PhotoCorrectionDTO photoCorrectionDTO = new PhotoCorrectionDTO();
            photoCorrectionDTO.setNameFile(file.getOriginalFilename());
            photoCorrectionDTO.setType(file.getContentType());
            photoCorrectionDTO.setPicByte(  vlService.compressZLib(file.getBytes()));
            photoCorrectionDTO.setTimestamp(timestamp.toString());
            vlService.uploadCorrection(homeworkId, versionHMid, photoCorrectionDTO,permanent, grade );

        }catch (HomeworkIsPermanentException | PermissionDeniedException | HomeworkNotFoundException e){
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
    public List<HomeworkDTO> allHomework(@PathVariable String courseName, @PathVariable Long assignmentId) {
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
     * @return : ritorna una lista di Map<String,Object>
     *           (chiave= "id", valore=versionHomeworkId; chiave="timestamp", valore="timestamp")
     *           di versioni di Homerwork per la consegna con assignmentId indicato e per il corso con courseName indicato
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/getVersions")
    public List<Map<String, Object>> getVersionsHMForProfessor(@PathVariable String courseName, @PathVariable String assignmentId, @PathVariable Long homeworkId) {
        try{
            return  vlService.getVersionsHMForProfessor(homeworkId);
        } catch ( PermissionDeniedException | HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

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
    public PhotoVersionHomeworkDTO getVersionHM(@PathVariable String courseName, @PathVariable String assignmentId,
                                                @PathVariable Long homeworkId, @PathVariable Long versionId) {
        try{
            return  vlService.getVersionHM(versionId);
        } catch ( PermissionDeniedException | PhotoVersionHMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    /**
     * Metodo: GET
     * Authority: Studente
     * @param courseName
     * @param assignmentId
     * @return ritorna la lista di versioni di Homerwork per la consegna con assignmentId indicato e per il courso con courseName indicato
     */
    @GetMapping("/{courseName}/{assignmentId}/getVersions")
    public List<Map<String, Object>> getVersionsHMForStudent(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlService.getVersionsHMForStudent(assignmentId);
        } catch ( PermissionDeniedException | HomeworkNotFoundException e) {
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
    public List<Map<String, Object>> getCorrectionsForProfessor(@PathVariable String courseName, @PathVariable String assignmentId, @PathVariable Long homeworkId) {
        try{
            return  vlService.getCorrectionsForProfessor(homeworkId);
        } catch ( PermissionDeniedException | HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
    public PhotoCorrectionDTO getCorrectionHM(@PathVariable String courseName, @PathVariable String assignmentId,
                                                @PathVariable Long homeworkId, @PathVariable Long correctionId) {
        try{
            return  vlService.getCorrectionHM( correctionId);
        } catch ( PermissionDeniedException | PhotoCorrectionNotFoundException e) {
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
    public List<Map<String, Object>> getCorrectionsForStudent(@PathVariable String courseName, @PathVariable Long assignmentId, @PathVariable Long homeworkId) {
        try{
            return  vlService.getCorrectionsForStudent(assignmentId);
        } catch ( PermissionDeniedException | HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }





    /**
     * Metodo: Get
     * Authority: Docente
     * @param courseName
     * @param assignmentId
     * @return informazioni assignment dello studente
     */
    @GetMapping("/{courseName}/{assignmentId}/getAssignment")
    public PhotoAssignmentDTO getAssignment(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlService.getAssignmentProfessor( assignmentId);
        } catch (CourseNotFoundException  | StudentNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }





}

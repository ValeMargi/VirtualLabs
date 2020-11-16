package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.VLService;
import it.polito.ai.virtualLabs.services.VLServiceProfessor;
import it.polito.ai.virtualLabs.services.VLServiceStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    VLService vlService;
    @Autowired
    VLServiceStudent vlServiceStudent;

    /**
     * Metodo: GET
     * @return: ritorna la lista di DTO degli studenti presenti nel sistema
     */
    @GetMapping({"", "/"})
    public List<StudentDTO> all() {
        return vlService.getAllStudents().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    /**
     * Metodo: GET
     * @param id: riceve dal path la matricola di uno studente
     * @return: ritorna una mappa contenente il DTO dello studente avente come matricola il valore ricevuto come parametro del metodo e
     *          il DTO dell'avatar dello studente
     */
    @GetMapping("/{id}")
    public Map<String, Object> getOne(@PathVariable String id) {
        try{
            Map<String, Object> profile=vlService.getStudent(id);
            profile.put("student",ModelHelper.enrich((StudentDTO) profile.get("student")));
            return profile;
        }catch(StudentNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());

        }
    }

    /**
     * Metodo: GET
     * @return: ritorna una mappa contenente il DTO dello studente autenticato e
     *          il DTO dell'avatar dello studente
     */
    @GetMapping("/getProfile")
    public Map<String, Object> getProfile() {
        try{
            return vlService.getProfileStudent();
        }catch(StudentNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param studentId: riceve dal path la matricola di uno studente
     * @return: ritorna una lista di DTO dei corsi a cui lo studente con la matricola indicata nel path è iscritto
     */
    @GetMapping("/{studentId}/courses")
    public List<CourseDTO> getCourses(@PathVariable String studentId) {
        try {
            return  vlServiceStudent.getCoursesForStudent(studentId).stream().map(ModelHelper::enrich).collect(Collectors.toList());
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, studentId);
        }
    }

    /**
     * Authority: Student

     * @param courseName:  riceve dal path il nome di un Corso
     * @return: ritorna la lista di DTO di VM con le informazioni di tutte le VM del team di cui lo studente autenticato è membro
     */
    @GetMapping("/VM/{courseName}")
    public List<VMDTO> allVMforStudent(@PathVariable String courseName) {
        try{
            return vlServiceStudent.allVMforStudent(courseName);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName: riceve dal path il nome di un Corso
     * @param VMid: riceve dal path l'id di una VM
     * @return: ritorna il DTO dell'immagine della VM con id pari a VMid
     */
    @GetMapping("/VM/{courseName}/{VMid}")
    public PhotoVMDTO getVMforStudent(@PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlServiceStudent.getVMforStudent(courseName, VMid);
        } catch (TeamNotFoundException  | VMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * Metodo per verificare se lo studente autenticato è owner della VM con WMid indicata
     * @param courseName:  riceve dal path il nome di un Corso
     * @param VMid:riceve dal path l'id di una VM
     * @return: ritorna true se l'utente autenticato è owner della VM con id pari a VMid ricevuto dal path
     */
    @GetMapping("/VM/{courseName}/{VMid}/owner")
    public boolean isOwner(  @PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlServiceStudent.isOwner( VMid);
        } catch (VMNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName: riceve dal path il nome di un Corso
     * @return: ritorna per ogni consegna associata al corso una mappa contenente il DTO
     *          dell'assignment, il campo "grade" e "status"
     */
    @GetMapping("/{courseName}/assignment")
    public List<Map<String,Object>> allAssignment(@PathVariable String courseName) {
        try{
            return  vlServiceStudent.allAssignmentStudent(courseName);
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(StudentNotEnrolledToCourseException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName: riceve dal path il nome di un Corso
     * @param assignmentId: riceve dal path l'id di una consegna associata al corso CourseName
     * @return: ritorna il DTO dell'immagine della consegna con id uguale ad assignmentId
     */
    @GetMapping("/{courseName}/{assignmentId}/getAssignment")
    public PhotoAssignmentDTO getAssignment(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlServiceStudent.getAssignmentStudent( assignmentId);
        } catch(StudentNotFoundException  | PhotoAssignmentNotFoundException | AssignmentNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: POST
     * @param courseName:courseName: riceve dal path il nome del corso
     * @param input: nella richiesta vengono inviati tutti i parametri associati alla VM creata
     * @return: ritorna il DTO della VM appena creata
     */

    @PostMapping("/{courseName}/addVM")
    public VMDTO addVM( @PathVariable String courseName, @RequestPart("VM") Map<String, Object> input) {
        if ( !input.containsKey("nameVM") ||  !input.containsKey("numVcpu")
                || !input.containsKey("diskSpace")
                || !input.containsKey("ram") )
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Parametri non conformi con la richiesta");

        try{
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());

            VMDTO vmdto = new VMDTO();
            vmdto.setDiskSpace(Integer.parseInt(input.get("diskSpace").toString()));
            vmdto.setNumVcpu(Integer.parseInt(input.get("numVcpu").toString()));
            vmdto.setNameVM(input.get("nameVM").toString());
            vmdto.setRam(Integer.parseInt(input.get("ram").toString()));
            vmdto.setStatus("off");
            return  vlServiceStudent.addVM(vmdto, courseName, timestamp.toString());
        }catch (CourseNotFoundException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch( ModelVMNotSettedException | ResourcesVMNotRespectedException |
                VMduplicatedException | CourseDisabledException | ImageSizeException | InvalidInputVMresources e){
            throw new    ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch(PermissionDeniedException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
        }
    }


    /**
     * Authority: Student
     * Metodo: POST
     * @param courseName: riceve dal path il nome del corso
     * @param VMid: riceve dal path l'id della VM
     * @param input: nel corpo della richiesta vengono inviati gli id dei membri del team che diventano owner della VM
     * @return: ritorna true o false a seconda dell'esito dell'operazione
     */
    @PostMapping("/{courseName}/{VMid}/addOwner")
    public boolean addOwner(  @PathVariable String courseName, @PathVariable Long VMid,@RequestBody String[] input) {
        try {
            List<String> membersId = Arrays.asList(input);
            return  vlServiceStudent.addOwner(VMid, courseName, membersId);
        } catch (VMNotFoundException | CourseNotFoundException | StudentNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
        }catch( CourseDisabledException | StudentAlreadyOwnerException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName:riceve dal path il nome del corso
     * @param teamId: riceve dal path l'id del team
     * @param vmId: riceve dal path l'id della VM
     * @return: ritorna la lista dei DTO degli studenti che sono owner della VM con id pari a vmId
     */
    @GetMapping("/VM/{courseName}/{teamId}/{vmId}")
    public List<StudentDTO> getOwners(@PathVariable String courseName, @PathVariable Long teamId, @PathVariable Long vmId) {
        try{
            return vlServiceStudent.getOwnersForStudent(vmId);
        } catch (StudentNotFoundException | VMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName: riceve dal path il nome del corso
     * @param VMid: riceve dal path l'id della VM da attivare
     * @return: ritorna true o false a seconda dell'esito dell'operazione
     */
    @GetMapping("/{courseName}/{VMid}/activateVM")
    public boolean activateVM(  @PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlServiceStudent.activateVM(VMid);
        } catch (VMNotFoundException   e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
        }catch(ResourcesVMNotRespectedException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName: riceve dal path il nome del corso
     * @param VMid: riceve dal path l'id della VM da disattivare
     * @return: ritorna true o false a seconda dell'esito dell'operazione
     */
    @GetMapping("/{courseName}/{VMid}/disableVM")
    public boolean disableVM(  @PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlServiceStudent.disableVM(VMid);
        } catch (VMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException p) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName: riceve dal path il nome del corso
     * @param VMid: riceve dal path l'id della VM da rimuovere
     * @return: ritorna true o false a seconda dell'esito dell'operazione
     */
    @GetMapping("/{courseName}/{VMid}/removeVM")
    public boolean removeVM(  @PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return  vlServiceStudent.removeVM(VMid);
        } catch (VMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException p) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
        }catch (CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: POST
     * @param courseName: riceve dal path il nome del corso
     * @param VMid:  riceve dal path l'id della VM da utilizzare
     * @param file: riceve l'immagine della nuova VM utilizzata
     */
    @PostMapping("/{courseName}/{VMid}/use")
    public boolean useVM( @PathVariable String courseName, @PathVariable Long VMid,
                       @RequestPart("file") @Valid @NotNull MultipartFile file) {
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Formato "+file.getContentType()+" non valido: richiesto jpg/jpeg/png");
        try{
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            PhotoVMDTO photoVMDTO = new PhotoVMDTO();
            photoVMDTO.setNameFile(file.getOriginalFilename());
            photoVMDTO.setType(file.getContentType());
            photoVMDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            return vlServiceStudent.useVM(VMid, timestamp.toString(), photoVMDTO);
        }catch (  VMNotFoundException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (VMnotEnabledException | ImageSizeException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: POST
     * @param courseName: riceve dal path il nome del corso
     * @param VMid:  riceve dal path l'id della VM da utilizzare
     * @param input: riceve una mappa contente i campi delle risorse aggiornate del DTO della VM con id pari a VMid
     *             OSS: per aggiornare le risorse di una VM, lo studente deve essere owner, la VM deve essere spenta
     *             e i nuovi valori delle risorse devono rispettare i vincoli associati al ModelVM del corso
     * @return: ritorna il DTO della VM modificata
     */
    @PostMapping("/{courseName}/{VMid}/update")
    public VMDTO updateVMresources( @PathVariable String courseName,  @PathVariable Long VMid,
                                    @RequestPart("VM")  Map<String, Object> input) {
        if ( !input.containsKey("nameVM") || !input.containsKey("numVcpu") || !input.containsKey("diskSpace")   || !input.containsKey("ram") )
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Parametri non conformi con la richiesta");
        try{
            VMDTO vmdto = new VMDTO();
            vmdto.setNameVM(input.get("nameVM").toString());
            vmdto.setDiskSpace((int)input.get("diskSpace"));
            vmdto.setNumVcpu((int)input.get("numVcpu"));
            vmdto.setRam((int)input.get("ram"));
            return  vlServiceStudent.updateVMresources(VMid, vmdto);
        }catch (VMNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (VMnotOffException | ResourcesVMNotRespectedException | VMduplicatedException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
    /**
     * Authority: Student
     * Metodo: POST
     * @param courseName: riceve dal path il nome del corso
     * @param assignmentId: riceve dal path l'id della consegna per cui lo studente vuole aggiungere un elaborato
     * @param homeworkId: riceve dal path l'id dell'eleborato a cui lo studente vuole caricare un'immagine
     * @param file: nella richiesta viene inviata l'immagine caricata dallo studente corrispondente al nuovo elaborato
     * @throws IOException
     */
    @PostMapping("/{courseName}/{assignmentId}/{homeworkId}/uploadHomework")
    public PhotoVersionHomeworkDTO uploadVersionHomework(@PathVariable String courseName, @PathVariable Long assignmentId,
                                      @PathVariable Long homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file ) throws IOException {
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Formato "+file.getContentType()+" non valido: richiesto jpg/jpeg/png");

        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            PhotoVersionHomeworkDTO photoVersionHomeworkDTO = new PhotoVersionHomeworkDTO();
            photoVersionHomeworkDTO.setNameFile(file.getOriginalFilename());
            photoVersionHomeworkDTO.setType(file.getContentType());
            photoVersionHomeworkDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            photoVersionHomeworkDTO.setTimestamp(timestamp.toString());
            return vlServiceStudent.uploadVersionHomework(homeworkId,photoVersionHomeworkDTO);
        }catch(ImageSizeException | HomeworkIsPermanentException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch (HomeworkNotFoundException | VMNotFoundException | TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName: riceve dal path il nome del corso di cui si vuole elencare gli elaborati per una certa consegna con id pari a assignmentId
     * @param assignmentId: riceve dal path l'id della consegna
     * @return: ritorna la lista di DTO degli elaborati svolti dagli studenti per la consegna indicata
     */
    @GetMapping("/{courseName}/{assignmentId}/getHomework")
    public HomeworkDTO getHomework(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlServiceStudent.getHomework( assignmentId);
        }catch (HomeworkNotFoundException | AssignmentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }


    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName:  riceve dal path il nome del corso
     * @param assignmentId: riceve dal path l'id di una consegna
     * @return : ritorna una lista di mappe composte nel seguente formato
     *                {
     *                  version id:"...",
     *                  timestamp :"...",
     *                  nameFile : "..."
     *              }
     */
    @GetMapping("/{courseName}/{assignmentId}/getVersions")
    public List<Map<String, Object>> getVersionsHWForStudent(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlServiceStudent.getVersionsHWForStudent(assignmentId);
        } catch (HomeworkNotFoundException | AssignmentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Student
     * Metodo: GET
     * @param courseName:   riceve dal path il nome del corso
     * @param assignmentId: riceve dal path l'id di una consegna
     * @return : ritorna una lista di mappe composte nel seguente formato
     *                {
     *                  correction id:"...",
     *                  timestamp :"...",
     *                  nameFile : "...",
     *                  versionId: "..."
     *              }
     */
    @GetMapping("/{courseName}/{assignmentId}/getCorrections")
    public List<Map<String, Object>> getCorrectionsForStudent(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlServiceStudent.getCorrectionsForStudent(assignmentId);
        } catch (HomeworkNotFoundException |  AssignmentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }



}

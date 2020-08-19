package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.services.VLService;
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

    /**
     * Metodo per verificare se lo studente autenticato è owner della VM con WMid indicata
     * @param courseName
     * @param VMid
     * @return
     */
    @GetMapping("/VM/{courseName}/{VMid}/owner")
    public boolean isOwner(  @PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlService.isOwner( VMid);
        } catch (VMNotFoundException | StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
                        @RequestPart("VM") Map<String, Object> input) {
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File provided is type "+file.getContentType()+" not valid");

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
        }catch (CourseNotFoundException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch( ModelVMAlreadytPresentException | ResourcesVMNotRespectedException | VMduplicatedException | CourseDisabledException e){
            throw new    ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(ImageSizeException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(IOException e){
            throw new    ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }catch(PermissionDeniedException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
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
    public void addOwner(  @PathVariable String courseName, @PathVariable Long VMid,@RequestBody String[] input) {
        try {
            List<String> membersId = Arrays.asList(input);
            vlService.addOwner(VMid, courseName, membersId);
        } catch (VMNotFoundException | CourseNotFoundException | StudentNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
        }catch(ModelVMNotSettedException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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
        } catch (VMNotFoundException   e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException p){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
        }catch(ResourcesVMNotRespectedException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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
        } catch (VMNotFoundException   e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException p) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
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
        } catch (VMNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException p) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, p.getMessage());
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
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File provided is type "+file.getContentType()+" not valid");
        try{
            //Date date= new Date(System.currentTimeMillis());
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());

            PhotoVMDTO photoVMDTO = new PhotoVMDTO();
            photoVMDTO.setNameFile(file.getOriginalFilename());
            photoVMDTO.setType(file.getContentType());
            photoVMDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            vlService.useVM(VMid, timestamp.toString(), photoVMDTO);
        }catch (  VMNotFoundException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (VMnotEnabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(ImageSizeException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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
        }catch (VMNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (VMnotOffException | ResourcesVMNotRespectedException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
    public void uploadVersionHomework(@PathVariable String courseName, @PathVariable Long assignmentId,
                                      @PathVariable Long homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file ) throws IOException {
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File provided is type "+file.getContentType()+" not valid");

        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            PhotoVersionHomeworkDTO photoVersionHomeworkDTO = new PhotoVersionHomeworkDTO();
            photoVersionHomeworkDTO.setNameFile(file.getOriginalFilename());
            photoVersionHomeworkDTO.setType(file.getContentType());
            photoVersionHomeworkDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            photoVersionHomeworkDTO.setTimestamp(timestamp.toString());
            vlService.uploadVersionHomework(homeworkId,photoVersionHomeworkDTO);
        }catch(ImageSizeException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch (HomeworkNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch (HomeworkIsPermanentException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Studente
     * @param courseName: riceve dal path il nome del corso di cui si vuole elencare gli elaborati per una certa consegna con id pari a assignmentId
     * @param assignmentId: riceve dal path l'id della consegna
     * @return: ritorna la lista di elaborati svolti dagli studenti per la consegna indicata
     */
    @GetMapping("/{courseName}/{assignmentId}/getHomework")
    public HomeworkDTO getHomework(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlService.getHomework( assignmentId);
        }catch (  HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
        } catch (  HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
        } catch (  HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }



}

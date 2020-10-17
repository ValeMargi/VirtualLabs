package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.Image;
import it.polito.ai.virtualLabs.entities.PhotoModelVM;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/professors")
public class ProfessorController {

    @Autowired
    VLService vlService;


    @GetMapping({"", "/"})
    public List<ProfessorDTO> getAll(){
        return vlService.getAllProfessors().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public Map<String, Object> getOne(@PathVariable String id) {
        try{
            Map<String, Object> profile=vlService.getProfessor(id);
            profile.put("professor",ModelHelper.enrich((ProfessorDTO) profile.get("professor")));
            return profile;
        }catch(ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());

        }
    }


    @GetMapping("/getProfile")
    public Map<String, Object> getProfile() {
        try{
            return vlService.getProfileProfessor();
        }catch(ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());

        }
    }
    /**
     * Metodo: GET
     * Authority: Docente
     * @param professorId: riceve dal path l'id di un professore
     * @return: ritorna una lista di DTO dei corsi di cui il professore con professorId indicato è titolare
     */
    @GetMapping("/{professorId}/courses")
    public List<CourseDTO> getCoursesForProfessor(@PathVariable String professorId){
        try{
            return vlService.getCoursesForProfessor(professorId).stream()
                    .map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(ProfessorNotFoundException e){
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
                                 @RequestPart("modelVM") Map<String, Object> input) {
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File provided is type "+file.getContentType()+" not valid");

        if (!input.containsKey("maxVcpu") || !input.containsKey("diskSpace")
                || !input.containsKey("ram") ||  !input.containsKey("runningInstances")
                || !input.containsKey("totInstances") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try{
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setMaxVcpu((int)input.get("maxVcpu"));
            courseDTO.setDiskSpace((int)input.get("diskSpace"));
            courseDTO.setRam((int)input.get("ram"));
            courseDTO.setRunningInstances((int)input.get("runningInstances"));
            courseDTO.setTotInstances((int)input.get("totInstances"));

            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            PhotoModelVM photoModelVM = new PhotoModelVM(image);
            return vlService.addModelVM(courseDTO, courseName, photoModelVM);
        }catch (CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(ImageSizeException | ModelVMAlreadytPresentException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch(IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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
                || !input.containsKey("ram") ||  !input.containsKey("runningInstances")
                || !input.containsKey("totInstances") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try{
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setMaxVcpu((int)input.get("maxVcpu"));
            courseDTO.setDiskSpace((int)input.get("diskSpace"));
            courseDTO.setRam((int)input.get("ram"));
            courseDTO.setRunningInstances((int)input.get("runningInstances"));
            courseDTO.setTotInstances((int)input.get("totInstances"));
            return vlService.updateModelVM(courseDTO, courseName);
        }catch(ModelVMNotSettedException | ResourcesVMNotRespectedException  e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch ( PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

        }
    }
    /**
     * Authrority: Professor
     * @param courseName
     * @param VMid
     * @return: ritrona  PhotoVM dto con l'immagine  della VM con id pari a VMid
     */
    @GetMapping("/VM/{courseName}/{VMid}")
    public PhotoVMDTO getVMforProfessor(@PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlService.getVMforProfessor(courseName, VMid);
        } catch (TeamNotFoundException | CourseNotFoundException | VMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch ( PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo GET
     * Authrority: Docente
     * @param courseName, teamId, vmId
     * @return: ritorna la lista di StudentDTO owner di una VM
     */
    @GetMapping("/VM/{courseName}/{teamId}/{vmId}")
    public List<StudentDTO> getOwners(@PathVariable String courseName, @PathVariable Long teamId, @PathVariable Long vmId) {
        try{
            return vlService.getOwnersForProfessor(vmId);
        } catch (ProfessorNotFoundException | VMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
    public boolean addAssignment(@PathVariable String courseName, @RequestPart("file") @Valid @NotNull MultipartFile file,
                              @RequestPart("assignment")  Map<String, Object> input ) throws IOException {
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File provided is type "+file.getContentType()+" not valid");

        if (!input.containsKey("assignmentName") || !input.containsKey("expiration") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try {
            AssignmentDTO assignmentDTO = new AssignmentDTO();
            assignmentDTO.setAssignmentName(input.get("assignmentName").toString());
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());

            assignmentDTO.setReleaseDate(timestamp.toString());
            assignmentDTO.setExpiration(input.get("expiration").toString());

            PhotoAssignmentDTO photoAssignmentDTO = new PhotoAssignmentDTO();
            photoAssignmentDTO.setNameFile(file.getOriginalFilename());
            photoAssignmentDTO.setType(file.getContentType());
            photoAssignmentDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            photoAssignmentDTO.setTimestamp( timestamp.toString());
            return vlService.addAssignment(assignmentDTO, photoAssignmentDTO, courseName);
        }catch ( CourseNotFoundException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(ImageSizeException | AssignmentAlreadyExistException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
        } catch (ProfessorNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
    public List<Map<String, Object>> getVersionsHWForProfessor(@PathVariable String courseName, @PathVariable Long assignmentId, @PathVariable Long homeworkId) {
        try{
            return  vlService.getVersionsHWForProfessor(homeworkId);
        } catch (  HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
    public boolean uploadCorrection(@PathVariable String courseName, @PathVariable Long assignmentId,
                                 @PathVariable Long homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file,
                                 @RequestPart("permanent") @NotNull String permanent, @PathVariable Long versionHMid,
                                 @RequestPart("grade") String grade) throws IOException {
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File provided is type "+file.getContentType()+" not valid");

        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());

            PhotoCorrectionDTO photoCorrectionDTO = new PhotoCorrectionDTO();
            photoCorrectionDTO.setNameFile(file.getOriginalFilename());
            photoCorrectionDTO.setType(file.getContentType());
            photoCorrectionDTO.setPicByte(  vlService.compressZLib(file.getBytes()));
            photoCorrectionDTO.setTimestamp(timestamp.toString());
            return vlService.uploadCorrection(homeworkId, versionHMid, photoCorrectionDTO, Boolean.parseBoolean(permanent), grade);

        }catch ( HomeworkNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(ImageSizeException | HomeworkIsPermanentException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
    public List<Map<String, Object>> getCorrectionsForProfessor(@PathVariable String courseName, @PathVariable Long assignmentId, @PathVariable Long homeworkId) {
        try{
            return  vlService.getCorrectionsForProfessor(homeworkId);
        } catch (  HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}

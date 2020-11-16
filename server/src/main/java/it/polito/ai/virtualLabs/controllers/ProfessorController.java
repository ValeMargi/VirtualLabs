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
    @Autowired
    VLServiceProfessor vlServiceProfessor;

    /**
     * Metodo: GET
     * @return: ritorna la lista dei professori presenti nel sistema
     */
    @GetMapping({"", "/"})
    public List<ProfessorDTO> getAll(){
        return vlService.getAllProfessors().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    /**
     * Metodo: GET
     * @param id: ID del professore da ricavare
     * @return: ritorna una mappa composta dal DTO del professore e dal suo avatar
     *  {
     *     professor:"...",
     *     avatar:"..."
     *  }
     */
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

    /**
     * Metodo: GET
     * @return: ritorna una mappa composta dal DTO del professore autenticato e dal suo avatar
     */
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
     * Authority: Professor
     * @param professorId: ID del professore
     * @return: ritorna una lista di DTO dei corsi di cui il professore è titolare
     */
    @GetMapping("/{professorId}/courses")
    public List<CourseDTO> getCoursesForProfessor(@PathVariable String professorId){
        try{
            return vlServiceProfessor.getCoursesForProfessor(professorId).stream()
                    .map(ModelHelper::enrich).collect(Collectors.toList());
        }catch(ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Professor
     * @param courseName: nome del corso
     * @param input: mappa contenente i parametri del modello VM del corso da modificare
     *             {
     *              maxVcpu:"...",
     *              diskSpace:"...",
     *              ram:"...",
     *              runningInstances:"...",
     *              totInstances:"..."
     *             }
     * @return: ritorna il DTO del corso modificato
     */
    @PostMapping("/{courseName}/update")
    public CourseDTO updateModelVM( @PathVariable String courseName, @RequestPart("modelVM")  Map<String, Object> input) {
        //controllo della presenza di tutti i parametri del modello VM
        if (!input.containsKey("maxVcpu") || !input.containsKey("diskSpace")
                || !input.containsKey("ram") ||  !input.containsKey("runningInstances")
                || !input.containsKey("totInstances") )
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Parametri non conformi con la richiesta");
        try{
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setMaxVcpu((int)input.get("maxVcpu"));
            courseDTO.setDiskSpace((int)input.get("diskSpace"));
            courseDTO.setRam((int)input.get("ram"));
            courseDTO.setRunningInstances((int)input.get("runningInstances"));
            courseDTO.setTotInstances((int)input.get("totInstances"));
            return vlServiceProfessor.updateModelVM(courseDTO, courseName);
        }catch(ModelVMNotSettedException | ResourcesVMNotRespectedException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch ( PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }catch(CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Authority: Professor
     * @param courseName: nome del corso
     * @param VMid: ID della VM della quale si vuole ricavare l'immagine
     * @return: ritorna il DTO con l'immagine  della VM richiesta
     */
    @GetMapping("/VM/{courseName}/{VMid}")
    public PhotoVMDTO getVMforProfessor(@PathVariable String courseName, @PathVariable Long VMid) {
        try{
            return vlServiceProfessor.getVMforProfessor(courseName, VMid);
        } catch (TeamNotFoundException | CourseNotFoundException | VMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (CourseDisabledException | VMnotEnabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch ( PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Professor
     * @param teamId: ID del team del quale si vogliono ricavare le risorse utilizzate
     * @return: ritorna una mappa con questo formato:
     *  "nomeRisorsa": inUso/MassimoUtilizzabili
     *
     *  {
     *      Vcpu: ________
     *      diskSpace: ______
     *      ram: _____
     *      runningInstances: _______
     *      totalInstances: _____
     *  }
     */
    @GetMapping("/team/{teamId}/resources")
    public Map<String, Object> getResourcesVM(@PathVariable Long teamId) {
        try{
            return vlServiceProfessor.getResourcesVM(teamId);
        } catch (TeamNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch ( PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Authority: Professor
     * @param courseName: nome del corso
     * @return: ritorna la lista di DTO delle VM del corso
     */
    @GetMapping("/VM/{courseName}")
    public List<VMDTO> allVMforCourse(@PathVariable String courseName) {
        try{
            return vlServiceProfessor.allVMforCourse(courseName);
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo GET
     * Authority: Professor
     * @param courseName: nome del corso
     * @param teamId: ID del team al quale è collegata la VM
     * @param vmId: ID della VM della quale si vogliono ricavare gli studenti owner
     * @return: ritorna la lista dei DTO degli studenti owner della VM
     */
    @GetMapping("/VM/{courseName}/{teamId}/{vmId}")
    public List<StudentDTO> getOwners(@PathVariable String courseName, @PathVariable Long teamId, @PathVariable Long vmId) {
        try{
            return vlServiceProfessor.getOwnersForProfessor(vmId);
        } catch (ProfessorNotFoundException | VMNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Professor
     * @param courseName: nome del corso
     * @param file: immagine della consegna caricata dal professore
     * @param input: mappa contenente il nome della consegna e la data di scadenza
     *             {
     *                 assignmentName:"...",
     *                 expiration:"..."
     *             }
     * @throws IOException
     */
    @PostMapping("/{courseName}/addAssignment")
    public AssignmentDTO addAssignment(@PathVariable String courseName, @RequestPart("file") @Valid @NotNull MultipartFile file,
                              @RequestPart("assignment")  Map<String, Object> input ) throws IOException {
        //controllo della validità del contenuto del file
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        //controllo della validità del formato del file
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Formato "+file.getContentType()+" non valido: richiesto jpg/jpeg/png");
        //controllo che i campi necessari della mappa siano presenti
        if (!input.containsKey("assignmentName") || !input.containsKey("expiration") )
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Parametri non conformi con la richiesta");
        try {
            AssignmentDTO assignmentDTO = new AssignmentDTO();
            assignmentDTO.setAssignmentName(input.get("assignmentName").toString());
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            assignmentDTO.setReleaseDate(timestamp.toString());
            assignmentDTO.setExpiration(input.get("expiration").toString());
            //controllo che la data di scadenza non sia antecedente alla data di caricamento
            if(assignmentDTO.getExpiration().compareTo(assignmentDTO.getReleaseDate())<=0)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Data di scadenza non valida");
            PhotoAssignmentDTO photoAssignmentDTO = new PhotoAssignmentDTO();
            photoAssignmentDTO.setNameFile(file.getOriginalFilename());
            photoAssignmentDTO.setType(file.getContentType());
            photoAssignmentDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            photoAssignmentDTO.setTimestamp( timestamp.toString());
            return vlServiceProfessor.addAssignment(assignmentDTO, photoAssignmentDTO, courseName);
        }catch ( CourseNotFoundException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(ImageSizeException | AssignmentAlreadyExistException | CourseDisabledException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Professor
     * @param courseName: nome del corso
     * @return: ritorna la lista di DTO delle consegne associate al corso
     */
    @GetMapping("/{courseName}/assignments")
    public List<AssignmentDTO> allAssignment(@PathVariable String courseName) {
        try{
            return vlServiceProfessor.allAssignment(courseName);
        } catch (ProfessorNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: Get
     * Authority: Professor
     * @param courseName: nome del corso
     * @param assignmentId: ID della consegna della quale si vogliono ricavare le informazioni
     * @return: ritorna il DTO della consegna richiesta
     */
    @GetMapping("/{courseName}/{assignmentId}/getAssignmentDTO")
    public AssignmentDTO getAssignmentDTO(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlServiceProfessor.getAssignmentDTOProfessor( assignmentId);
        } catch ( ProfessorNotFoundException | AssignmentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: Get
     * Authority: Professor
     * @param courseName: nome del corso
     * @param assignmentId: ID della correzione richiesta
     * @return: ritorna il DTO dell'immagine della consegna richiesta
     */
    @GetMapping("/{courseName}/{assignmentId}/getAssignment")
    public PhotoAssignmentDTO getAssignment(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlServiceProfessor.getAssignmentProfessor( assignmentId);
        } catch ( ProfessorNotFoundException | AssignmentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Professor
     * @param courseName: nome del corso
     * @param assignmentId: ID della consegna collegata all'elaborato richiesto
     * @return: ritorna una lista di mappe contenenti il DTO dell'elaborato e il DTO dello studente a cui è collegato
     *          {
     *              Homework:"...",
     *              Student:"..."
     *          }
     */
    @GetMapping("/{courseName}/{assignmentId}/allHomework")
    public List<Map<String, Object>> allHomework(@PathVariable String courseName, @PathVariable Long assignmentId) {
        try{
            return  vlServiceProfessor.allHomework(courseName, assignmentId);
        } catch (CourseNotFoundException | StudentNotFoundException | AssignmentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch (PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Professor
     * @param courseName: nome del corso
     * @param assignmentId: ID della consegna collegata all'elaborato richiesto
     * @param homeworkId: ID dell'elaborato collegato alle versioni richieste
     * @return : ritorna una lista di mappe contenenti l'ID della versione, la data di caricamento e il nome dell'immagine collegata
     *          {
     *              id:"...",
     *              timestamp":"...",
     *              nameFile:"..."
     *          }
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/getVersions")
    public List<Map<String, Object>> getVersionsHWForProfessor(@PathVariable String courseName, @PathVariable Long assignmentId, @PathVariable Long homeworkId) {
        try{
            return  vlServiceProfessor.getVersionsHWForProfessor(homeworkId);
        } catch (  HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * Authority: Professor
     * @param courseName: nome del corso
     * @param assignmentId: ID della consegna collegata all'elaborato richiesto
     * @param homeworkId: ID dell'elaborato collegato alla versione richiesta
     * @param file: file contenente l'immagine della correzione caricata dal professore
     * @param versionHMid: ID della versione collegata alla correzione caricata dal professore
     * @param permanent ; se impostato a true indica che la versione deve ritenersi quella definitiva,
     *                  quindi lo studente non può più caricare nuove versioni dell'elaborato
     * @param grade:  se permanent è impostato a true il professore usa questo parametro per caricare un voto
     *            (grade da ricevere a NULL se permanent é false)
     * @throws IOException
     */
    @PostMapping("/{courseName}/{assignmentId}/{homeworkId}/{versionHMid}/uploadCorrection")
    public Map<String, Object> uploadCorrection(@PathVariable String courseName, @PathVariable Long assignmentId,
                                 @PathVariable Long homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file,
                                 @RequestPart("permanent") @NotNull String permanent, @PathVariable Long versionHMid,
                                 @RequestPart("grade") String grade) throws IOException {
        //controllo della validità del contenuto del file
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        //controllo della validità del formato del file
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Formato "+file.getContentType()+" non valido: richiesto jpg/jpeg/png");
        try {
            PhotoCorrectionDTO photoCorrectionDTO = new PhotoCorrectionDTO();
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            photoCorrectionDTO.setTimestamp(timestamp.toString());
            photoCorrectionDTO.setNameFile(file.getOriginalFilename());
            photoCorrectionDTO.setType(file.getContentType());
            photoCorrectionDTO.setPicByte(vlService.compressZLib(file.getBytes()));
            return vlServiceProfessor.uploadCorrection(homeworkId, versionHMid, photoCorrectionDTO, Boolean.parseBoolean(permanent), grade);
        }catch ( HomeworkNotFoundException | HomeworkVersionIdNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(ImageSizeException  | GradeNotValidException | CourseDisabledException | NewVersionHMisPresentException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    /**
     * Metodo: GET
     * Authority: Professor
     * @param courseName: nome del corso
     * @param assignmentId: ID della consegna collegata all'elaborato richiesto
     * @param homeworkId: ID dell'elaborato collegato alla correzione richiesta
     * @return : ritorna una lista di mappe contenenti l'ID della correzione, la data di caricamento della correzione,
     *              il nome dell'immagine della correzione e l'ID della versione dell'elaborato a cui è collegata la correzione
     *              {
     *                  id:"...",
     *                  timestamp:"...",
     *                  nameFile:"...",
     *                  versionID:"..."
     *              }
     */
    @GetMapping("/{courseName}/{assignmentId}/{homeworkId}/getCorrections")
    public List<Map<String, Object>> getCorrectionsForProfessor(@PathVariable String courseName, @PathVariable Long assignmentId, @PathVariable Long homeworkId) {
        try{
            return  vlServiceProfessor.getCorrectionsForProfessor(homeworkId);
        } catch (  HomeworkNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }catch(PermissionDeniedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}

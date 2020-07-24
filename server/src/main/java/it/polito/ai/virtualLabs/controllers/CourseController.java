package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.Assignment;
import it.polito.ai.virtualLabs.entities.Image;
import it.polito.ai.virtualLabs.entities.VM;
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

    @GetMapping("/{name}")
    public CourseDTO getOne(@PathVariable String name){
        Optional<CourseDTO > courseDTO= vlService.getCourse(name);
        if( !courseDTO.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+name+" not present");
        else
            return  ModelHelper.enrich(courseDTO.get());
    }

    /*GET mapping request to see the list of students enrolled in the course "name"*/
    @GetMapping("/{name}/enrolled")
    public List<StudentDTO> enrolledStudents(@PathVariable  String name){
        try {
            return vlService.getEnrolledStudents(name).stream().map(s -> ModelHelper.enrich(s)).collect(Collectors.toList());
        }catch(CourseNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+name+" not present");
        }
    }

    @PostMapping({"", "/"})
    public CourseDTO addCourse(@RequestBody CourseDTO dto){
        if(vlService.addCourse(dto)){
            return ModelHelper.enrich(dto);
        }else
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.getName());
    }

    @PostMapping({"/{name}/addProfessor"})
    public ProfessorDTO addProfessorToCourse(@RequestBody ProfessorDTO professorDTO, @PathVariable String courseName){
        if(vlService.addProfessorToCourse(courseName, professorDTO)){
            return ModelHelper.enrich(professorDTO);
        }else
            throw new ResponseStatusException(HttpStatus.CONFLICT, professorDTO.getName());
    }


    @PostMapping("/{name}/enrollOne")
    @ResponseStatus(HttpStatus.CREATED)
    public void enrollOne(@RequestBody Map<String, String> input, @PathVariable String name){
        if( !input.containsKey("id"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, input.get("id"));
        try {
            if (!vlService.addStudentToCourse(input.get("id"), name))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+name+" not present");
        }catch (PermissionDeniedException permissionException){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, permissionException.getMessage());
        }
    }

    @PostMapping("/{name}/enrollMany")
    public List<Boolean> enrollStudents(@PathVariable String name, @RequestParam("file") MultipartFile file){
        if( !file.getContentType().equals("text/csv") && !file.getContentType().equals("application/vnd.ms-excel"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"File provided is type "+file.getContentType()+" not text/csv");
        else
            try {
                return vlService.addAndEnroll(new BufferedReader(new InputStreamReader(file.getInputStream())), name);
            }catch(FormatFileNotValidException | IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    /*POST mapping request to enable / disable course "name"*/
    @PostMapping("/{name}/enable")
    public void enableCourse(@PathVariable String name, @RequestBody Boolean enabled){
        try {
            if(enabled)
                vlService.enableCourse(name);
            else
                vlService.disableCourse(name);
        }catch(CourseNotFoundException cntfe ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, cntfe.getMessage());
        }catch (PermissionDeniedException permissionException){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, permissionException.getMessage());
        }catch (CourseAlreadyEnabledException enabledException){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, enabledException.getMessage());
        }
    }

    @GetMapping("/{studentId}")
    public List<CourseDTO>  getCoursesForStudent(@PathVariable String studentId){
            return vlService.getCoursesForStudent(studentId).stream().map(c-> ModelHelper.enrich(c)).collect(Collectors.toList());
    }

    @GetMapping("/{professorId}")
    public List<CourseDTO>  getCoursesForProfessor(@PathVariable String professorId){
        return vlService.getCoursesForProfessor(professorId).stream().map(c-> ModelHelper.enrich(c)).collect(Collectors.toList());
    }


    @PostMapping("/{courseId}/remove")
    public boolean removeCourse(@PathVariable String courseId){
        try{
            return vlService.removeCourse(courseId);
        }catch(PermissionDeniedException |CourseNotFoundException |ProfessorNotFoundException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/modify")
    public boolean modifyCourse(@PathVariable String courseId, @RequestBody CourseDTO dto){
        try{
            return vlService.modifyCourse(dto);
        }catch(PermissionDeniedException |CourseNotFoundException |CardinalityNotAccetableException  e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{courseId}/proposeTeam")
    public TeamDTO proposeTeam(@PathVariable String courseId, @RequestBody Map<String, Object> object) {
        try {
            String nameTeam = object.get("nameTeam").toString();
            List<String> membersId= (List<String>)object.get("membersId");
            return vlService.proposeTeam(courseId, nameTeam, membersId);
        } catch (StudentNotEnrolledToCourseExcpetion | CourseNotFoundException
                | StudentAlreadyInTeamExcpetion | CardinalityNotAccetableException
                | StudentDuplicateException | PermissionDeniedException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    /*POST mapping request to see the list of students enrolled in a team with id=teamId*/
    @PostMapping("/membersTeam")
    public List<StudentDTO> getMembersTeam(  @RequestBody Long teamdId) {
        try{
            return vlService.getMembers(teamdId).stream().map(s -> ModelHelper.enrich(s)).collect(Collectors.toList());
        }catch (TeamNotFoundException tnfe){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team " +teamdId.toString() +"not present!");
        }
    }

    @PostMapping("/{courseId}/addModel")
    public ModelVMDTO addModelVM( @PathVariable String courseId,  @RequestPart("file") @Valid @NotNull MultipartFile file,
                                  @RequestPart("modelVM")  Map<String, Object> input) {
        if (!input.containsKey("modelVMid") || !input.containsKey("maxVcpu") || !input.containsKey("diskSpace")
           || !input.containsKey("ram") )
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");

        try{
            Date date= new Date(System.currentTimeMillis());
            ModelVMDTO modelVMDTO = new ModelVMDTO();
            modelVMDTO.setDiskSpace((int)input.get("dispSpace"));
            modelVMDTO.setMaxVcpu((int)input.get("maxVcpu"));
            modelVMDTO.setId(input.get("modelVMid").toString());
            modelVMDTO.setRam((int)input.get("ram"));
            modelVMDTO.setRunningInstances(0);
            modelVMDTO.setTotInstances(0);

            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            image.setTimestamp((Timestamp) date);
            vlService.addModelVM(modelVMDTO, courseId, image);
            return modelVMDTO;
        }catch (CourseNotFoundException | ModelVMAlreadytPresent | IOException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    /*ADDvm*/
    @PostMapping("/{courseId}/addVM")
    public VMDTO addVM( @PathVariable String courseId,  @RequestPart("file") @Valid @NotNull MultipartFile file,
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
            image.setTimestamp((Timestamp) date);
            vlService.addVM(vmdto, courseId, image);
            return vmdto;
        }catch (CourseNotFoundException | ModelVMAlreadytPresent | IOException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


    @PostMapping("/{courseId}/{VMid}/addOwner")
    public void addOwner(  @PathVariable String courseId, @PathVariable String VMid,@RequestBody Map<String, Object> input) {
        if (!input.containsKey("id"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, input.get("id").toString());
        try {
            List<String> membersId = (List<String>) input.get("id");
            vlService.addOwner(VMid, courseId, membersId);
        } catch (VMNotFound | CourseNotFoundException | StudentNotFoundException | PermissionDeniedException | ModelVMNotSetted e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/{VMid}/activateVM")
    public void activateVM(  @PathVariable String courseId, @PathVariable String VMid) {
     try{
            vlService.activateVM(VMid);
        } catch (VMNotFound  | PermissionDeniedException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/{VMid}/activateVM")
    public void disableVM(  @PathVariable String courseId, @PathVariable String VMid) {
        try{
            vlService.disableVM(VMid);
        } catch (VMNotFound  | PermissionDeniedException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{courseId}/{VMid}/removeVM")
    public void removeVM(  @PathVariable String courseId, @PathVariable String VMid) {
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
    @PostMapping("/{courseId}/addAssignment")
    public void addAssignment(@PathVariable String courseId, @RequestPart("file") @Valid @NotNull MultipartFile file,
                              @RequestPart("assignment")  Map<String, Object> input ) throws IOException {
        if (!input.containsKey("assignmentId") || !input.containsKey("expirationDate") || !input.containsKey("image"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try {
            AssignmentDTO assignmentDTO = new AssignmentDTO();
            assignmentDTO.setId(input.get("assignmentId").toString());
            Date date= new Date(System.currentTimeMillis());
            assignmentDTO.setRelease(date);
            assignmentDTO.setExpiration((Date)input.get("expirationDate"));

            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            image.setTimestamp((Timestamp) date);
            vlService.addAssignment(assignmentDTO,image, courseId);


        }catch (PermissionDeniedException | CourseNotFoundException | AssignmentAlreadyExist e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @GetMapping("/{courseId}/assignment")
    public List<Assignment> allAssignment(@PathVariable String courseId) {
        try{
            return  vlService.allAssignment(courseId);
        } catch (CourseNotFoundException  | ProfessorNotFoundException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    //addHomework  DOVREBBE NON SERVIRE
  /*  @PostMapping("/{courseId}/{assignmentId}/addHomework")
    public void addHomework(@PathVariable String courseId, @PathVariable String assignmentId, @RequestParam Map<String, Object> input ) throws IOException {
        if (!input.containsKey("homeworkId") || !input.containsKey("expirationDate") || !input.containsKey("image"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters not found");
        try {
            HomeworkDTO homeworkDTO = new HomeworkDTO();
            homeworkDTO.setId(input.get("homeworkId").toString());
            homeworkDTO.setStatus("NULL");
            homeworkDTO.setPermanent(false);
            vlService.addHomework(homeworkDTO, courseId, assignmentId);

        }catch (PermissionDeniedException | ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
*/

    //addHomework
    @PostMapping("/{courseId}/{assignmentId}/{homeworkId}/uploadHomework")
    public void uploadHomework(@PathVariable String courseId, @PathVariable String assignmentId,
                               @PathVariable String homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file ) throws IOException {
        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            image.setTimestamp(timestamp);
            vlService.addHomework(homeworkId,image);

        }catch (HomeworkIsPermanent | PermissionDeniedException | HomeworkNotFound e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @PostMapping("/{courseId}/{homeworkId}")
    public void updateStatusHomework(@PathVariable String courseId,@PathVariable String homeworkId, @RequestParam String status) {
        try{
            vlService.updateStatusHomework(homeworkId, status );
        } catch (HomeworkNotFound  | PermissionDeniedException  e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    //uploadCorrection
    @PostMapping("/{courseId}/{assignmentId}/{homeworkId}/uploadHomework")
    public void uploadCorrection(@PathVariable String courseId, @PathVariable String assignmentId,
                               @PathVariable String homeworkId, @RequestPart("file") @Valid @NotNull MultipartFile file ) throws IOException {
        try {
            Timestamp timestamp= new Timestamp(System.currentTimeMillis());
            Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            image.setTimestamp(timestamp);
            vlService.addHomework(homeworkId,image);

        }catch (HomeworkIsPermanent | PermissionDeniedException | HomeworkNotFound e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    /*allHomework  per professore e GetHomework per stude*/
}

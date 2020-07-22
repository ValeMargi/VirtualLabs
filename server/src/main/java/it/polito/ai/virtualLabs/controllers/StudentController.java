package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.CourseDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.exceptions.CourseNotFoundException;
import it.polito.ai.virtualLabs.exceptions.PermissionDeniedException;
import it.polito.ai.virtualLabs.exceptions.StudentNotFoundException;
import it.polito.ai.virtualLabs.exceptions.VMNotFound;
import it.polito.ai.virtualLabs.services.VLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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



    /*GET mapping request to see the list of courses enrolled by a student with studentId*/
    @GetMapping("/{studentId}/courses")
    public List<CourseDTO> getCourses(@PathVariable String studentId) {
        try {
            return  vlService.getCoursesForStudent(studentId).stream().map(c-> ModelHelper.enrich(c)).collect(Collectors.toList());

        } catch (StudentNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, studentId);
        }
    }

    /*GET mapping request to see the list of teams a particular student is enrolled in*/
    @GetMapping("/{studentId}/teams")
    public List<TeamDTO> getTeamsForStudent(@PathVariable String studentId) {
        try {
            return vlService.getTeamsForStudent(studentId);
        } catch (StudentNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Student with id:"+studentId+" not present");
        }
    }

    /*GET mapping request to see the list of groups of a given course*/
    @GetMapping("/teams/{courseId}")
    public List<TeamDTO> getTeamsForCourse(@PathVariable String courseId) {
        try {
            return vlService.getTeamForCourse(courseId);

        }catch(CourseNotFoundException cnfe){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+courseId+" not present");
        }
    }

    /* GET mapping request to see the list of students who are part of a team in a given course*/
    @GetMapping("/inTeam/{courseId}")
    public List<StudentDTO> getStudentsInTeams(@PathVariable String courseId) {
        try {
            return vlService.getStudentsInTeams(courseId).stream().map(s-> ModelHelper.enrich(s)).collect(Collectors.toList());
        } catch (CourseNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+courseId+" not present");
        }
    }

    /* GET mapping request to see the list of students who are not yet part of a team in a given course*/
    @GetMapping("/notInTeam/{courseId}")
    public List<StudentDTO> getAvailableStudents(@PathVariable String courseId) {
        try {
            return vlService.getAvailableStudents(courseId).stream().map(s-> ModelHelper.enrich(s)).collect(Collectors.toList());
        } catch (CourseNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course "+courseId+" not present");
        }
    }

    @GetMapping("/VM/{courseId}")
    public void allVMforCourse(  @PathVariable String courseId) {
        try{
            vlService.allVMforCourse(courseId);
        } catch (PermissionDeniedException| CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/VM/{courseId}/{VMid}/")
    public void isOwner(  @PathVariable String courseId, @PathVariable String VMid) {
        try{
            vlService.isOwner( VMid);
        } catch (PermissionDeniedException | VMNotFound| StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

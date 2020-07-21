package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.VM;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface VLService {
    Optional<StudentDTO> getStudent(String studentId);
    List<StudentDTO> getAllStudents();
    List<StudentDTO> getEnrolledStudents(String courseName);
    boolean addStudentToCourse(String studentId, String courseName);
    boolean deleteStudentFromCourse(String studentId, String courseName);
    List<ProfessorDTO> getProfessorsForCourse(String courseName);
    List<Boolean> addAll(List<StudentDTO> student);
    List<Boolean> enrollAll(List<String> studentsIds, String courseName);
    List<Boolean> addAndEnroll(Reader r, String courseName);
    boolean addCourse(CourseDTO course);
    boolean removeCourse(CourseDTO course);
    Optional<CourseDTO> getCourse(String name);
    List<CourseDTO> getAllCourses();
    boolean addProfessorToCourse(CourseDTO course, ProfessorDTO professor);
    void enableCourse(String courseName);
    void disableCourse(String courseName);
    List<CourseDTO> getCourses(String studentId);
    List<TeamDTO> getTeamsForStudent(String studentId);
    List<StudentDTO> getMembers(Long TeamId);
    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds);
    List<TeamDTO> getTeamForCourse(String courseName);
    List<StudentDTO> getStudentsInTeams(String courseName);
    List<StudentDTO>  getAvailableStudents(String courseName);
    void activateTeam(Long id);
    void evictTeam(Long id);
    boolean addModelVM(ModelVMDTO modelVMdto, String courseId);
    boolean addVM(VMDTO vmdto, String courseId);
    boolean addOwner(VMDTO vmdto, String courseId, String studentId);
    boolean activateVM(VMDTO vmdto);
    boolean disableVM(VMDTO vmdto);
    boolean removeVM(VMDTO vmdto);
    List<VM> allVMforStudent(String courseId);
    List<VM> allVMforCourse( String courseId);
    boolean isOwner( String courseId, String VMid);
    boolean addAssignment( AssignmentDTO assignmentDTO, ImageDTO imageDTO, String courseId);
}

package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.Assignment;
import it.polito.ai.virtualLabs.entities.Image;
import it.polito.ai.virtualLabs.entities.Student;
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
    //List<Boolean> addAll(List<StudentDTO> student);
    List<Boolean> enrollAll(List<String> studentsIds, String courseName);
   // List<Boolean> addAndEnroll(Reader r, String courseName);
   List<Boolean> EnrollAllFromCSV(Reader r, String courseName);
    boolean addCourse(CourseDTO course);
    boolean removeCourse(String courseId);
    boolean modifyCourse(CourseDTO course);
    Optional<CourseDTO> getCourse(String name);
    List<CourseDTO> getAllCourses();
    boolean addProfessorToCourse(String courseId, ProfessorDTO professor);
    void enableCourse(String courseName);
    void disableCourse(String courseName);
    List<CourseDTO> getCoursesForStudent(String studentId);
    List<CourseDTO> getCoursesForProfessor(String professorId);
    List<TeamDTO> getTeamsForStudent(String studentId);
    List<StudentDTO> getMembers(Long TeamId);
    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds);
    List<TeamDTO> getTeamForCourse(String courseName);
    List<StudentDTO> getStudentsInTeams(String courseName);
    List<StudentDTO>  getAvailableStudents(String courseName);
    void activateTeam(Long id);
    void evictTeam(Long id);
    boolean addModelVM(ModelVMDTO modelVMdto, String courseI, Image image);
    boolean addVM(VMDTO vmdto, String courseId, Image image);
    boolean addOwner(String VMid, String courseId, List<String> students);
    boolean activateVM(String VMid);
    boolean disableVM(String VMid);
    boolean removeVM(String VMid);
    List<VM> allVMforStudent(String courseId);
    List<VM> allVMforCourse( String courseId);
    boolean isOwner(  String VMid);
    boolean addAssignment(AssignmentDTO assignmentDTO, Image image, String courseId);
    List<Assignment> allAssignment(String courseId);
    boolean addHomework( String homeworkId, Image image);
    boolean updateStatusHomework( String homeworkId, String status);
    boolean uploadHomework( ImageDTO imageDTO,  String homeworkId, String courseId);
    boolean uploadCorrection( ImageDTO imageDTO,  String homeworkId, String courseId, Boolean permanent);
     byte[] compressZLib(byte[] data);
     byte[] decompressZLib(byte[] data);

}

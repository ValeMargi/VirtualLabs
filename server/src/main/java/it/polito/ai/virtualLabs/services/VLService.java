package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;

import java.io.Reader;
import java.util.List;
import java.util.Map;
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
    boolean addModelVM(CourseDTO courseDTO, String courseI, PhotoModelVM photoModelVM);
    boolean addVM(VMDTO vmdto, String courseId, PhotoVMDTO photoVMDTO);
    boolean addOwner(String VMid, String courseId, List<String> students);
    boolean activateVM(String VMid);
    boolean disableVM(String VMid);
    boolean removeVM(String VMid);
    List<VMDTO> allVMforStudent(String courseId);
    List<VMDTO> allVMforCourse( String courseId);
   PhotoVMDTO getVMforStudent( String courseId, String VMid);
    boolean isOwner(  String VMid);
    boolean addAssignment(AssignmentDTO assignmentDTO, PhotoAssignmentDTO photoAssignmentDTO, String courseId);
    PhotoAssignmentDTO getAssignmentStudent( Long assignmentId);
    PhotoAssignmentDTO getAssignmentProfessor(Long assignmentId );
    List<AssignmentDTO> allAssignmentStudent(  String courseId);
    List<AssignmentDTO> allAssignment(String courseId);
    boolean uploadVersionHomework( Long homeworkId, PhotoVersionHomeworkDTO photoVersionHomeworkDTO);
    boolean updateStatusHomework( Long homeworkId, String status);
    boolean uploadCorrection(Long homeworkId, Long versionHMid, PhotoCorrectionDTO photoCorrectionDTO,Boolean permanent);
    List<Homework> allHomework(String courseName, String assignmentId);
    List<Map<String, Object>> getVersionsHMForStudent(Long assignmentId);
    List<Map<String, Object>> getVersionsHMForProfessor(Long homeworkId);
    PhotoVersionHomeworkDTO getVersionHM(Long versionId);
    List<Map<String, Object>> getCorrectionsForProfessor( Long homeworkId);
    List<Map<String, Object>> getCorrectionsForStudent(Long assignmentId);
    PhotoCorrectionDTO getCorrectionHM(Long correctionId);
     byte[] compressZLib(byte[] data);
     byte[] decompressZLib(byte[] data);

}

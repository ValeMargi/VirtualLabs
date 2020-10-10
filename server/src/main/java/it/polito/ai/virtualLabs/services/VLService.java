package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;

import java.io.Reader;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VLService {
    boolean changeAvatar(AvatarProfessorDTO avatarProfessorDTO, AvatarStudentDTO avatarStudentDTO);
    Map<String, Object> getProfileStudent();
    Map<String, Object>  getStudent(String studentId);
    List<StudentDTO> getAllStudents();
    List<ProfessorDTO> getAllProfessors();
    Map<String, Object> getProfessor(String professorId);
    Map<String, Object> getProfileProfessor();
    List<StudentDTO> getEnrolledStudents(String courseName);
    boolean addStudentToCourse(String studentId, String courseName);
    List<StudentDTO> deleteStudentsFromCourse(List<String> studentsIds, String courseName);
    List<ProfessorDTO> getProfessorsForCourse(String courseName);
    //List<Boolean> addAll(List<StudentDTO> student);
    List<Boolean> enrollAll(List<String> studentsIds, String courseName);
   // List<Boolean> addAndEnroll(Reader r, String courseName);
   List<StudentDTO> EnrollAllFromCSV(Reader r, String courseName);
    boolean addCourse(CourseDTO course, List<String> professorsId);
    boolean removeCourse(String courseId);
    boolean modifyCourse(CourseDTO course);
    Optional<CourseDTO> getCourse(String name);
    List<CourseDTO> getAllCourses();
    List<ProfessorDTO> addProfessorsToCourse(String courseId, List<String> professorsId);
    void enableCourse(String courseName);
    void disableCourse(String courseName);
    List<CourseDTO> getCoursesForStudent(String studentId);
    List<CourseDTO> getCoursesForProfessor(String professorId);
    List<TeamDTO> getTeamsForStudent(String studentId);
    TeamDTO getTeamForStudent(String courseId, String studentId);
    List<StudentDTO> getMembers(Long TeamId);
    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds);
    List<Map<String, Object>> getProposals(String courseId);
    List<TeamDTO> getTeamForCourse(String courseName);
    List<StudentDTO> getStudentsInTeams(String courseName);
    List<StudentDTO>  getAvailableStudents(String courseName);
    void activateTeam(Long id);
    void evictTeam(Long id);
    CourseDTO addModelVM(CourseDTO courseDTO, String courseI, PhotoModelVM photoModelVM);
    CourseDTO updateModelVM(CourseDTO courseDTO, String courseName );
    VMDTO addVM(VMDTO vmdto, String courseId, PhotoVMDTO photoVMDTO);
    boolean addOwner(Long VMid, String courseId, List<String> students);
    boolean activateVM(Long VMid);
    boolean disableVM(Long VMid);
    boolean removeVM(Long VMid);
    boolean useVM(Long VMid, String timestamp, PhotoVMDTO photoVMDTO );
    VMDTO updateVMresources(Long VMid,VMDTO vmdto);
    List<VMDTO> allVMforStudent(String courseId);
    List<VMDTO> allVMforCourse( String courseId);
   PhotoVMDTO getVMforStudent( String courseId, Long VMid);
    List<VMDTO> getAllVMTeam(  Long teamId);
    boolean isOwner(  Long VMid);
    List<StudentDTO> getOwnersForProfessor(Long VMid);
    List<StudentDTO> getOwnersForStudent(Long VMid);
    boolean addAssignment(AssignmentDTO assignmentDTO, PhotoAssignmentDTO photoAssignmentDTO, String courseId);
    PhotoAssignmentDTO getAssignmentStudent( Long assignmentId);
    PhotoAssignmentDTO getAssignmentProfessor(Long assignmentId );
    List<AssignmentDTO> allAssignmentStudent(  String courseId);
    List<AssignmentDTO> allAssignment(String courseId);
    boolean uploadVersionHomework( Long homeworkId, PhotoVersionHomeworkDTO photoVersionHomeworkDTO);
    boolean updateStatusHomework( Long homeworkId, String status);
    boolean uploadCorrection(Long homeworkId, Long versionHMid, PhotoCorrectionDTO photoCorrectionDTO,Boolean permanent, String grade);
    List<HomeworkDTO> allHomework(String courseName, Long assignmentId);
    HomeworkDTO getHomework(Long assignmentId);
    List<Map<String, Object>> getVersionsHMForStudent(Long assignmentId);
    List<Map<String, Object>> getVersionsHMForProfessor(Long homeworkId);
    PhotoVersionHomeworkDTO getVersionHM(Long versionId);
    List<Map<String, Object>> getCorrectionsForProfessor( Long homeworkId);
    List<Map<String, Object>> getCorrectionsForStudent(Long assignmentId);
    PhotoCorrectionDTO getCorrectionHM(Long correctionId);
     byte[] compressZLib(byte[] data);
     byte[] decompressZLib(byte[] data);

}

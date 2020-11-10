package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface VLServiceStudent {
    List<CourseDTO> getCoursesForStudent(String studentId);
    List<TeamDTO> getTeamsForStudent(String studentId);
    TeamDTO getTeamForStudent(String courseId, String studentId);
    Map<String,Object> proposeTeam(String courseId, String name, List<String> memberIds, Timestamp timeout);
    List<Map<String, Object>> getProposals(String courseId);
    List<StudentDTO> getStudentsInTeams(String courseName);
    List<StudentDTO>  getAvailableStudents(String courseName);
    VMDTO addVM(VMDTO vmdto, String courseId, String timestamp);
    boolean addOwner(Long VMid, String courseId, List<String> students);
    List<StudentDTO> getOwnersForStudent(Long VMid);
    boolean activateVM(Long VMid);
    boolean disableVM(Long VMid);
    boolean removeVM(Long VMid);
    boolean useVM(Long VMid, String timestamp, PhotoVMDTO photoVMDTO );
    VMDTO updateVMresources(Long VMid,VMDTO vmdto);
    List<VMDTO> allVMforStudent(String courseId);
    PhotoVMDTO getVMforStudent( String courseId, Long VMid);
    boolean isOwner(  Long VMid);
    List<Map<String, Object>> allAssignmentStudent(  String courseId);
    PhotoAssignmentDTO getAssignmentStudent( Long assignmentId);
    PhotoVersionHomeworkDTO uploadVersionHomework( Long homeworkId, PhotoVersionHomeworkDTO photoVersionHomeworkDTO);
    HomeworkDTO getHomework(Long assignmentId);
    List<Map<String, Object>> getVersionsHWForStudent(Long assignmentId);
    List<Map<String, Object>> getCorrectionsForStudent(Long assignmentId);

}

package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.PhotoModelVM;

import java.io.Reader;
import java.util.List;
import java.util.Map;

public interface VLServiceProfessor {
    List<Map<String, Object>> getEnrolledStudentsAllInfo(String courseName);
    boolean addStudentToCourse(String studentId, String courseName);
    List<Boolean> enrollAll(List<String> studentsIds, String courseName);
    List<StudentDTO> deleteStudentsFromCourse(List<String> studentsIds, String courseName);
    List<StudentDTO> EnrollAllFromCSV(Reader r, String courseName);
    boolean addCourse(CourseDTO course, List<String> professorsId, PhotoModelVM photoModelVM);
    List<ProfessorDTO> addProfessorsToCourse(String courseId, List<String> professorsId);
    void enableCourse(String courseName);
    void disableCourse(String courseName);
    boolean removeCourse(String courseId);
    boolean modifyCourse(CourseDTO course);
    List<CourseDTO> getCoursesForProfessor(String professorId);
    CourseDTO updateModelVM(CourseDTO courseDTO, String courseName );
    Map<String, Object> getMaxResources(String courseId);
    List<StudentDTO> getOwnersForProfessor(Long VMid);
    List<VMDTO> allVMforCourse(String courseId);
    PhotoVMDTO getVMforProfessor(String courseId, Long VMid);
    Map<String, Object> getResourcesVM(Long teamId);
    AssignmentDTO addAssignment(AssignmentDTO assignmentDTO, PhotoAssignmentDTO photoAssignmentDTO, String courseId);
    List<AssignmentDTO> allAssignment(String courseId);
    PhotoAssignmentDTO getAssignmentProfessor(Long assignmentId );
    AssignmentDTO getAssignmentDTOProfessor(Long assignmentId );
    List<Map<String, Object>> allHomework(String courseName, Long assignmentId);
    List<Map<String, Object>> getVersionsHWForProfessor(Long homeworkId);
    Map<String, Object> uploadCorrection(Long homeworkId, Long versionHMid, PhotoCorrectionDTO photoCorrectionDTO,Boolean permanent, String grade);
    List<Map<String, Object>> getCorrectionsForProfessor( Long homeworkId);

}

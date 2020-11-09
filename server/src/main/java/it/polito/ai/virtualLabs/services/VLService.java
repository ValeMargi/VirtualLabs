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
    List<ProfessorDTO> getProfessorsForCourse(String courseName);
    Optional<CourseDTO> getCourse(String name);
    List<CourseDTO> getAllCourses();
    List<StudentDTO> getMembers(Long TeamId);
    List<TeamDTO> getTeamForCourse(String courseName);
    void activateTeam(Long id);
    void evictTeam(Long id);
    List<VMDTO> getAllVMTeam(  Long teamId);
   PhotoVersionHomeworkDTO getVersionHW(Long versionId);
    PhotoCorrectionDTO getCorrectionHW(Long correctionId);
     byte[] compressZLib(byte[] data);
     byte[] decompressZLib(byte[] data);
    void assignmentExpiredSetPermanentHW(Assignment a);


    }

package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.ProfessorDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.UserDAO;

import java.util.Optional;

public interface AuthenticationService {
    Optional<UserDTO> addStudent(StudentDTO student);
    Optional<UserDTO> addProfessor(ProfessorDTO professorDTO);
    void createPasswordResetTokenForUser(final UserDAO user, final String token);
    String validatePasswordResetToken(String token);
    Optional<UserDAO> getUserByPasswordResetToken(final String token);
    void changeUserPassword(UserDAO user, String password);
    }

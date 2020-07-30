package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.ProfessorDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.AvatarStudent;
import it.polito.ai.virtualLabs.entities.Image;
import it.polito.ai.virtualLabs.entities.UserDAO;

import java.util.Optional;

public interface AuthenticationService {
    Optional<UserDTO> addStudent(StudentDTO student, String password, Image photoStudent);
    Optional<UserDTO> addProfessor(ProfessorDTO professorDTO, String password, Image photoProfessor);
    void createPasswordResetTokenForUser(final UserDAO user, final String token);
    String validatePasswordResetToken(String token);
    Optional<UserDAO> getUserByPasswordResetToken(final String token);
    void changeUserPassword(UserDAO user, String password);
    void insertAdmin();

    }

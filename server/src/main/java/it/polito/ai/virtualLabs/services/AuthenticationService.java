package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.Image;
import it.polito.ai.virtualLabs.entities.TokenRegistration;
import it.polito.ai.virtualLabs.entities.UserDAO;

import java.util.Optional;

public interface AuthenticationService {
    Optional<UserDTO> addStudent(StudentDTO student, String password, AvatarStudentDTO avatarStudentDTO);
    Optional<UserDTO> addProfessor(ProfessorDTO professor, String password,  AvatarProfessorDTO avatarProfessorDTO);
    void createPasswordResetTokenForUser(final UserDAO user, final String token);
    String validatePasswordResetToken(String token);
    Optional<UserDAO> getUserByPasswordResetToken(final String token);
    void changeUserPassword(UserDAO user, String password);
    void insertAdmin();
    boolean confirmRegistration(String token);
    Optional<TokenRegistration> checkTokenValidity(String token);
    void activateUser(String userId);
    public boolean checkIfValidOldPassword(UserDAO user, final String oldPassword);
    }

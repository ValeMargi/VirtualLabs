package it.polito.ai.virtualalbs.services;

import it.polito.ai.virtualalbs.dtos.ProfessorDTO;
import it.polito.ai.virtualalbs.dtos.StudentDTO;
import it.polito.ai.virtualalbs.dtos.UserDTO;

import java.util.Optional;

public interface AuthenticationService {
    Optional<UserDTO> addStudent(StudentDTO student);
    Optional<UserDTO> addProfessor(ProfessorDTO professorDTO);


    }

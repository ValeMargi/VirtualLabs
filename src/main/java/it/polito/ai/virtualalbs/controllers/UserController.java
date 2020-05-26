package it.polito.ai.virtualalbs.controllers;

import it.polito.ai.virtualalbs.dtos.ProfessorDTO;
import it.polito.ai.virtualalbs.dtos.StudentDTO;
import it.polito.ai.virtualalbs.dtos.UserDTO;
import it.polito.ai.virtualalbs.services.AuthenticationService;
import it.polito.ai.virtualalbs.services.JwtUserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;

    //TO DO image student upload

    public Optional<UserDTO> registerUser(@RequestBody String firstName,  String name, String id, String password, String email){

        if( !email.matches("^[A-z0-9\\.\\+_-]+@polito.it") || !email.matches("^[A-z0-9\\.\\+_-]+@studenti.polito.it")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Email " + email + " not supported");
        }
        else if( !jwtUserDetailsService.checkUsernameInUserRepo(email)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with email"+ email+"  already present");
        }else{

            if( email.matches("^[A-z0-9\\.\\+_-]+@polito.it")){ //Professor
                StudentDTO studentDTO = new StudentDTO(id,firstName, name,email);
                return authenticationService.addStudent(studentDTO);
            }else {
                ProfessorDTO professorDTO = new ProfessorDTO(id, firstName, name, email);
                return authenticationService.addProfessor(professorDTO);
            }
        }

    }


}

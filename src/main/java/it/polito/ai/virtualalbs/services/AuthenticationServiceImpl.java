package it.polito.ai.virtualalbs.services;

import it.polito.ai.virtualalbs.dtos.ProfessorDTO;
import it.polito.ai.virtualalbs.dtos.StudentDTO;
import it.polito.ai.virtualalbs.dtos.UserDTO;
import it.polito.ai.virtualalbs.entities.Professor;
import it.polito.ai.virtualalbs.entities.Student;
import it.polito.ai.virtualalbs.repositories.ProfessorRepository;
import it.polito.ai.virtualalbs.repositories.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.OneToOne;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    NotificationService notificationService;

    @Override
    public Optional<UserDTO> addStudent(StudentDTO student) {
        if (  !studentRepository.findById(student.getId()).isPresent() )  {
            Student s = modelMapper.map( student, Student.class);
            studentRepository.save(s);
            studentRepository.flush();

            UserDTO user = new UserDTO();
            user.setPassword(UUID.randomUUID().toString());
            user.setRole("student");
            user.setEmail(student.getId());
            notificationService.sendMessage(user.getEmail(),
                    "Enrollment to the VirtualLabs app",
                    "You have been subscribed to the application.\n" +
                            "Your data to access are as follows::\n\n" +
                            "Username:  " + user.getEmail() +"\n"+
                            "Password:   " + user.getPassword());
            jwtUserDetailsService.save(user);
            return Optional.ofNullable(user);
        }
        return null;
    }

    @Override
    public Optional<UserDTO> addProfessor(ProfessorDTO professor) {
        if ( !professorRepository.findById(professor.getId()).isPresent() )  {
            Professor p = modelMapper.map( professor, Professor.class);
            professorRepository.save(p);
            professorRepository.flush();

            UserDTO user = new UserDTO();
            user.setPassword(UUID.randomUUID().toString());
            user.setRole("professor");
            user.setEmail(professor.getId());
            notificationService.sendMessage( user.getEmail(),
                    "Enrollment to the VirtualLabs app",
                    "You have been subscribed to the application.\n" +
                            "Your data to access are as follows::\n\n" +
                            "Username:  " + user.getEmail() + "\n"+
                            "Password:   " + user.getPassword());
            jwtUserDetailsService.save(user);
            return Optional.ofNullable(user);
        }
        return null;
    }
}


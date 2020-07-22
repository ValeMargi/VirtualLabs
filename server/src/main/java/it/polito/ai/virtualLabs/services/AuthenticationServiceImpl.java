package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.ProfessorDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.PasswordResetToken;
import it.polito.ai.virtualLabs.entities.Professor;
import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.UserDAO;
import it.polito.ai.virtualLabs.repositories.PasswordResetTokenRepository;
import it.polito.ai.virtualLabs.repositories.ProfessorRepository;
import it.polito.ai.virtualLabs.repositories.StudentRepository;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Calendar;
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

    @Autowired
    PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

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

    /*If the User database is empty, the admin user
    with the "admin" role/authority is inserted*/
    @PostConstruct()
    public void insertAdmin(){
        if(!this.userRepository.existsById("admin")){
            UserDTO admin= new UserDTO();
            admin.setPassword("admin");
            admin.setRole("admin");
            admin.setEmail("admin"); //??
            jwtUserDetailsService.save(admin);
        }
    }

    @Override
    public void createPasswordResetTokenForUser(final UserDAO user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }
    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
    @Override
    public Optional<UserDAO> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changeUserPassword(UserDAO user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }





}

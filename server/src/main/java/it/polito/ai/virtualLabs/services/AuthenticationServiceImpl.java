package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.ProfessorDTO;
import it.polito.ai.virtualLabs.dtos.StudentDTO;
import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.repositories.*;
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

    @Autowired
    ImageRepository imageRepository;

    @Override
    public Optional<UserDTO> addStudent(StudentDTO student, String password,  Image photoStudent) {
        if (  !studentRepository.findById(student.getId()).isPresent() )  {
            Student s = modelMapper.map( student, Student.class);
            s.setPhotoStudent(photoStudent);
            studentRepository.save(s);
            studentRepository.flush();
            UserDTO user = new UserDTO();
            //user.setPassword(UUID.randomUUID().toString());
            user.setPassword(password); //passwordEncoder.encode(password));
            user.setRole("student");
            user.setEmail(student.getId());
            notificationService.sendMessage(user.getEmail(),
                    "Enrollment to the VirtualLabs app",
                    "You have been subscribed to the application.\n" +
                            "Your data to access are as follows::\n\n" +
                            "Username:  " + user.getEmail() +"\n"+
                            "Password:   " + user.getPassword());
            jwtUserDetailsService.save(user);
            imageRepository.saveAndFlush(photoStudent);
            return Optional.ofNullable(user);
        }
        return null;
    }

    @Override
    public Optional<UserDTO> addProfessor(ProfessorDTO professor, String password,  Image photoProfessor) {
        if ( !professorRepository.findById(professor.getId()).isPresent() )  {
            Professor p = modelMapper.map( professor, Professor.class);
            p.setPhotoProfessor(photoProfessor);
            professorRepository.save(p);
            professorRepository.flush();
            UserDTO user = new UserDTO();
           // user.setPassword(UUID.randomUUID().toString());
            user.setPassword(password); //passwordEncoder.encode(password));
            user.setRole("professor");
            user.setEmail(professor.getId());
            notificationService.sendMessage( user.getEmail(),
                    "Enrollment to the VirtualLabs app",
                    "You have been subscribed to the application.\n" +
                            "Your data to access are as follows::\n\n" +
                            "Username:  " + user.getEmail() + "\n"+
                            "Password:   " + user.getPassword());
            jwtUserDetailsService.save(user);
            imageRepository.saveAndFlush(photoProfessor);
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


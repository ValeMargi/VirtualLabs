package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
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
    AvatarStudentRepository avatarStudentRepository;

    @Autowired
    AvatarProfessorRepository avatarProfessorRepository;


   @Override
   public Optional<UserDTO> addStudent(StudentDTO student, String password,  AvatarStudentDTO avatarStudentDTO) {
           if (  !studentRepository.findById(student.getId()).isPresent() )  {
               Student s = modelMapper.map( student, Student.class);
               studentRepository.saveAndFlush(s);
               AvatarStudent avatarStudent = modelMapper.map(avatarStudentDTO,AvatarStudent.class);
               s.setPhotoStudent(avatarStudent);
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
                avatarStudentRepository.save(avatarStudent);
                studentRepository.save(s);
               jwtUserDetailsService.save(user, s, null);
                return Optional.ofNullable(user);
               }
               return null;
           }

    @Override
    public Optional<UserDTO> addProfessor(ProfessorDTO professor, String password,  AvatarProfessorDTO avatarProfessorDTO) {
        if ( !professorRepository.findById(professor.getId()).isPresent() )  {
            Professor p = modelMapper.map( professor, Professor.class);
            professorRepository.saveAndFlush(p);
            AvatarProfessor avatarProfessor = modelMapper.map(avatarProfessorDTO, AvatarProfessor.class);
            p.setPhotoProfessor(avatarProfessor);

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
            avatarProfessorRepository.saveAndFlush(avatarProfessor);
            professorRepository.save(p);
            jwtUserDetailsService.save(user, null, p);

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
            jwtUserDetailsService.save(admin, null, null);
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


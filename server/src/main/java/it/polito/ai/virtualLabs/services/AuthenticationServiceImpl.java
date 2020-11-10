package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.*;
import it.polito.ai.virtualLabs.entities.*;
import it.polito.ai.virtualLabs.entities.TokenRegistration;
import it.polito.ai.virtualLabs.exceptions.UserAlreadyPresentException;
import it.polito.ai.virtualLabs.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    PasswordResetTokenRepository passwordTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AvatarStudentRepository avatarStudentRepository;
    @Autowired
    AvatarProfessorRepository avatarProfessorRepository;
    @Autowired
    TokenRegistrationRepository tokenRegistrationRepository;
    @Autowired
    VLService vlService;

    /**
     * Metodo per aggiungere l'utente STUDENTE al sistema
     * settando inizialmente il flag activate a false
     */
   @Override
   public Optional<UserDTO> addStudent(StudentDTO student, String password,  AvatarStudentDTO avatarStudentDTO) {
       if (  !studentRepository.findById(student.getId()).isPresent() )  {
               Student s = modelMapper.map( student, Student.class);
               studentRepository.saveAndFlush(s);
               AvatarStudent avatarStudent = modelMapper.map(avatarStudentDTO,AvatarStudent.class);
               s.setPhotoStudent(avatarStudent);
               UserDTO user = new UserDTO();
               user.setPassword(password);
               user.setRole("student");
               user.setId(student.getId());
               user.setActivate(false);

               TokenRegistration t = new TokenRegistration();
               t.setId(UUID.randomUUID().toString());
               t.setUserId(user.getId());
               t.setExpiryDate(Timestamp.from(Instant.now().plus(5, ChronoUnit.MINUTES)));
               vlService.sendMessage(user.getId(),
                       "Enrollment to the VirtualLabs app",
                       "You have been subscribed to the application.\n" +
                               "Your data to access are as follows::\n\n" +
                               "Email:  " + user.getId()+"@studenti.polito.it" +"\n"+
                               "Click here to activate the registration:\n\n" +
                               "http://localhost:8080/API/registration/confirm/"+ t.getId()
                       );
               avatarStudentRepository.save(avatarStudent);
               studentRepository.save(s);
               tokenRegistrationRepository.save(t);
               jwtUserDetailsService.save(user, s, null);
                return Optional.ofNullable(user);
               }else throw new UserAlreadyPresentException();

           }

    /**
     * Metodo per aggiungere l'utente PROFESSORE al sistema
     */
    @Override
    public Optional<UserDTO> addProfessor(ProfessorDTO professor, String password,  AvatarProfessorDTO avatarProfessorDTO) {
        if (!professorRepository.findById(professor.getId()).isPresent()) {
            Professor p = modelMapper.map(professor, Professor.class);
            professorRepository.saveAndFlush(p);
            AvatarProfessor avatarProfessor = modelMapper.map(avatarProfessorDTO, AvatarProfessor.class);
            p.setPhotoProfessor(avatarProfessor);

            UserDTO user = new UserDTO();
            user.setPassword(password);
            user.setRole("professor");
            user.setId(professor.getId());
            user.setActivate(false);

            TokenRegistration t = new TokenRegistration();
            t.setId(UUID.randomUUID().toString());
            t.setUserId(user.getId());
            t.setExpiryDate(Timestamp.from(Instant.now().plus(2, ChronoUnit.HOURS)));
            vlService.sendMessage(user.getId(),
                    "Enrollment to the VirtualLabs app",
                    "You have been subscribed to the application.\n" +
                            "Your data to access are as follows::\n\n" +
                            "Email:  " + user.getId() + "@polito.it" + "\n" +
                            "Click here to activate the registration:\n\n" +
                            "http://localhost:8080/API/registration/confirm/" + t.getId()
            );
            avatarProfessorRepository.saveAndFlush(avatarProfessor);
            professorRepository.save(p);
            tokenRegistrationRepository.save(t);
            jwtUserDetailsService.save(user, null, p);
            return Optional.ofNullable(user);
        } else throw new UserAlreadyPresentException();
    }

    /**
     * Metodo per accettare la registrazione di un utente al sistema
     */
    @Override
    public boolean confirmRegistration(String token) {
        Optional<TokenRegistration> t = checkTokenValidity(token);
        if(t.isPresent()){
            activateUser(t.get().getUserId());
            tokenRegistrationRepository.deleteById(token);
                return true;
        }else
            return false;
    }

    /**
     * Metodo per verificare la validità del token associato alla fase di registrazione di un utente al sistema,
     * controllando che sia ancora presente nel database e che non sia scaduto
     */
    @Override
    public Optional<TokenRegistration> checkTokenValidity(String token){
        Optional<TokenRegistration> t= tokenRegistrationRepository.findById(token);
        if(t.isPresent()){
            if( t.get().getExpiryDate().compareTo(Timestamp.from(Instant.now()))<0){
                if( studentRepository.existsById(t.get().getUserId()))
                    studentRepository.deleteById(t.get().getUserId());
                else if(professorRepository.existsById(t.get().getUserId()))
                    professorRepository.deleteById(t.get().getUserId());
                userRepository.deleteById(t.get().getUserId());
                tokenRegistrationRepository.deleteById(token);
                return Optional.empty();
            }
            return t;
        }
        return Optional.empty();
   }

    /**
     * Metodo per aggiornare il flag activate a true dopo aver confermato
     * la registrazione di un utente al sistema tramite link inviato via email
     */
    @Override
    public void activateUser(String userId){
       Optional<UserDAO> user = userRepository.findById(userId);
       if(user.isPresent()){
           user.get().setActivate(true);
       }
    }

    /**
     * Funzioni di libreria per l'implementazione del recupero password
     */
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
    @Override
    public boolean checkIfValidOldPassword(UserDAO user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    /*If the User database is empty, the admin user
    with the "admin" role/authority is inserted*/
    @PostConstruct()
    public void insertAdmin(){
        if(!this.userRepository.existsById("admin")){
            UserDTO admin= new UserDTO();
            admin.setPassword("admin");
            admin.setRole("admin");
            admin.setId("admin");
            jwtUserDetailsService.save(admin, null, null);
        }
    }
}


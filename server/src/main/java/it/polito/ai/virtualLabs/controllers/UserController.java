package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;

import it.polito.ai.virtualLabs.entities.UserDAO;
import it.polito.ai.virtualLabs.dtos.ProfessorDTO;
import it.polito.ai.virtualLabs.exceptions.*;
import it.polito.ai.virtualLabs.repositories.PasswordResetTokenRepository;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import it.polito.ai.virtualLabs.services.AuthenticationService;
import it.polito.ai.virtualLabs.services.JwtUserDetailsService;
import it.polito.ai.virtualLabs.services.NotificationService;
import it.polito.ai.virtualLabs.services.VLService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RequestMapping("/API")
@RestController
public class UserController {

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    VLService vlService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    PasswordResetTokenRepository passwordTokenRepository;

    /**
     *Metodo: POST
     * @param file avatar associato all'utente che si sta registrando
     * @param registerData Mappa contenente i campi:
     *                     {
     *                          "firstName":"...",
     *                          "Name":"...",
     *                          "id":"...",
     *                          "email":"..."
     *                           "password":"..."
     *                     }
     * @return: ritorna il DTO dell'utente appena aggiunto se l'iscrizione è andata a buon fine, altrimenti l'optional vuoto
     * @throws IOException
     */
    @PostMapping("/addUser")
    public Optional<UserDTO> registerUser(@RequestPart("file") @Valid @NotNull MultipartFile file,
                                          @Valid @RequestPart("registerData") Map<String, String> registerData) throws IOException {
        if(file.isEmpty() || file.getContentType()==null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        if( !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")
                && !file.getContentType().equals("image/png"))
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Formato "+file.getContentType()+" non valido: richiesto jpg/jpeg/png");

        if(!registerData.containsKey("firstName") || !registerData.containsKey("name") || !registerData.containsKey("id")
          || !registerData.containsKey("email") || !registerData.containsKey("password")){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Parametri non conformi con la richiesta");
        }
        if (!registerData.get("email").matches("^d[0-9]+@polito.it") && !registerData.get("email").matches("^s[0-9]+@studenti.polito.it")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email " + registerData.get("email") + " non supportata");
        } else if (!jwtUserDetailsService.checkUsernameInUserRepo(registerData.get("email"))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Utente già presente");
        }else try {
             int index = registerData.get("email").indexOf("@");
             String id = registerData.get("email").substring(0, index);
            if(!id.equals(registerData.get("id")))
               throw new ResponseStatusException(HttpStatus.CONFLICT, "Matricola e email non corrispondono");

            if (registerData.get("email").matches("^d[0-9]+@polito.it")) { //Professor
                ProfessorDTO professorDTO = new ProfessorDTO(registerData.get("id"),
                                                             registerData.get("firstName"),
                                                             registerData.get("name"),
                                                             registerData.get("email"));
                AvatarProfessorDTO avatarProfessorDTO = new AvatarProfessorDTO();
                avatarProfessorDTO.setNameFile( file.getOriginalFilename());
                avatarProfessorDTO.setType(file.getContentType());
                avatarProfessorDTO.setPicByte(vlService.compressZLib(file.getBytes()));
                return authenticationService.addProfessor(professorDTO, registerData.get("password"), avatarProfessorDTO);
            } else {
                StudentDTO studentDTO = new StudentDTO(registerData.get("id"),
                                                       registerData.get("firstName"),
                                                       registerData.get("name"),
                                                       registerData.get("email"));
                AvatarStudentDTO avatarStudentDTO = new AvatarStudentDTO();
                avatarStudentDTO.setNameFile( file.getOriginalFilename());
                avatarStudentDTO.setType(file.getContentType());
                avatarStudentDTO.setPicByte(vlService.compressZLib(file.getBytes()));
                return authenticationService.addStudent(studentDTO, registerData.get("password"),avatarStudentDTO);
            }
        }catch(ImageSizeException e){
        throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Metodo: POST
     * @param token: token associato alla registrazione di un utente
     * @return: viene effettuata una redirect alla pagina corrispondente all'esito della conferma di registrazione
     */
    @GetMapping("/registration/confirm/{token}")
    public ResponseEntity<Void> confirmationPage(@PathVariable String token) {
        HttpHeaders headers = new HttpHeaders();

        if (authenticationService.confirmRegistration((token))) {
            headers.setLocation(URI.create("http://localhost:4200/register/confirmation?confirmToken=" + token));
        }
        else {
            headers.setLocation(URI.create("http://localhost:4200/register/confirmation?expToken=" + token));
        }
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * Metodo: POST
     * @param userId: matricola dell'utente che vuole resettare la password
     * @return: ritorna un booleano a true se l'operazione ha avuto successo, false altrimenti
     */
    @PostMapping("/user/resetPassword")
    @ResponseStatus(HttpStatus.OK)
    public boolean resetPassword(HttpServletRequest request, @RequestBody String userId) {
        Optional<UserDAO> ouser = userRepository.findById(userId);
        if (ouser.isPresent()) {
            UserDAO user = ouser.get();
            String token = UUID.randomUUID().toString();
            authenticationService.createPasswordResetTokenForUser(user, token);
            String email = null;
            if(user.getRole().equals("student"))
                email = userId+"@studenti.polito.it";
            else if(user.getRole().equals("professor") )
                email=userId+"@polito.it";
            return vlService.sendMessage(email, "Change password request",
                    " Click here to change password:\n\n"
                    +"http://localhost:8080/API/user/changePassword?token="+token );
        }
        else {
            return false;
        }
    }

    /**
     * Metodo: GET
     * @param token: token collegato alla richiesta di cambio password da parte dell'utente
     * @return: viene effettuata una redirect alla pagina corrispondente all'esito del cambio password
     */
    @GetMapping("/user/changePassword")
    public ResponseEntity<Void> showChangePasswordPage(Locale locale, Model model, @RequestParam("token") String token) {
        String result = authenticationService.validatePasswordResetToken(token);
        HttpHeaders headers = new HttpHeaders();

        if(result == null) {
            headers.setLocation(URI.create("http://localhost:4200/user/password-reset?token=" + token));
        }
        else {
            String message = messageSource.getMessage("auth.message." + result, null, locale);
            headers.setLocation(URI.create("http://localhost:4200/user/password-reset?error=" + message));
        }
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * @param input mappa contenente la coppia
     *             {
     *              "token":"token associato nel metodo showChangePasswordPage ",
     *              "newPassword":"..."
     *              }
     * @return: ritorna un booleano a true se il cambio password ha avuto successo, false altrimenti
     */
    @PostMapping("/user/savePassword")
    @ResponseStatus(HttpStatus.OK)
    public boolean savePassword(final Locale locale, @RequestBody Map<String, String> input) {
        String result = authenticationService.validatePasswordResetToken(input.get("token"));
        if(result != null) {
            return false; //error message
            }
        Optional<UserDAO> user = authenticationService.getUserByPasswordResetToken(input.get("token"));
        if(user.isPresent()) {
            authenticationService.changeUserPassword(user.get(), input.get("newPassword"));
            passwordTokenRepository.deleteByToken(input.get("token"));
            return  true; //success
        } else {
            return false; //error
        }
    }

    /**
     * Metodo per aggiornare password quando l'utente ha già effettuato il log in
     * @param locale
     * @param input : mappa contenente la password precedente alla modifica e la nuova password che l'utente vuole utilizzare
     *             {
     *              "oldPassword": "...",
     *              "newPassword": "..."
     *              }
     * @return: ritorna un booleano a true se l'operazione ha avuto successo, altrimenti viene sollevata un'eccezione
     */
    // change user password -> aggiornamento pass da utente loggato
    @PostMapping("/user/updatePassword")
    @ResponseBody
    public boolean changeUserPassword(final Locale locale, @RequestBody Map<String, String> input) {
        Optional<UserDAO> ouser = userRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());
        if( ouser.isPresent()) {
            UserDAO user = ouser.get();
            if (!authenticationService.checkIfValidOldPassword(user, input.get("oldPassword"))) {
                throw new InvalidOldPasswordException();
            }
            authenticationService.changeUserPassword(user, input.get("newPassword"));
            return true;
        }else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato");
    }


    /**
     * Metodo POST
     * @param file: file con il nuovo avatar
     * @return: ritorna un booleano a true se l'operazione di cambio avatar ha avuto successo, false altrimenti (o viene sollevata un'eccezione)
     * @throws IOException
     */
    @PostMapping("/user/updateAvatar")
    public boolean changeAvatar(@RequestPart("file") @Valid @NotNull MultipartFile file) throws IOException {
       boolean res = false;
       try {
           if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("professor"))) {
               AvatarProfessorDTO avatarProfessorDTO = new AvatarProfessorDTO();
               avatarProfessorDTO.setNameFile(file.getOriginalFilename());
               avatarProfessorDTO.setType(file.getContentType());
               avatarProfessorDTO.setPicByte(vlService.compressZLib(file.getBytes()));
               res = vlService.changeAvatar(avatarProfessorDTO, null);
           } else if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("student"))) {
               AvatarStudentDTO avatarStudentDTO = new AvatarStudentDTO();
               avatarStudentDTO.setNameFile(file.getOriginalFilename());
               avatarStudentDTO.setType(file.getContentType());
               avatarStudentDTO.setPicByte(vlService.compressZLib(file.getBytes()));
               res = vlService.changeAvatar(null, avatarStudentDTO);
           }
           return res;
       }catch (AvatarNotPresentException | StudentNotFoundException | ProfessorNotFoundException e){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       }catch(ImageSizeException e){
           throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
       }
    }


}

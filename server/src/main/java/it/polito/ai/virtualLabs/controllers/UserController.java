package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;

import it.polito.ai.virtualLabs.entities.AvatarStudent;
import it.polito.ai.virtualLabs.entities.UserDAO;
import it.polito.ai.virtualLabs.dtos.ProfessorDTO;
import it.polito.ai.virtualLabs.exceptions.InvalidOldPasswordException;
import it.polito.ai.virtualLabs.repositories.PasswordResetTokenRepository;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import it.polito.ai.virtualLabs.services.AuthenticationService;
import it.polito.ai.virtualLabs.services.JwtUserDetailsService;
import it.polito.ai.virtualLabs.services.NotificationService;
import it.polito.ai.virtualLabs.services.VLService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.Timestamp;
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
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PasswordResetTokenRepository passwordTokenRepository;

    @PostMapping("/addUser")
    public Optional<UserDTO> registerUser(@RequestPart("file") @Valid @NotNull MultipartFile file, @RequestPart Map<String, String> registerData) throws IOException {
        /*Controllare se fare lowerCase*/
        if(!registerData.containsKey("firstName") || !registerData.containsKey("name") || !registerData.containsKey("id")
          || !registerData.containsKey("email") || !registerData.containsKey("password")){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters for login not found");
        }
        if (!registerData.get("email").matches("^d[0-9]+@polito.it") && !registerData.get("email").matches("^s[0-9]+@studenti.polito.it")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email " + registerData.get("email") + " not supported");
        } else if (!jwtUserDetailsService.checkUsernameInUserRepo(registerData.get("email"))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with email" + registerData.get("email") + "  already present");
        } else {
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
        }
    }


    @GetMapping("/registration/confirm/{token}")
    public Boolean confirmationPage(@PathVariable String token) {
        return authenticationService.confirmRegistration((token));

    }

    private SimpleMailMessage constructEmail(String subject, String body, UserDAO user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getId());
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @PostMapping("/user/resetPassword")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) {
        UserDAO user = userRepository.findById(userEmail).get();
        if (user != null) {
            String token = UUID.randomUUID().toString();
            authenticationService.createPasswordResetTokenForUser(user, token);
            String email = null;
            if(user.getRole().equals("student"))
                email = userEmail+"@studenti.polito.it";
            else if(user.getRole().equals("professor") )
                email=userEmail+"@polito.it";
            notificationService.sendMessage(email, "Change password request",
                    " Click here to change password:\n\n"
                    +"http://localhost:8080/API/user/changePassword?token="+token );
            //mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));

        }
    }
    /*private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final UserDAO user) {
        final String url = contextPath + "/user/changePassword?token=" + token;
        final String message = messageSource.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }*/

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token) {
        String result = authenticationService.validatePasswordResetToken(token);
        if(result != null) {
            String message = messageSource.getMessage("auth.message." + result, null, locale);
            return "redirect:/login.html?message=" + message;
        } else {
            model.addAttribute("token", token);
            return "redirect:/updatePassword.html";
        }
    }

    /**
     * metodoche riceve un json con token e newPassword  la nuova password resettata
     * @param locale
     * @param input {"token":"token ricevuto nel metodo precedente ", "newPassword":"passwordNuova"}
     * @return
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
     * Metodo per aggiornare password quando l'utente è già loggato
     * @param locale
     * @param input : riceve oldPassword, newPassword
     * @return
     */
    // change user password -> aggiornamento pass da utente loggato
    @PostMapping("/user/updatePassword")
    @ResponseBody
    public boolean changeUserPassword(final Locale locale, @RequestBody Map<String, String> input) {
        final UserDAO user = userRepository.findById(( SecurityContextHolder.getContext().getAuthentication().getName())).get();
        if( user !=null) {
            if (!authenticationService.checkIfValidOldPassword(user, input.get("oldPassword"))) {
                throw new InvalidOldPasswordException();
            }
            authenticationService.changeUserPassword(user, input.get("newPassword"));
            return true;
        }else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }








    }

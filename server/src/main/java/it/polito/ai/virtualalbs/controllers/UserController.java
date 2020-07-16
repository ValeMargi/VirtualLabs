package it.polito.ai.virtualalbs.controllers;

import it.polito.ai.virtualalbs.dtos.*;
import it.polito.ai.virtualalbs.entities.UserDAO;
import it.polito.ai.virtualalbs.dtos.ProfessorDTO;
import it.polito.ai.virtualalbs.repositories.UserRepository;
import it.polito.ai.virtualalbs.services.AuthenticationService;
import it.polito.ai.virtualalbs.services.JwtUserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messages;


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

    private SimpleMailMessage constructEmail(String subject, String body, UserDAO user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        //email.setFrom(env.getProperty("support.email"));  @Autowired
        //    private Environment env;
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @PostMapping("/user/resetPassword")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) {
        UserDAO user = userRepository.findByEmail(userEmail);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            authenticationService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));

        }
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final UserDAO user) {
        final String url = contextPath + "/user/changePassword?token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token) {
        String result = authenticationService.validatePasswordResetToken(token);
        if(result != null) {
            String message = messages.getMessage("auth.message." + result, null, locale);
            return "redirect:/login.html?message=" + message;
        } else {
            model.addAttribute("token", token);
            return "redirect:/updatePassword.html";
        }
    }

    @PostMapping("/user/savePassword")
    @ResponseStatus(HttpStatus.OK)
    public void savePassword(final Locale locale, @Valid PasswordDTO passwordDto) {

        String result = authenticationService.validatePasswordResetToken(passwordDto.getToken());

        if(result != null) {
           // return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
            //
            // RITORNARE MESSAGGIO DI ERRORE
            }

        Optional<UserDAO> user = authenticationService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user.isPresent()) {
            authenticationService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            //return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
            // RITORNARE MESS SUCC
        } else {
            //return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
            // RITORNARE ERRORE
        }
    }

    // change user password -> aggiornamento pass da utente loggato
   /* @PostMapping("/user/updatePassword")
    @ResponseBody
    public GenericResponse changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = userService.findUserByEmail(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }
*/







    }

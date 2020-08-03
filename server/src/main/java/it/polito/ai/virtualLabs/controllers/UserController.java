package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.dtos.*;

import it.polito.ai.virtualLabs.entities.UserDAO;
import it.polito.ai.virtualLabs.dtos.ProfessorDTO;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import it.polito.ai.virtualLabs.services.AuthenticationService;
import it.polito.ai.virtualLabs.services.JwtUserDetailsService;
import it.polito.ai.virtualLabs.services.VLService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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


    //TO DO image student upload ------->
    @PostMapping("/addUser")
    //public Optional<UserDTO> registerUser(@RequestBody String firstName,  String name, String id, String password, String email){
    public Optional<UserDTO> registerUser(@RequestPart("file") @Valid @NotNull MultipartFile file, @RequestPart Map<String, String> inputLogin) throws IOException {

        if(!inputLogin.containsKey("firstName") || !inputLogin.containsKey("name") || !inputLogin.containsKey("id")
          || !inputLogin.containsKey("email") || !inputLogin.containsKey("password")){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parameters for login not found");
        }
        if (!inputLogin.get("email").matches("^[A-z0-9\\.\\+_-]+@polito.it") || !inputLogin.get("email").matches("^[A-z0-9\\.\\+_-]+@studenti.polito.it")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email " + inputLogin.get("email") + " not supported");
        } else if (!jwtUserDetailsService.checkUsernameInUserRepo(inputLogin.get("email"))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email" + inputLogin.get("email") + "  already present");
        } else {
           // Image image = new Image(file.getOriginalFilename(), file.getContentType(), vlService.compressZLib(file.getBytes()));
            if (inputLogin.get("email").matches("^[A-z0-9\\.\\+_-]+@polito.it")) { //Professor
                ProfessorDTO professorDTO = new ProfessorDTO(inputLogin.get("id"),
                                                             inputLogin.get("firstName"),
                                                             inputLogin.get("name"),
                                                             inputLogin.get("email"),
                                                             file.getOriginalFilename(),
                                                             file.getContentType(),
                                                             vlService.compressZLib(file.getBytes()));
                return authenticationService.addProfessor(professorDTO, inputLogin.get("password"));
            } else {
                StudentDTO studentDTO = new StudentDTO(inputLogin.get("id"),
                                                       inputLogin.get("firstName"),
                                                       inputLogin.get("firstName"),
                                                       inputLogin.get("email"),
                                                       file.getOriginalFilename(),
                                                       file.getContentType(),
                                                       vlService.compressZLib(file.getBytes()));
                return authenticationService.addStudent(studentDTO, inputLogin.get("password"));
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
        final String message = messageSource.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

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

    @PostMapping("/user/savePassword")
    @ResponseStatus(HttpStatus.OK)
    /*FINIREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE --> */
    public void savePassword(final Locale locale, @Valid PasswordDTO passwordDto) {

        String result = authenticationService.validatePasswordResetToken(passwordDto.getToken());

        if(result != null) {
           // return new GenericResponse(messageSource.getMessage("auth.message." + result, null, locale));
            //
            // RITORNARE MESSAGGIO DI ERRORE
            }

        Optional<UserDAO> user = authenticationService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user.isPresent()) {
            authenticationService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            //return new GenericResponse(messageSource.getMessage("message.resetPasswordSuc", null, locale));
            // RITORNARE MESS SUCC
        } else {
            //return new GenericResponse(messageSource.getMessage("auth.message.invalid", null, locale));
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
        return new GenericResponse(messageSource.getMessage("message.updatePasswordSuc", null, locale));
    }
*/







    }

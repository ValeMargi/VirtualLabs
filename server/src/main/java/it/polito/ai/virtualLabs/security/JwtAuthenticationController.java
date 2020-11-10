package it.polito.ai.virtualLabs.security;

import it.polito.ai.virtualLabs.services.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;


    /**
     * Per poter fare il login, l'utente deve aver attivato l'account, cliccando sul link
     * presente nell'email ricevuta all'atto di registrazione (validità email 5min).
     * Questo metodo verifica  se i parametri inseriti in fase di login sono validi e
     * ritorna il token associato all'utente che ha effettuato correttamente il login
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception{
        String usernameAuth = authenticationRequest.getUsername();
        /*Sono valide sono email istituzionali*/
        if( !( usernameAuth.matches("^s[0-9]+@studenti.polito.it")) && !( usernameAuth.matches("^d[0-9]+@polito.it")))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email " + usernameAuth + " non valida");
        int index = usernameAuth.indexOf("@");
        if(index == -1)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email " + usernameAuth + " non valida");
        else{
            String id = usernameAuth.substring(0, index);
            authenticate(id, authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(id);
            final String token = jwtTokenUtil.generateToken(userDetails, jwtUserDetailsService.userDAOfromUserDetails(userDetails));
            return ResponseEntity.ok(new JwtResponse(token));
        }
    }

    /**
     * Funzione di libreria per l'autenticazione di un utente
     */
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.UserDTO;
import it.polito.ai.virtualLabs.entities.Professor;
import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.UserDAO;
import it.polito.ai.virtualLabs.exceptions.UserNotActivateException;
import it.polito.ai.virtualLabs.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;


    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<UserDAO> ouser = userRepository.findById(id);
        if (!ouser.isPresent()) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
        UserDAO user = ouser.get();
        if(!user.getActivate())
            throw new UserNotActivateException();
        List<String> roles=new ArrayList<>();
        roles.add(user.getRole());
        return new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(),
                roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }


    public UserDAO save(UserDTO userDTO, Student s, Professor p){
        UserDAO user= new UserDAO();
        user.setId(userDTO.getId());
        user.setPassword(bcryptEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setActivate(false);
        if(s!=null)
            user.setStudent(s);
        else if(p!=null)
            user.setProfessor(p);
        return userRepository.save(user);
    }

    public  boolean checkUsernameInUserRepo(String username){
        return !userRepository.existsById(username);
    }

    public UserDAO userDAOfromUserDetails(UserDetails userDetails){
        Optional<UserDAO> ouser = userRepository.findById(userDetails.getUsername());
        if( ouser.isPresent())
            return  ouser.get();
        else throw new UsernameNotFoundException(userDetails.getUsername());
    }
}

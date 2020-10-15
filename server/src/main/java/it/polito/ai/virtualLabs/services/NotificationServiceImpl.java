package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.entities.Team;
import it.polito.ai.virtualLabs.entities.Token;
import it.polito.ai.virtualLabs.repositories.StudentRepository;
import it.polito.ai.virtualLabs.repositories.TeamRepository;
import it.polito.ai.virtualLabs.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import it.polito.ai.virtualLabs.exceptions.*;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    VLService VLService;

    @Autowired
    TeamRepository teamRepository;
    @Autowired
    StudentRepository studentRepository;


    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("virtuallabs.ai2020@gmail.com");
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    /**
     * Controllo se tutti gli studenti hanno accettato, elimina token dal repo e attivazione Team con invio email
     * @param token
     * @return
     */
    @Override
    public Integer confirm(String token) {
        Optional<Token> t = checkTokenValidity(token);
        if(t.isPresent()){
            if( tokenRepository.findAllByTeamId(t.get().getTeamId())
                    .stream().filter(te->te.getStatus()).count() == teamRepository.getOne(t.get().getTeamId()).getMembers().size()) {
                tokenRepository.findAllByTeamId(t.get().getTeamId()).forEach(tk-> tokenRepository.delete(tk));
                VLService.activateTeam(t.get().getTeamId());
                return 2;
            }else
                return 1;
        }else
            return 0;
    }

    @Override
    public Integer reject(String token) {
        Optional<Token> t = checkTokenValidity(token);
        if( t.isPresent()){
                tokenRepository.deleteFromTokenByTeamId(t.get().getTeamId()); //TESTare
               // tokenRepository.findAllByTeamId(t.get().getTeamId()).forEach(tk-> tokenRepository.delete(tk));
                VLService.evictTeam(t.get().getTeamId());
                return  1;
        }else
            return 0;
    }
    @Override
    public void notifyTeam(TeamDTO dto, List<String> memberIds, String creatorStudent, String courseId, Timestamp timeout) {
        if(timeout.before(Timestamp.from(Instant.now())))
            throw new TimeoutNotValidException();
        for (int i = 0; i < memberIds.size(); i++) {
            Token t = new Token();
            t.setId(UUID.randomUUID().toString());
            t.setTeamId(dto.getId());
            t.setStatus(false);
            t.setCourseId(courseId);
            t.setStudent(studentRepository.getOne(memberIds.get(i)));
            t.setExpiryDate(timeout);
            //t.setExpiryDate(Timestamp.from(Instant.now().plus(5000, ChronoUnit.MILLIS))); for debug
            //t.setExpiryDate(Timestamp.from(Instant.now().plus(1, ChronoUnit.HOURS))); //MODIFICARE
            tokenRepository.saveAndFlush(t);
            sendMessage(memberIds.get(i)+"@studenti.polito.it",
                    "Join the Team",
                    "You have been added to the Team "+dto.getName()+"\n\n"
                          /*  + "Accept the registration on:\n\n http://localhost:8080/API/notification/confirm/"+ t.getId()+
                            "\n \n" +
                            "or refuse registration at the following link:\n\n http://localhost:8080/API/notification/reject/"+t.getId()*/);
        }
        Token t = new Token();
        t.setId(UUID.randomUUID().toString());
        t.setTeamId(dto.getId());
        t.setStatus(true);
        t.setCourseId(courseId);
        t.setStudent(studentRepository.getOne(creatorStudent));
        t.setExpiryDate(timeout);
        tokenRepository.saveAndFlush(t);
    }
    @Override
    public Optional<Token> checkTokenValidity(String token){
        Optional<Token> t= tokenRepository.findById(token);
        if(t.isPresent()){
            // se è ancora valido
            if( t.get().getExpiryDate().compareTo(Timestamp.from(Instant.now()))>0){
                t.get().setStatus(true);
                return t;
            }else{
                tokenRepository.deleteById(token);
                return null;
            }
        }
        return t;
    }

}

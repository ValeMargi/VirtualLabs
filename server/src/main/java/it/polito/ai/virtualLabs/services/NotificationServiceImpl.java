package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.entities.Token;
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
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    VLService VLService;
    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(address);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Override
    public void notifyTeam(TeamDTO dto, List<String> memberIds) {
        for (int i = 0; i < memberIds.size(); i++) {
            Token t = new Token();
            t.setId(UUID.randomUUID().toString());
            t.setTeamId(dto.getId());
            //t.setExpiryDate(Timestamp.from(Instant.now().plus(5000, ChronoUnit.MILLIS))); for debug
            t.setExpiryDate(Timestamp.from(Instant.now().plus(1, ChronoUnit.HOURS)));
            tokenRepository.saveAndFlush(t);
            sendMessage(memberIds.get(i)+"@studenti.polito.it",
                    "Join the Team",
                    "You have been added to the Team "+dto.getName()+"\n\n" +
                            "Accept the registration on:\n\n http://localhost:8080/API/notification/confirm/"+ t.getId()+
                            "\n \n" +
                            "or refuse registration at the following link:\n\n http://localhost:8080/API/notification/reject/"+t.getId());
        }
    }
}

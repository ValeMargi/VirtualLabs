package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.entities.Token;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface NotificationService {
    boolean sendMessage(String address, String subject, String body);
    void notifyTeam(TeamDTO dto, List<String> memberIds, String creator, String courseId, Timestamp timeout);
    Integer confirm(String token);
    Integer reject(String token);
    Optional<Token> checkTokenValidity(String token);
}

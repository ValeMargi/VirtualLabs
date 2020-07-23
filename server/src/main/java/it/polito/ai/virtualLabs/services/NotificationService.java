package it.polito.ai.virtualLabs.services;

import it.polito.ai.virtualLabs.dtos.TeamDTO;
import it.polito.ai.virtualLabs.entities.Token;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    void sendMessage(String address, String subject, String body);
    void notifyTeam(TeamDTO dto, List<String> memberIds);
    boolean confirm(String token);
    boolean reject(String token);
    Optional<Token> checkTokenValidity(String token);
}

package it.polito.ai.virtualalbs.services;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    void sendMessage(String address, String subject, String body);

}

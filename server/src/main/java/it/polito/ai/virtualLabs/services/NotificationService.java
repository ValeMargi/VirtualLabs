package it.polito.ai.virtualLabs.services;

public interface NotificationService {
    void sendMessage(String address, String subject, String body);

}

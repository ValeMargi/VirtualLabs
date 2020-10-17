package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.exceptions.TeamNotFoundException;
import it.polito.ai.virtualLabs.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@Controller
@RequestMapping("/API/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{token}")
    @ResponseBody
    public ResponseEntity<Void> confirmationPage(@PathVariable String token) {
        HttpHeaders headers = new HttpHeaders();

        try {
            switch (notificationService.confirm(token)) {
                case 0:
                    headers.setLocation(URI.create("http://localhost:4200/team/not-valid/" + token));
                    break;
                case 1:
                    headers.setLocation(URI.create("http://localhost:4200/team/confirm/" + token));
                    break;
                case 2:
                    headers.setLocation(URI.create("http://localhost:4200/team/create/" + token));
                    break;
            }

            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }catch (TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/reject/{token}")
    @ResponseBody
    public ResponseEntity<Void> rejectionPage(@PathVariable String token) {
        HttpHeaders headers = new HttpHeaders();

        try {
            if (notificationService.reject(token) == 0) {
                headers.setLocation(URI.create("http://localhost:4200/team/not-valid/" + token));
            } else {
                headers.setLocation(URI.create("http://localhost:4200/team/reject/" + token));
            }

            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }catch(TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

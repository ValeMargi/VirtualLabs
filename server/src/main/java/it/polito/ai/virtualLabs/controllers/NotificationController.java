package it.polito.ai.virtualLabs.controllers;

import it.polito.ai.virtualLabs.exceptions.TeamNotFoundException;
import it.polito.ai.virtualLabs.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/API/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{token}")
    @ResponseBody
    public Integer confirmationPage(@PathVariable String token) {
        try{
            return notificationService.confirm(token);
        }catch (TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/reject/{token}")
    @ResponseBody
    public Integer rejectionPage(@PathVariable String token) {
        try {
            return notificationService.reject(token);
        }catch(TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}

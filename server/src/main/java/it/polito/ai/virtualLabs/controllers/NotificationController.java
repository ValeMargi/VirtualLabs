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
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/API/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping("/confirm/{token}")
    public String confirmationPage(@PathVariable String token, Model model) {
        boolean confirm;
        confirm = notificationService.confirm((token));
        model.addAttribute("confirm", confirm);
        return "confirm";
    }

    @GetMapping("/reject/{token}")
    public String rejectionPage(@PathVariable String token, Model model) {
        try {
            boolean reject;
            reject = notificationService.reject(token);
            model.addAttribute("reject", reject);
            return "reject";
        }catch(TeamNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

        }
    }
}
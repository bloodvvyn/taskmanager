package ru.zaikin.taskmanager.taskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/security")
public class MainController {

    @GetMapping("/user")
    public String userAccess(Principal principal) {

        if (principal == null) {
            return "You are not logged in";
        }

        return principal.getName();
    }
}

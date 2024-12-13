package ru.zaikin.taskmanager.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zaikin.taskmanager.taskmanager.model.Role;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {



    @GetMapping
    public String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());
        System.out.println(authentication.getName());
        return "Hello World";
    }
}

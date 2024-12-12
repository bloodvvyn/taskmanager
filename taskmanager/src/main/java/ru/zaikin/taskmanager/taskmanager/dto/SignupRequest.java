package ru.zaikin.taskmanager.taskmanager.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
}

package ru.zaikin.taskmanager.taskmanager.dto;


import lombok.Data;

@Data
public class CommentDTO {
    private String author;
    private String text;
    private Long taskId;
}

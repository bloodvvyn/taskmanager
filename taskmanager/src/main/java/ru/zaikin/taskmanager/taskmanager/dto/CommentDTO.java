package ru.zaikin.taskmanager.taskmanager.dto;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommentDTO {
    private String author;
    private String text;
    private Long taskId;
}

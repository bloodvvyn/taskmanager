package ru.zaikin.taskmanager.taskmanager.dto;
import lombok.Data;
import ru.zaikin.taskmanager.taskmanager.enums.Priority;
import ru.zaikin.taskmanager.taskmanager.enums.Status;

@Data
public class TaskDTO {
    private Long id;
    private String headline;
    private String description;
    private Status status;
    private Priority priority;
    private String author;
    private String executor;
}

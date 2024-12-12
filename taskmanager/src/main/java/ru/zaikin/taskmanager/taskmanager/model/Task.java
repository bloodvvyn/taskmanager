package ru.zaikin.taskmanager.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.zaikin.taskmanager.taskmanager.enums.Priority;
import ru.zaikin.taskmanager.taskmanager.enums.Status;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Task {
    @Id
    private Long id;
    private String headline;
    private String description;
    private Status status;
    private Priority priority;

    @OneToMany
    @JoinColumn(name = "comment_id")
    private List<Comment> comments = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToOne
    @JoinColumn(name = "executor_id")
    private User executor;
}

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String headline;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @OneToMany
    @JoinColumn(name = "comment_id")
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;
}

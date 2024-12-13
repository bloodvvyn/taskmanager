package ru.zaikin.taskmanager.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String text;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private LocalDateTime data;
}

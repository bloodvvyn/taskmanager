package ru.zaikin.taskmanager.taskmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Comment {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String text;
}

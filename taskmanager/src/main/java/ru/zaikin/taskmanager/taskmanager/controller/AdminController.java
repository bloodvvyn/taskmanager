package ru.zaikin.taskmanager.taskmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.zaikin.taskmanager.taskmanager.dto.TaskDTO;
import ru.zaikin.taskmanager.taskmanager.repository.RoleRepository;
import ru.zaikin.taskmanager.taskmanager.repository.TaskRepository;
import ru.zaikin.taskmanager.taskmanager.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public AdminController(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TaskDTO taskDTO) {



        /*    private String headline;
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
}*/
        return null;
    }

}

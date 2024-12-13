package ru.zaikin.taskmanager.taskmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.zaikin.taskmanager.taskmanager.dto.TaskDTO;
import ru.zaikin.taskmanager.taskmanager.model.Task;
import ru.zaikin.taskmanager.taskmanager.repository.RoleRepository;
import ru.zaikin.taskmanager.taskmanager.repository.TaskRepository;
import ru.zaikin.taskmanager.taskmanager.service.TaskService;
import ru.zaikin.taskmanager.taskmanager.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TaskService taskService;
    private final UserService userService;

    public AdminController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/get")
    public TaskDTO getTask(@RequestParam int id) {
        return taskService.getTask(id);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TaskDTO taskDTO) {

        try {
            taskService.createTask(taskDTO, 0);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(taskDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody TaskDTO taskDTO, @RequestParam long id) {

        try {
            taskService.createTask(taskDTO, id);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(taskDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam long id) {
        taskService.deleteTask(id);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}

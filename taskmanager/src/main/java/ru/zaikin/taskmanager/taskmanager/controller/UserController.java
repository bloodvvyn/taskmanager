package ru.zaikin.taskmanager.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.zaikin.taskmanager.taskmanager.dto.CommentDTO;
import ru.zaikin.taskmanager.taskmanager.enums.Status;
import ru.zaikin.taskmanager.taskmanager.exception.TaskNotFoundException;
import ru.zaikin.taskmanager.taskmanager.model.Task;
import ru.zaikin.taskmanager.taskmanager.model.User;
import ru.zaikin.taskmanager.taskmanager.service.TaskService;
import ru.zaikin.taskmanager.taskmanager.service.UserService;
import ru.zaikin.taskmanager.taskmanager.util.UserDetailsImpl;

@RestController
@RequestMapping("/user")
public class UserController {

    private final TaskService taskService;
    private final UserService userService;

    public UserController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }


    @GetMapping
    public String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());
        System.out.println(authentication.getName());

        return "Hello World";
    }


    @PostMapping("/status")
    @Operation(summary = "Изменяем статус задачи", description = "Создание задачи с помощью POST запроса")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача создана"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "404", description = "Задача или автор не найдена"),
            @ApiResponse(responseCode = "401", description = "У вас недостаточно прав для выполнения операции")
    })
    public ResponseEntity<?> changeStatus(@RequestParam Long id, @RequestParam Status status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl author = (UserDetailsImpl) authentication.getPrincipal();
        Long authorId = author.getId();

        try {
            taskService.setStatus(status, id, authorId);
        } catch (AuthorizationDeniedException e) {

        }

        return ResponseEntity.status(HttpStatus.OK).body("Task was updated successfully");


    }

    @PostMapping("/comment")
    public void addComment(@RequestBody CommentDTO commentDTO, @RequestParam Long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl author = (UserDetailsImpl) authentication.getPrincipal();
        String email = author.getUsername();

        taskService.addCommentByUser(commentDTO, taskId, email);
    }
}

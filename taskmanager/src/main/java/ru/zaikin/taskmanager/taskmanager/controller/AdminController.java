package ru.zaikin.taskmanager.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.zaikin.taskmanager.taskmanager.dto.CommentDTO;
import ru.zaikin.taskmanager.taskmanager.dto.TaskDTO;
import ru.zaikin.taskmanager.taskmanager.exception.TaskNotFoundException;
import ru.zaikin.taskmanager.taskmanager.service.TaskService;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin API", description = "API для управления задачами и комментариями для админа")
public class AdminController {

    private final TaskService taskService;
    private Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/comment")
    @Operation(summary = "Добавить комментарий", description = "Добавляет комментарий к задаче с указанным ID")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Комментарий успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "404", description = "Задача или автор не найдена"),
            @ApiResponse(responseCode = "401", description = "У вас недостаточно прав для выполнения операции")
    })
    public ResponseEntity<?>  addComment(@RequestBody CommentDTO commentDTO, @RequestParam long id) {

        try {
            taskService.addComment(commentDTO, id);
        } catch (UsernameNotFoundException e) {
            logger.error("Пользователь :" + commentDTO.getAuthor() + " не был найден");
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        } catch (TaskNotFoundException e) {
            logger.error("Задача под идентификатором :" + commentDTO.getTaskId() + " не был найдена");
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }

        logger.info("Комментарий: " + commentDTO + " был успешно создан!");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/get")
    @Operation(summary = "Получает задачу", description = "Получает json задачи с указанным с помощью параметра в url id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача возвращена"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "404", description = "Задача или автор не найдена"),
            @ApiResponse(responseCode = "401", description = "У вас недостаточно прав для выполнения операции")
    })
    public ResponseEntity<?> getTask(@RequestParam int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(taskService.getTaskDTO(id));
        } catch (TaskNotFoundException e) {
            logger.error("Задача под идентификатором :" + id + " не был найдена");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }
    }

    @PostMapping("/create")
    @Operation(summary = "Создание задачи", description = "Создание задачи с помощью POST запроса")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача создана"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "404", description = "Задача или автор не найдена"),
            @ApiResponse(responseCode = "401", description = "У вас недостаточно прав для выполнения операции")
    })
    public ResponseEntity<?> create(@RequestBody TaskDTO taskDTO) {

        try {
            taskService.createTask(taskDTO, taskDTO.getId());
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(taskDTO);
    }

    @PutMapping("/update")
    @Operation(summary = "Обновление задачи", description = "Обновление задачи с помощью PUT запроса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "404", description = "Задача или автор не найдена"),
            @ApiResponse(responseCode = "401", description = "У вас недостаточно прав для выполнения операции")
    })
    public ResponseEntity<?> update(@RequestBody TaskDTO taskDTO, @RequestParam long id) {

        try {
            taskService.createTask(taskDTO, id);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("Username not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK).body(taskDTO);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Удаление задачи", description = "Удаление задачи с помощью delete запроса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача удалена"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос"),
            @ApiResponse(responseCode = "404", description = "Задача или автор не найдена"),
            @ApiResponse(responseCode = "401", description = "У вас недостаточно прав для выполнения операции")
    })
    public ResponseEntity<?> delete(@RequestParam long id) {

        try {
            taskService.deleteTask(id);
        } catch (TaskNotFoundException e) {
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

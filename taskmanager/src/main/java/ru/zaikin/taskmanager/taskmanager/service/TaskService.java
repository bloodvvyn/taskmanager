package ru.zaikin.taskmanager.taskmanager.service;

import org.apache.tomcat.websocket.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zaikin.taskmanager.taskmanager.dto.CommentDTO;
import ru.zaikin.taskmanager.taskmanager.dto.TaskDTO;
import ru.zaikin.taskmanager.taskmanager.enums.Status;
import ru.zaikin.taskmanager.taskmanager.exception.TaskNotFoundException;
import ru.zaikin.taskmanager.taskmanager.model.Comment;
import ru.zaikin.taskmanager.taskmanager.model.Task;
import ru.zaikin.taskmanager.taskmanager.repository.TaskRepository;

import java.time.LocalDateTime;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Transactional
    public void addCommentByUser(CommentDTO commentDTO, long taskId, String authorEmail)  {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("task not found"));

        if (task.getAuthor().getEmail() != authorEmail)
            throw new AuthorizationDeniedException("You have not enough right");

        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setAuthor(userService.getUser(commentDTO.getAuthor()));
        comment.setData(LocalDateTime.now());
        comment.setTask(taskRepository.findById(commentDTO.getTaskId()).get());

        taskRepository.findById(taskId).get().addComment(comment);

    }

    @Transactional
    public void addComment(CommentDTO commentDTO, long id) {

        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setAuthor(userService.getUser(commentDTO.getAuthor()));
        comment.setData(LocalDateTime.now());

        if (taskRepository.findById(commentDTO.getTaskId()).isEmpty())
            throw new TaskNotFoundException("Задача под идентификатором :" + commentDTO.getTaskId() + " не была найдена");

        comment.setTask(taskRepository.findById(commentDTO.getTaskId()).get());

        taskRepository.findById(id).get().addComment(comment);

    }

    @Transactional
    public void setStatus(Status taskStatus, long taskId, long authorId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (task.getAuthor().getId() != authorId) {
            throw new AuthorizationDeniedException("You haven't permission to change this task");
        } else {
            task.setStatus(taskStatus);
            taskRepository.save(task);
        }

    }

    @Transactional(readOnly = true)
    public Task getTask(long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskDTO(long id) {
        TaskDTO taskDTO = new TaskDTO();
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));

        taskDTO.setId(id);
        taskDTO.setHeadline(task.getHeadline());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setAuthor(task.getAuthor().getEmail());
        taskDTO.setExecutor(task.getExecutor().getEmail());


        return taskDTO;
    }


    @Transactional
    public Task createTask(TaskDTO taskDTO, Long id) {

        Task task = new Task();

        if (id != null)
            task.setId(id);

        task.setHeadline(taskDTO.getHeadline());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setAuthor(userService.getUser(taskDTO.getAuthor()));
        task.setExecutor(userService.getUser(taskDTO.getExecutor()));

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(long id) {

        taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));

        taskRepository.deleteById(id);
    }
}

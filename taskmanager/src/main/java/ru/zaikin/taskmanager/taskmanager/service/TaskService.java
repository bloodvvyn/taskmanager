package ru.zaikin.taskmanager.taskmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zaikin.taskmanager.taskmanager.dto.TaskDTO;
import ru.zaikin.taskmanager.taskmanager.model.Task;
import ru.zaikin.taskmanager.taskmanager.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public TaskDTO getTask(long id) {
        TaskDTO taskDTO = new TaskDTO();
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

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
    public Task createTask(TaskDTO taskDTO, long id) {

        try {
            userService.getUser(taskDTO.getAuthor());
        } catch (UsernameNotFoundException e) {
            logger.error(taskDTO.getAuthor() + " not found");
        }

        try {
            userService.getUser(taskDTO.getExecutor());
        } catch (UsernameNotFoundException e) {
            logger.error(taskDTO.getAuthor() + " not found");
        }

        Task task = new Task();
        if (id != 0)
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
        taskRepository.deleteById(id);
    }
}

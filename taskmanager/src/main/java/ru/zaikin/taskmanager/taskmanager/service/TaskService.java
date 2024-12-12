package ru.zaikin.taskmanager.taskmanager.service;

import org.springframework.stereotype.Service;
import ru.zaikin.taskmanager.taskmanager.model.Task;
import ru.zaikin.taskmanager.taskmanager.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
}

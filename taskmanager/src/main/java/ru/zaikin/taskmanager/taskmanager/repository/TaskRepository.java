package ru.zaikin.taskmanager.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zaikin.taskmanager.taskmanager.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}

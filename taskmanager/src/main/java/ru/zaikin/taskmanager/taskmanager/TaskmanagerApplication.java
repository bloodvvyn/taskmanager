package ru.zaikin.taskmanager.taskmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.zaikin.taskmanager.taskmanager.util.JWTCore;

@SpringBootApplication
public class TaskmanagerApplication {

	private JWTCore jwtCore;

	@Autowired
	public TaskmanagerApplication(JWTCore jwtCore) {
		this.jwtCore = jwtCore;
	}

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerApplication.class, args);
	}

}

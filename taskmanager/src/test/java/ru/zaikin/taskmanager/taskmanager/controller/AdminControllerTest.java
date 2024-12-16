package ru.zaikin.taskmanager.taskmanager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.zaikin.taskmanager.taskmanager.dto.SigninRequest;
import ru.zaikin.taskmanager.taskmanager.dto.SignupRequest;
import ru.zaikin.taskmanager.taskmanager.dto.TaskDTO;
import ru.zaikin.taskmanager.taskmanager.enums.Priority;
import ru.zaikin.taskmanager.taskmanager.enums.Status;
import ru.zaikin.taskmanager.taskmanager.exception.TaskNotFoundException;
import ru.zaikin.taskmanager.taskmanager.model.Task;
import ru.zaikin.taskmanager.taskmanager.model.User;
import ru.zaikin.taskmanager.taskmanager.repository.TaskRepository;
import ru.zaikin.taskmanager.taskmanager.repository.UserRepository;
import ru.zaikin.taskmanager.taskmanager.service.TaskService;
import ru.zaikin.taskmanager.taskmanager.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminController adminController;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthController authController;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setUp() {
        postgreSQLContainer.start();
        taskRepository.deleteAll();
        userRepository.deleteAll();

        jdbcTemplate.execute("ALTER SEQUENCE task_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE task_seq RESTART WITH 1");

    }

    @AfterEach
    void tearDown() {
        postgreSQLContainer.stop();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateTaskSuccessfully() throws Exception {

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@mail.ru");
        signupRequest.setPassword("test");

        String signupJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.ru"));

        User user = userService.getUser("test@mail.ru");
        assert user != null;
        assert user.getEmail().equals("test@mail.ru");


        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setHeadline("headline");
        taskDTO.setDescription("description");
        taskDTO.setStatus(Status.IN_PROGRESS);
        taskDTO.setPriority(Priority.HIGH);
        taskDTO.setAuthor("test@mail.ru");
        taskDTO.setExecutor("test@mail.ru");

        String taskJson = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));


        Task task = taskService.getTask(1L);

        assert task != null;
        assert task.getAuthor().getEmail().equals("test@mail.ru");
        assert task.getExecutor().getEmail().equals("test@mail.ru");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateTaskFailure() throws Exception {

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setHeadline("headline");
        taskDTO.setDescription("description");
        taskDTO.setStatus(Status.IN_PROGRESS);
        taskDTO.setPriority(Priority.HIGH);
        taskDTO.setAuthor("test@mail.ru");
        taskDTO.setExecutor("test@mail.ru");

        String taskJson = objectMapper.writeValueAsString(taskDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.content().string("Username not found"));


        assertThrows(TaskNotFoundException.class, () -> taskService.getTask(1L));
    }


}
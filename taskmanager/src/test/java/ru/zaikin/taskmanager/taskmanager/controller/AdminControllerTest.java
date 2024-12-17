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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.zaikin.taskmanager.taskmanager.dto.CommentDTO;
import ru.zaikin.taskmanager.taskmanager.dto.SignupRequest;
import ru.zaikin.taskmanager.taskmanager.dto.TaskDTO;
import ru.zaikin.taskmanager.taskmanager.enums.Priority;
import ru.zaikin.taskmanager.taskmanager.enums.Status;
import ru.zaikin.taskmanager.taskmanager.exception.TaskNotFoundException;
import ru.zaikin.taskmanager.taskmanager.model.Comment;
import ru.zaikin.taskmanager.taskmanager.model.Task;
import ru.zaikin.taskmanager.taskmanager.model.User;
import ru.zaikin.taskmanager.taskmanager.repository.TaskRepository;
import ru.zaikin.taskmanager.taskmanager.repository.UserRepository;
import ru.zaikin.taskmanager.taskmanager.service.TaskService;
import ru.zaikin.taskmanager.taskmanager.service.UserService;

import java.util.List;

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

    private TaskDTO taskDTO;
    private String taskJson;
    private SignupRequest signupRequest;
    private String signupJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        postgreSQLContainer.start();
        taskRepository.deleteAll();
        userRepository.deleteAll();

        jdbcTemplate.execute("ALTER SEQUENCE task_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE task_seq RESTART WITH 1");

        jdbcTemplate.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1");

        taskDTO = new TaskDTO();
        taskDTO.setHeadline("headline");
        taskDTO.setDescription("description");
        taskDTO.setStatus(Status.IN_PROGRESS);
        taskDTO.setPriority(Priority.HIGH);
        taskDTO.setAuthor("test@mail.ru");
        taskDTO.setExecutor("test@mail.ru");

        taskJson = objectMapper.writeValueAsString(taskDTO);

        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@mail.ru");
        signupRequest.setPassword("test");
        signupJson = objectMapper.writeValueAsString(signupRequest);

    }

    @AfterEach
    void tearDown() {
        postgreSQLContainer.stop();
    }

    @Test
    @WithMockUser(username = "admin", roles={"ADMIN"})
    public void addCommentSuccess() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setAuthor("test@mail.ru");
        commentDTO.setText("Тестим комментарий");
        commentDTO.setTaskId(1L);

        String commentJson = objectMapper.writeValueAsString(commentDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.ru"));

        User user = userService.getUser("test@mail.ru");
        assert user != null;
        assert user.getEmail().equals("test@mail.ru");


        mockMvc.perform(MockMvcRequestBuilders.post("/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/comment?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));

        List<Comment> comments = taskRepository.findById(1L).get().getComments();

        assert comments.get(0).getText().equals(commentDTO.getText());

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Transactional
    public void getTaskSucessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.ru"));

        User user = userService.getUser("test@mail.ru");
        assert user != null;
        assert user.getEmail().equals("test@mail.ru");


        mockMvc.perform(MockMvcRequestBuilders.post("/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));

        assert taskService.getTask(1).getHeadline().equals(taskDTO.getHeadline());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void deleteUserSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.ru"));

        User user = userService.getUser("test@mail.ru");
        assert user != null;
        assert user.getEmail().equals("test@mail.ru");


        mockMvc.perform(MockMvcRequestBuilders.post("/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));

        Task task = taskService.getTask(1);

        assert task != null;
        assert task.getAuthor().getEmail().equals("test@mail.ru");
        assert task.getExecutor().getEmail().equals("test@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/delete?id=1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));

        assertThrows(TaskNotFoundException.class, () -> taskService.getTask(1));


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateUserSuccessfully() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.ru"));

        User user = userService.getUser("test@mail.ru");
        assert user != null;
        assert user.getEmail().equals("test@mail.ru");


        mockMvc.perform(MockMvcRequestBuilders.post("/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));

        Task task = taskService.getTask(1);

        assert task != null;
        assert task.getAuthor().getEmail().equals("test@mail.ru");
        assert task.getExecutor().getEmail().equals("test@mail.ru");

        taskDTO.setDescription("updated description");
        taskJson = objectMapper.writeValueAsString(taskDTO);


        mockMvc.perform(MockMvcRequestBuilders.put("/admin/update?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));

        assert taskDTO.getDescription().equals(taskService.getTask(1).getDescription());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateTaskSuccessfully() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@mail.ru"));

        User user = userService.getUser("test@mail.ru");
        assert user != null;
        assert user.getEmail().equals("test@mail.ru");



        mockMvc.perform(MockMvcRequestBuilders.post("/admin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()));


        Task task = taskService.getTask(-48L);

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
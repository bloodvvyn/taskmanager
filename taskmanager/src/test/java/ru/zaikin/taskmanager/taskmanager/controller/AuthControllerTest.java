package ru.zaikin.taskmanager.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.zaikin.taskmanager.taskmanager.dto.SigninRequest;
import ru.zaikin.taskmanager.taskmanager.dto.SignupRequest;
import ru.zaikin.taskmanager.taskmanager.repository.UserRepository;
import ru.zaikin.taskmanager.taskmanager.model.User;
import ru.zaikin.taskmanager.taskmanager.service.UserService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthController authController;

    @BeforeEach
    void setUp() {
        postgresContainer.start();
        userRepository.deleteAll();
    }

    @Test
    void testSigninSuccess() throws Exception {

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        authController.signup(signupRequest);

        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("test@example.com");
        signinRequest.setPassword("password123");

        String signinJson = objectMapper.writeValueAsString(signinRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signinJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testSigninFailure() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        authController.signup(signupRequest);

        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("test@example.com");
        signinRequest.setPassword("password1233");

        String signinJson = objectMapper.writeValueAsString(signinRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signinJson))

        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(MockMvcResultMatchers.content().string("Invalid email or password"));
    }


    @Test
    void testSignupSucess() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");



        String signupJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));

        User user = userService.getUser("test@example.com");
        assert user != null;
        assert user.getEmail().equals("test@example.com");
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));
    }

    @Test
    void testSignupEmailAlreadyExists() throws Exception {

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        userService.addUser(user);

        String signupJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.content().string("Choose different email"));

        assertEquals("test@example.com", user.getEmail());
    }


    @AfterEach
    void tearDown() {
        postgresContainer.stop();
    }
}


package ru.zaikin.taskmanager.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zaikin.taskmanager.taskmanager.dto.SigninRequest;
import ru.zaikin.taskmanager.taskmanager.dto.SignupRequest;
import ru.zaikin.taskmanager.taskmanager.model.Role;
import ru.zaikin.taskmanager.taskmanager.model.User;
import ru.zaikin.taskmanager.taskmanager.repository.RoleRepository;
import ru.zaikin.taskmanager.taskmanager.repository.UserRepository;
import ru.zaikin.taskmanager.taskmanager.util.JWTCore;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTCore jwtCore;

    @Autowired
    public SecurityController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTCore jwtCore) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequest signinRequest) {
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signinRequest.getEmail(), signinRequest.getPassword()
            ));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);

    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {


        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }

        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);

        Role role = roleRepository.findById(Long.valueOf(1)).get();
        System.out.println(role.getName());
        user.addRole(role);
        System.out.println(user.getRoles());

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);

    }

}

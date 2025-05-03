package com.example.demo.controller;

import com.example.demo.entity.AuthResponse;
import com.example.demo.entity.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
//    @Autowired
//    AuthenticationManager authenticationManager;

    private final AuthenticationManager authManager;
    private final JwtTokenUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authManager, JwtTokenUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPrimaryRole("USER");
        user.setIsActive(false); // Wait for verification
        user.setDepartmentHead(false); // default
        String token = UUID.randomUUID().toString();
        user.setRememberToken(token);

        // 4) set created timestamp (if your entity has it)

        // 5) save & send email
        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);


        return ResponseEntity.ok("Registration successful. Please check your email to verify.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        Optional<User> userOpt = userRepository.findByRememberToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(true);
            user.setRememberToken(null);
            userRepository.save(user);
            return ResponseEntity.ok("Account verified successfully!");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification token.");
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
//        logger.info("Login attempt for email: {}", loginRequest.getEmail());
//        try {
//            Authentication auth = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getEmail(),
//                            loginRequest.getPassword()
//                    )
//            );
//            SecurityContextHolder.getContext().setAuthentication(auth);
//            logger.info("Login successful for email: {}", loginRequest.getEmail());
//            return ResponseEntity.ok("Login successful");
//        } catch (BadCredentialsException ex) {
//            logger.warn("Invalid login attempt for email: {}", loginRequest.getEmail());
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body("Invalid email or password");
//        } catch (Exception ex) {
//            logger.error("Unexpected error during login for email: {}", loginRequest.getEmail(), ex);
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An unexpected error occurred");
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        String jwt = jwtUtil.generateToken(auth.getName());
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}



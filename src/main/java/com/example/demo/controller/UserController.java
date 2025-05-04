package com.example.demo.controller;

import com.example.demo.entity.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> me(Principal principal) {
        if (principal == null) {
            // Should not happen if /me is protected correctly
            logger.warn("Attempt to call /me without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = principal.getName();
        logger.info("Authenticated user email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        UserDto dto = new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIsActive()  ? 1 : 0,
                user.getIsVisible()  ? 1 : 0,
                user.getHoursAvailableDay(),
                user.getTeam(),
                user.getPictureUrl(),
                user.getIntegrationEntityId(),
                user.getDepartmentHead()  ? 1 : 0,
                user.getPrimaryRole(),
                user.getIntegration(),
                user.getPtos()
        );

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }
}
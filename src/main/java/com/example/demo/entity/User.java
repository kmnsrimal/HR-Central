package com.example.demo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String primaryRole;

    private Boolean isActive;
    private Boolean isVisible;

    private Integer hoursAvailableDay;

    private String team;
    private String pictureUrl;

    private String integrationId;
    private String integrationEntityId;

    private Boolean departmentHead;

    // Getters and Setters or use Lombok's @Data

    @Column(name = "remember_token", unique = true)
    private String rememberToken;
}

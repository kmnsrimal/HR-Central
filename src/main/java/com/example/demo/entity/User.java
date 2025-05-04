package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "primary_role", referencedColumnName = "id")
    private Role primaryRole;

    private Boolean isActive;
    private Boolean isVisible;

    private Integer hoursAvailableDay;

    private String team;
    private String pictureUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "integration_id", referencedColumnName = "id")
    private Integration integration;

    private Integer integrationEntityId;

    private Boolean departmentHead;

    // Getters and Setters or use Lombok's @Data

    @Column(name = "remember_token", unique = true)
    private String rememberToken;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Pto> ptos = new ArrayList<>();



}

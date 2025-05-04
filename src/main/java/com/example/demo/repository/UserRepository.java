package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByUsername(String name);
      Optional<User> findByRememberToken(String rememberToken);
    Optional<User> findByEmail(String email);
    @EntityGraph(attributePaths = {"role", "integration"})
    Optional<User> findWithRoleAndIntegrationById(Long id);

}

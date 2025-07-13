package com.paulo.api_spring_jwt.repositorys;

import com.paulo.api_spring_jwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
}
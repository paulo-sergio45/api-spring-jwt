package com.paulo.api_spring_jwt.repositorys;

import com.paulo.api_spring_jwt.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}

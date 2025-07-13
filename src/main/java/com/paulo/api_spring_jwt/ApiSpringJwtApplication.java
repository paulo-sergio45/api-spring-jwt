package com.paulo.api_spring_jwt;

import com.paulo.api_spring_jwt.models.User;
import com.paulo.api_spring_jwt.models.Role;
import com.paulo.api_spring_jwt.repositorys.RoleRepository;
import com.paulo.api_spring_jwt.repositorys.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class ApiSpringJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiSpringJwtApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		return args -> {

			Role adminRole = new Role("ADMIN");
			Role userRole = new Role("USER");

			roleRepository.saveAll(List.of(adminRole, userRole));

			User admin = new User();
			admin.setUsername("admin");
			admin.addRole(adminRole);
			admin.setPassword(passwordEncoder.encode("admin"));

			User user = new User();
			user.setUsername("user");
			user.addRole(userRole);
			user.setPassword(passwordEncoder.encode("user"));

			repository.saveAll(List.of(admin, user));
		};
	}
}

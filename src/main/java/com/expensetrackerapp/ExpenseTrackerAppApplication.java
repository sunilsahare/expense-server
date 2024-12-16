package com.expensetrackerapp;

import com.expensetrackerapp.constants.ROLE;
import com.expensetrackerapp.modal.User;
import com.expensetrackerapp.repository.UserRepository;
import com.expensetrackerapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ExpenseTrackerAppApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public static void main(String[] args) {
        SpringApplication.run(ExpenseTrackerAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        User user = new User();
        user.setFullName("Sunil Sahare");
        user.setRole(ROLE.USER.name());
        user.setUsername("sunil");

        user.setPassword(passwordEncoder.encode("Sunil@123"));

        User admin = new User();
        admin.setFullName("Admin User");
        admin.setRole(ROLE.USER.name());
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("Admin@123"));



        User savedUser = userRepository.save(user);
        User savedAdminUser = userRepository.save(admin);

        System.err.println("Saved User - "+savedUser);
        System.err.println("Saved Admin User - "+savedAdminUser);


    }
}

package com.expensetrackerapp.controller;

import com.expensetrackerapp.exception.HttpApiResponse;
import com.expensetrackerapp.exception.UserException;
import com.expensetrackerapp.exception.UserNotFoundException;
import com.expensetrackerapp.modal.JwtRequest;
import com.expensetrackerapp.modal.User;
import com.expensetrackerapp.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public ResponseEntity<HttpApiResponse<Object>> authenticate(@RequestBody JwtRequest request) {
        HttpApiResponse<Object> response = new HttpApiResponse<>();
        response.setMessage("User Successfully Authenticated");
        response.setStatusCode(HttpStatus.OK.value());
        response.setResponseData(userService.login(request));
        response.setTimestamp(new Timestamp(System.currentTimeMillis()));
        logger.info("'{}' User successfully authenticated.",request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody @Valid User uerDto) throws UserNotFoundException {
        User savedUser = userService.addUser(uerDto);
        logger.info("'{}' User successfully registered.",uerDto.getUsername());

        HttpApiResponse<Object> response = new HttpApiResponse<>();
        response.setMessage("User Successfully registered.");
        response.setStatusCode(HttpStatus.OK.value());
        response.setResponseData(savedUser);
        response.setTimestamp(new Timestamp(System.currentTimeMillis()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/home")
    public String home() {
        return "This is a open API";
    }


}
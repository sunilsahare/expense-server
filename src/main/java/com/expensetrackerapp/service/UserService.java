package com.expensetrackerapp.service;

import com.expensetrackerapp.constants.ROLE;
import com.expensetrackerapp.exception.UserNotFoundException;
import com.expensetrackerapp.modal.JwtRequest;
import com.expensetrackerapp.modal.User;
import com.expensetrackerapp.repository.UserRepository;
import com.expensetrackerapp.security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private AuthenticationManager authenticationManager;

    public UserService() {

    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User addUser(User user) throws UserNotFoundException {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(ROLE.USER.name());
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

        if (optionalUser.isPresent()) {
            throw new UserNotFoundException("Please use other username.");
        }

        return userRepository.save(user);
    }

    public Map<String, Serializable> login(JwtRequest request) {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername()   ,
                                request.getPassword()
                        )
                );
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String jwtToken = jwtHelper.generateToken(user);

        return Map.of("token", jwtToken, "user", user);
    }

    public Integer getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                return user.getId();
            }
        }
        return null;
    }


}

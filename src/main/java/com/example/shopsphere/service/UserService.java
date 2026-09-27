package com.example.shopsphere.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.shopsphere.model.User;
import com.example.shopsphere.model.UserStatus;
import com.example.shopsphere.repo.userRepo;

@Service
public class UserService {
    private final userRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(userRepo userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (user.getRole() == null) {
            user.setRole(com.example.shopsphere.model.Role.CUSTOMER);
            userRepository.save(user);
        }
        if (!passwordEncoder.matches(password, user.getPassword()) || user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return user;
    }

}

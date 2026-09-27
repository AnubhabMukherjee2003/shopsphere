package com.example.shopsphere.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.shopsphere.model.User;
import com.example.shopsphere.model.UserStatus;
import com.example.shopsphere.repo.userRepo;

@Service
public class UserService {
    private final userRepo userRepository;

    public UserService(userRepo userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!user.getPassword().equals(password) || user.getStatus() != UserStatus.ACTIVE) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return user;
    }

}

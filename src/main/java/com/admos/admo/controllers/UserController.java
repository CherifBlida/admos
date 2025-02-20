package com.admos.admo.controllers;


import com.admos.admo.entities.User;
import com.admos.admo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/Users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
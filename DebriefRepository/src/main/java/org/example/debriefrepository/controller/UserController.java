package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.service.UserService;
import org.example.debriefrepository.types.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @QueryMapping
    public List<User> users(@Argument("input") Map<String, Object> input) {
        return userService.getUser(input);
    }

    @QueryMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @MutationMapping
    public User addUser(@Argument("input") UserInput user) {
        return userService.createUser(user);
    }

    @MutationMapping
    public User updateUser(@Argument("input") Map<String, Object> user) {
        return userService.update(user);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        return userService.deleteById(id);
    }

}

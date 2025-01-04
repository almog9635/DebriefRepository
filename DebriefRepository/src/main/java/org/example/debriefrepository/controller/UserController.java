package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.types.input.UserInput;
import org.example.debriefrepository.service.UserService;
import org.example.debriefrepository.types.update.UserUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @QueryMapping
    public User users(@Argument Long id) {
        return userService.getUserById(id);
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
    public User updateUser(@Argument("input") UserUpdate user) {
        return userService.updateById(user);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        return userService.deleteById(id);
    }
}

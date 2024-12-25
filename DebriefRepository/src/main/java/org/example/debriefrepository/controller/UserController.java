package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.service.UserService;
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
    public User userById(@Argument Long id) {
        return userService.getUserById(id);
    }

    @QueryMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @MutationMapping
    public User create(@Argument User user) {
        return userService.createUser(user);
    }

    @MutationMapping
    public User update(@Argument User user) {
        return userService.updateById(user.getId(), user);
    }

    @MutationMapping
    public Boolean delete(@Argument Long id) {
        return userService.deleteById(id);
    }
}

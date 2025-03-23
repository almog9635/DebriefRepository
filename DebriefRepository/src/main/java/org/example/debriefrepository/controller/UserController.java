package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.service.UserService;
import org.example.debriefrepository.types.input.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
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
    public User createUser(@Argument("input") UserInput user, @ContextValue String userId) {
        User newUser = null;
        try {
            UserContext.setCurrentUserId(userId);
            newUser =  userService.createUser(user);
        }
        finally {
            UserContext.clear();
        }
        return newUser;
    }

    @MutationMapping
    public User updateUser(@Argument("input") UserInput user, @ContextValue String userId) {
        User updatedUser = null;
        try {
            UserContext.setCurrentUserId(userId);
            updatedUser =  userService.update(user);
        }
        finally {
            UserContext.clear();
        }
        return updatedUser;
    }

    @MutationMapping
    public Boolean deleteUser(@Argument String id) {
        return userService.deleteById(id);
    }

}

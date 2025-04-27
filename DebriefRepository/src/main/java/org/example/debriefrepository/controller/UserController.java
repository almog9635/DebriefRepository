package org.example.debriefrepository.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext.WithUserContext;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.service.UserService;
import org.example.debriefrepository.types.consts.Const;
import org.example.debriefrepository.types.input.UserInput;
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
    public List<User> users(@Argument(Const.INPUT) Map<String, Object> input) {
        return userService.getUser(input);
    }

    @QueryMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @WithUserContext
    @MutationMapping
    public User createUser(@Argument(Const.INPUT) UserInput user, DataFetchingEnvironment environment) {
        return userService.createUser(user);
    }

    @WithUserContext
    @MutationMapping
    public User updateUser(@Argument(Const.INPUT) UserInput input, DataFetchingEnvironment environment) {
        return userService.update(input);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument String id) {
        return userService.deleteById(id);
    }

}

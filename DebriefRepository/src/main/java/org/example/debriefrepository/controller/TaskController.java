package org.example.debriefrepository.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.config.UserContext.WithUserContext;
import org.example.debriefrepository.entity.Task;
import org.example.debriefrepository.service.debrief.TaskService;
import org.example.debriefrepository.types.consts.Const;
import org.example.debriefrepository.types.input.TaskInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TaskController {

    @Autowired
    private final TaskService taskService;

    @WithUserContext
    @MutationMapping
    public Task updateTask(@Argument(Const.INPUT) TaskInput input, DataFetchingEnvironment environment) {
        return taskService.updateTask(input);
    }
}

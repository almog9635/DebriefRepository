package org.example.debriefrepository.controller;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.repository.DebriefRepository;
import org.example.debriefrepository.service.DebriefService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DebriefController {
    private final DebriefService debriefService;

    @QueryMapping
    public String hello() {
        return "hello, world";
    }
}

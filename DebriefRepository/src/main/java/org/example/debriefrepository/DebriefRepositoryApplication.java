package org.example.debriefrepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration
public class DebriefRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DebriefRepositoryApplication.class, args);
    }

}

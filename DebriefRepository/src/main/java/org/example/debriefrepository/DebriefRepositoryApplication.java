package org.example.debriefrepository;

import org.example.debriefrepository.config.GraphQLConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ImportAutoConfiguration
public class DebriefRepositoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DebriefRepositoryApplication.class, args);
	}

}

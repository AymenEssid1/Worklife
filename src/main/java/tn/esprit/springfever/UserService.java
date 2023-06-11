package tn.esprit.springfever;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableSpringConfigured
@EntityScan (basePackages = {"tn.esprit.springfever.entities"})
@EnableBatchProcessing
@SpringBootApplication
@EnableJpaRepositories("tn.esprit.springfever.Repositories")
public class UserService {
    public static void main(String[] args) {
        SpringApplication.run(UserService.class, args);
    }
}
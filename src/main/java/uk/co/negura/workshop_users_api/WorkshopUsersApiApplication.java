package uk.co.negura.workshop_users_api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@SpringBootApplication
@EnableWebSecurity
public class WorkshopUsersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkshopUsersApiApplication.class, args);
    }

}

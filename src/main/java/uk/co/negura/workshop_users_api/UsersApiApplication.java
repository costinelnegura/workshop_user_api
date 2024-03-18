package uk.co.negura.workshop_users_api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class UsersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApiApplication.class, args);
    }

}

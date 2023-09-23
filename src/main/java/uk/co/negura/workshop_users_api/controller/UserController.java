package uk.co.negura.workshop_users_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

}

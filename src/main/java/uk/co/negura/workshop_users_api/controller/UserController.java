package uk.co.negura.workshop_users_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.negura.workshop_users_api.model.User;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final List<User> userList = Arrays.asList(
            new User(1, "John"),
            new User(2, "Anna")
    );

    @GetMapping(path = "/{userID}")
    public User getUser (@PathVariable Integer userID) {
        return userList.stream()
                .filter(user -> userID.equals(user.getUserID()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User " + userID + " does not exist(s)"));
    }

}

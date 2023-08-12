package uk.co.negura.workshop_users_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.negura.workshop_users_api.model.User;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/management/api/v1/users")
public class UserManagementController {

    private static final List<User> userList = Arrays.asList(
            new User(1, "Dorel"),
            new User(2, "Cornel"),
            new User(3, "Gigel")
    );

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
    public List<User> getAppUsers(){
        return userList;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student:write')")
    public void registerNewStudent(@RequestBody User user){
        System.out.println(user);
    }

    @DeleteMapping(path = "{userID}")
    @PreAuthorize("hasAuthority('student:write')")
    public void deleteUser(@PathVariable Integer userID){
        System.out.println(userID);
    }

    @PutMapping(path = "{userID}")
    @PreAuthorize("hasAuthority('student:write')")
    public void updateUser(@PathVariable Integer userID, @RequestBody User user){
        System.out.println(String.format("%s, %s", userID, user));
    }
}

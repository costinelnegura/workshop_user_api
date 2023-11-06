package uk.co.negura.workshop_users_api.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.negura.workshop_users_api.model.Authority;
import uk.co.negura.workshop_users_api.model.User;
import uk.co.negura.workshop_users_api.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user, Collection<Authority> authorities){
        return userService.createUser(user, authorities);
    }

    @GetMapping(value = "/{ID}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long ID){
        return userService.getUserDetails(ID);
    }

    @PatchMapping("/details/{ID}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long ID, @RequestBody JsonPatch patch){
        return userService.updateUser(ID, patch);
    }

    @DeleteMapping(value = "/{ID}")
    public ResponseEntity<?> deleteUser(@PathVariable Long ID){
        return userService.deleteUser(ID);
    }
}

package uk.co.negura.workshop_users_api.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.negura.workshop_users_api.model.UserEntity;
import uk.co.negura.workshop_users_api.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*
    Create a new user and save the user details.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserEntity user){
        return userService.createUser(user);
    }

    /*
    Get user details using the ID.
     */
    @GetMapping(value = "/{ID}")
    public ResponseEntity<?> getUserDetails(@PathVariable String ID){
        return userService.getUserDetails(ID);
    }

    /*
    Update user details using the ID and a JSon object containing the new user info, and then save the updated user details.
     */
    @PatchMapping("/{ID}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long ID, @RequestBody JsonPatch patch){
        return userService.updateUser(ID, patch);
    }

    /*
    Delete user details using the ID.
     */
    @DeleteMapping(value = "/{ID}")
    public ResponseEntity<?> deleteUser(@PathVariable Long ID){
        return userService.deleteUser(ID);
    }
}

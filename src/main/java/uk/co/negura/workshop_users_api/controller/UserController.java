package uk.co.negura.workshop_users_api.controller;

import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.negura.workshop_users_api.model.UserEntity;
import uk.co.negura.workshop_users_api.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserEntity user){
        return userService.createUser(user);
    }

    @GetMapping(value = "/{ID}")
    public ResponseEntity<?> getUserDetails(@PathVariable String ID){
        return userService.getUserDetails(ID);
    }

    @PatchMapping("/{ID}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long ID, @RequestBody JsonPatch patch){
        return userService.updateUser(ID, patch);
    }

    @DeleteMapping(value = "/{ID}")
    public ResponseEntity<?> deleteUser(@PathVariable Long ID){
        return userService.deleteUser(ID);
    }
}

package uk.co.negura.workshop_users_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.co.negura.workshop_users_api.model.WorkshopUser;

import java.util.List;

@RestController
@RequestMapping("/management/api/v1/users")
public class UserManagementController {

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ESTIMATORTRAINEE')")
    public List<WorkshopUser> getAppUsers(){
        return null;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('estimator:write')")
    public void registerNewStudent(@RequestBody WorkshopUser workshopUser){
        System.out.println(workshopUser);
    }

    @DeleteMapping(path = "{userID}")
    @PreAuthorize("hasAuthority('estimator:write')")
    public void deleteUser(@PathVariable Integer userID){
        System.out.println(userID);
    }

    @PutMapping(path = "{userID}")
    @PreAuthorize("hasAuthority('estimator:write')")
    public void updateUser(@PathVariable Integer userID, @RequestBody WorkshopUser workshopUser){
        System.out.printf("%s, %s%n", userID, workshopUser);
    }
}

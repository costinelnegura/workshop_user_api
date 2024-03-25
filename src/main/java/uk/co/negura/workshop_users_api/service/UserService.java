package uk.co.negura.workshop_users_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.co.negura.workshop_users_api.model.RoleEntity;
import uk.co.negura.workshop_users_api.model.UserEntity;
import uk.co.negura.workshop_users_api.repository.RoleRepository;
import uk.co.negura.workshop_users_api.repository.UserRepository;
import uk.co.negura.workshop_users_api.util.JwtTokenUtil;

import java.util.*;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;


    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
            Get user details using the ID.
             */
    public ResponseEntity<?> getUserDetails(Long ID){
        Map<String, Object> map = new LinkedHashMap<>();
        if (ID == null) {
            map.put("object", "Error");
            map.put("status", 400);
            map.put("message", "ID is required.");
//            LOGGER.info("ID is required.");
            return ResponseEntity.badRequest().body(map);
        } else if(userRepository.existsById(ID)) {
            UserEntity user = userRepository.findById(ID).get();
//            LOGGER.info("User {} has been retrieved", user.getEmail());
            return ResponseEntity.ok(user);
        } else {
            map.put("object", "Error");
            map.put("status", 404);
            map.put("message", "User not found with ID: " + ID);
//            LOGGER.info("User not found with ID: {}", ID);
            return ResponseEntity.badRequest().body(map);
        }
    }

    public ResponseEntity<?> getAllUsers(){
        Map<String, Object> map = new LinkedHashMap<>();
        List<UserEntity> users = userRepository.findAll();
        if(users.isEmpty()){
            map.put("object", "Error");
            map.put("status", 404);
            map.put("message", "No users found");
//            LOGGER.info("No users found");
            return ResponseEntity.badRequest().body(map);
        } else {
//            LOGGER.info("All users have been retrieved");
            return ResponseEntity.ok(users);
        }
    }

    /*
    Update user details using the ID and the User object and then save the updated user details.
     */
    public ResponseEntity<?> updateUser(Long ID, JsonPatch patch){
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            UserEntity user = userRepository.findById(ID).orElseThrow(ChangeSetPersister.NotFoundException::new);
            UserEntity patchedUser = (applyPatchToUser(patch, user));
            userRepository.save(patchedUser);
//            LOGGER.info("User {} has been updated", patchedUser.getEmail());
            return ResponseEntity.ok(patchedUser);
        } catch (ChangeSetPersister.NotFoundException e) {
//            LOGGER.error("User not found with ID: {}", ID);
            map.put("object", "Error");
            map.put("status", 404);
            map.put("message", "User not found with ID: " + ID);
            return ResponseEntity.badRequest().body(map);
        } catch (JsonPatchException | JsonProcessingException e) {
//            LOGGER.error("Error updating user");
            map.put("object", "Error");
            map.put("status", 500);
            map.put("message", "Internal Server Error: JSON Patch Error");
            return ResponseEntity.internalServerError().body(map);
        } catch (Exception e) {
//            LOGGER.error("Something went wrong");
            map.put("object", "Error");
            map.put("status", 500);
            map.put("message", "Internal Server Error: Something went wrong");
            return ResponseEntity.internalServerError().body(map);
        }
    }

    /*
    Apply the patch to the user object.
     */
    private UserEntity applyPatchToUser(JsonPatch patch, UserEntity targetUser) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(new ObjectMapper().convertValue(targetUser, JsonNode.class));
//        LOGGER.info("Applying patch to user: {}", patched);
        return new ObjectMapper().treeToValue(patched, UserEntity.class);
    }

    /*
    Check if a user with the same email already exists in the database.
    If a user with the same email exists, return a ResponseEntity with a bad request status and a message indicating that the email already exists.
    If a user with the same email does not exist, create a new ArrayList of RoleEntity objects.
    Iterate over the roles of the user passed as an argument.
    For each role, retrieve the RoleEntity from the roleRepository using the role's name and add it to the roles list.
    Set the roles of the user.
    Save the user in the database.
    Return a ResponseEntity with an OK status and the saved user.
     */
    public ResponseEntity<?> createUser(UserEntity user) {
        Map<String, Object> map = new LinkedHashMap<>();
        if(userRepository.existsByEmail(user.getEmail())){
            map.put("object", "Error");
            map.put("status", 400);
            map.put("message", "Email: " + user.getEmail() + " already exists");
            return ResponseEntity.badRequest().body(map);
        } else if(userRepository.existsByUsername(user.getUsername())) {
            map.put("object", "Error");
            map.put("status", 400);
            map.put("message", "Username: " + user.getUsername() + " already exists");
            return ResponseEntity.badRequest().body(map);
        } else {
            List<RoleEntity> roles = new ArrayList<>();
            for(RoleEntity role : user.getRoles()){
                RoleEntity roleEntity = roleRepository.findByName(role.getName()).get();
                roles.add(roleEntity);
            }
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
//            LOGGER.info("User {} has been created", user.getEmail() + " - " + user.getUsername());
            return ResponseEntity.ok(userRepository.save(user));
        }
    }

    /*
    Delete existing user using the ID.
     */
    public ResponseEntity<?> deleteUser(Long Id){

        Map<String, Object> map = new LinkedHashMap<>();
        if (!getUserDetails(Id).getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.badRequest().body(getUserDetails(Id).getBody());
        } else {
            UserEntity user = userRepository.findById(Id).get();
            user.getRoles().clear(); // Clear the user's roles
            userRepository.save(user); // Save the user to update the "user_roles" table
            userRepository.deleteById(Id); // Now you can delete the user
//            LOGGER.info("User with {} has been deleted", " email: " + user.getEmail() + " - username: " + user.getUsername() + " - ID " + user.getId());
            map.put("message", "User " + Id + " is deleted!");
            return ResponseEntity.ok().body(map);

        }
    }
}

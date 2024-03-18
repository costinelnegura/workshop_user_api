package uk.co.negura.workshop_users_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.co.negura.workshop_users_api.model.AuthorityEntity;
import uk.co.negura.workshop_users_api.model.RoleEntity;
import uk.co.negura.workshop_users_api.model.UserEntity;
import uk.co.negura.workshop_users_api.repository.AuthorityRepository;
import uk.co.negura.workshop_users_api.repository.RoleRepository;
import uk.co.negura.workshop_users_api.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       AuthorityRepository authorityRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
            Get user details using the ID.
             */
    public ResponseEntity<?> getUserDetails(String email){
        return ResponseEntity.ok(userRepository.existsByEmail(email));
    }

    /*
    Update user details using the ID and the User object and then save the updated user details.
     */
    public ResponseEntity<?> updateUser(Long ID, JsonPatch patch){
        try {
            UserEntity user = userRepository.findById(ID).orElseThrow(ChangeSetPersister.NotFoundException::new);
            UserEntity patchedUser = (applyPatchToUser(patch, user));
            userRepository.save(patchedUser);
            return ResponseEntity.ok(patchedUser);
        } catch (ChangeSetPersister.NotFoundException e) {
            // Handle the case when the user is not found.
            return ResponseEntity.notFound().build();
        } catch (JsonPatchException | JsonProcessingException e) {
            // Handle JSON patch or processing exceptions.
            return ResponseEntity.internalServerError().body("Internal Server Error: JSON Patch Error");
        } catch (Exception e) {
            // Handle any other unexpected exceptions.
            return ResponseEntity.internalServerError().body("Internal Server Error: Something went wrong");
        }
    }

    /*
    Apply the patch to the user object.
     */
    private UserEntity applyPatchToUser(JsonPatch patch, UserEntity targetUser) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(new ObjectMapper().convertValue(targetUser, JsonNode.class));
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
        if(userRepository.existsByEmail(user.getEmail())){
            return ResponseEntity.badRequest().body("Email: " + user.getEmail() + " already exists");
        } else if(userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username: " + user.getUsername() + " already exists");
        } else {
            List<RoleEntity> roles = new ArrayList<>();
            List<AuthorityEntity> authorities = new ArrayList<>();
            for(RoleEntity role : user.getRoles()){
                RoleEntity roleEntity = roleRepository.findByName(role.getName()).get();
                roles.add(roleEntity);
                for(AuthorityEntity authority : roleEntity.getAuthorities()){
                    AuthorityEntity authorityEntity = authorityRepository.findByName(authority.getName()).get();
                    authorities.add(authorityEntity);
                }
            }
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return ResponseEntity.ok(userRepository.save(user));
        }
    }

    /*
    Delete existing user using the ID.
     */
    public ResponseEntity<?> deleteUser(Long Id){
        if (!userRepository.existsById(Id)){
            return ResponseEntity.badRequest().body("User not found with ID: " + Id);
        } else {
            UserEntity user = userRepository.findById(Id).get();
            user.getRoles().clear(); // Clear the user's roles
            userRepository.save(user); // Save the user to update the "user_roles" table
            userRepository.deleteById(Id); // Now you can delete the user
            return ResponseEntity.ok().body("User " + Id + " is deleted!");
        }
    }
}

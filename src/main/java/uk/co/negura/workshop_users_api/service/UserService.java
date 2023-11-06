package uk.co.negura.workshop_users_api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.co.negura.workshop_users_api.model.Authority;
import uk.co.negura.workshop_users_api.model.User;
import uk.co.negura.workshop_users_api.repository.UserRepository;

import java.util.Collection;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
    Get user details using the ID.
     */
    public ResponseEntity<?> getUserDetails(Long ID){
        return ResponseEntity.ok(userRepository.findById(Long.valueOf(ID)));
    }

    /*
    Update user details using the ID and the User object and then save the updated user details.
     */
    public ResponseEntity<?> updateUser(Long ID, JsonPatch patch){
        try {
            User user = userRepository.findById(ID).orElseThrow(ChangeSetPersister.NotFoundException::new);
            User patchedUser = (applyPatchToUser(patch, user));
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
    private User applyPatchToUser(JsonPatch patch, User targetUser) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(new ObjectMapper().convertValue(targetUser, JsonNode.class));
        return new ObjectMapper().treeToValue(patched, User.class);
    }

    /*
    Create new user if the email does not exist in the database.
     */
    public ResponseEntity<?> createUser(User user, Collection<Authority> authorities) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("Email: " + user.getEmail() + " already exists");
        } else {
            user.setAuthorities(authorities);
            return ResponseEntity.ok(userRepository.save(user));
        }
    }

    /*
    Delete existing user using the ID.
     */
    public ResponseEntity<?> deleteUser(Long ID){
        if (!userRepository.existsById(ID)){
            return ResponseEntity.badRequest().body("User not found with ID: " + ID);
        } else {
            return ResponseEntity.ok().body("User " + ID + " deleted!");
        }
    }
}

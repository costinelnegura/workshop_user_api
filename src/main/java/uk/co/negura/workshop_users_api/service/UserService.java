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
import uk.co.negura.workshop_users_api.model.User;
import uk.co.negura.workshop_users_api.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /*
    Get user details using the ID.
     */
    public ResponseEntity<?> getUserDetails(String ID){
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

    private User applyPatchToUser(JsonPatch patch, User targetUser) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(new ObjectMapper().convertValue(targetUser, JsonNode.class));
        return new ObjectMapper().treeToValue(patched, User.class);
    }

    /*
    Create new user if the email does not exist in the database.
     */
    public ResponseEntity<?> createUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("Email: " + user.getEmail() + " already exists");
        } else {
            return ResponseEntity.ok(userRepository.save(user));
        }
    }

    /*
    Delete existing user using the ID.
     */
    public ResponseEntity<?> deleteUser(String ID){
        if (!userRepository.existsById(Long.valueOf(ID))){
            return ResponseEntity.badRequest().body("User not found with ID: " + ID);
        } else {
            return ResponseEntity.ok().body("User " + ID + " deleted!");
        }
    }
}

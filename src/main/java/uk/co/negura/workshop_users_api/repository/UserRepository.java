package uk.co.negura.workshop_users_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.negura.workshop_users_api.model.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}

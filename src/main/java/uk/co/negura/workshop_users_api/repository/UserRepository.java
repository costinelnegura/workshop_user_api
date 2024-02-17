package uk.co.negura.workshop_users_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.negura.workshop_users_api.model.UserEntity;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

}

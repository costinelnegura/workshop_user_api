package uk.co.negura.workshop_users_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.negura.workshop_users_api.model.AuthorityEntity;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

    Optional<AuthorityEntity> findByName(String name);
}

package uk.co.negura.workshop_users_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.negura.workshop_users_api.model.RoleEntity;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

    void deleteById(Long Id);

}

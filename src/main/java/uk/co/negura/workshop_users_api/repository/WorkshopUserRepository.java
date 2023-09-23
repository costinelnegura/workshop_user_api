package uk.co.negura.workshop_users_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.negura.workshop_users_api.model.User;

import java.util.Optional;

@Repository
public interface WorkshopUserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);

}

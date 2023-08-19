package uk.co.negura.workshop_users_api.auth;

import org.springframework.context.annotation.Bean;

import java.util.Optional;

public interface WorkshopUserDAO {

    Optional<WorkshopUser> selectWorkshopUserByUserName (String userName);
}

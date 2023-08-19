package uk.co.negura.workshop_users_api.auth;

import java.util.Optional;

public interface WorkshopUserDAO {

    Optional<WorkshopUser> selectWorkshopUserByUserName (String userName);
}

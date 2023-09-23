package uk.co.negura.workshop_users_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uk.co.negura.workshop_users_api.model.User;
import uk.co.negura.workshop_users_api.repository.WorkshopUserRepository;

import java.util.ArrayList;

@Service
public class WorkshopUserService implements UserDetailsService {

    private final WorkshopUserRepository workshopUserRepository;

    /*
    Using the @Qualifier allows the application to switch between different implementations to connect to different
    databases by modifying the qualifier's name.
     */

    @Autowired
    public WorkshopUserService(WorkshopUserRepository workshopUserRepository) {
        this.workshopUserRepository = workshopUserRepository;
    }

    public static WorkshopUserService createWorkshopUserService(User user) {
        return new WorkshopUserService((WorkshopUserRepository) user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = workshopUserRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}

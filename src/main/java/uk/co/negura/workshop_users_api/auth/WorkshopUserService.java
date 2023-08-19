package uk.co.negura.workshop_users_api.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WorkshopUserService implements UserDetailsService {

    private final WorkshopUserDAO workshopUserDAO;

//    public WorkshopUserService() {
//        this(null);
//    }

    @Autowired
    public WorkshopUserService(@Qualifier("fake") WorkshopUserDAO workshopUserDAO) {
        this.workshopUserDAO = workshopUserDAO;
    }

    public static WorkshopUserService createWorkshopUserService(WorkshopUserDAO workshopUserDAO) {
        return new WorkshopUserService(workshopUserDAO);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return workshopUserDAO.selectWorkshopUserByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found ", userName)));
    }
}

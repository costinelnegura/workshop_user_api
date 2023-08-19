package uk.co.negura.workshop_users_api.auth;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uk.co.negura.workshop_users_api.security.WorkshopUserRole.*;

@Repository("fake")
public class FaceWorkshopUserDAOService  implements WorkshopUserDAO{

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FaceWorkshopUserDAOService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<WorkshopUser> selectWorkshopUserByUserName(String userName) {
        return getWorkshopUsers()
                .stream()
                .filter(workshopUser -> userName.equals(workshopUser.getUsername()))
                .findFirst();
    }

    private List<WorkshopUser> getWorkshopUsers (){
        List<WorkshopUser> workshopUsers = Lists.newArrayList(
                new WorkshopUser(
                        "costinelnegura",
                        passwordEncoder.encode("password"),
                        STUDENT.getGrantedAuthoritySet(),
                        true,
                        true,
                        true,
                        true
                ),
                new WorkshopUser(
                        "alexandradedu",
                        passwordEncoder.encode("password"),
                        ADMIN.getGrantedAuthoritySet(),
                        true,
                        true,
                        true,
                        true
                ),
                new WorkshopUser(
                        "andreeanegura",
                        passwordEncoder.encode("password"),
                        ADMINTRAINEE.getGrantedAuthoritySet(),
                        true,
                        true,
                        true,
                        true
                )
        );
        return workshopUsers;
    }
}
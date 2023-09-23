package uk.co.negura.workshop_users_api.Repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import uk.co.negura.workshop_users_api.model.User;
import uk.co.negura.workshop_users_api.repository.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testCreateUser() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        var newUser = new User("costinel.g", encodedPassword, "costinel@negura.com");
        var savedUser = userRepository.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);


    }

}

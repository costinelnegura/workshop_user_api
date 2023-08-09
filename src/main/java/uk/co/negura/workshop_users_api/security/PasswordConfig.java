package uk.co.negura.workshop_users_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    /*
    This method encrypts the password string.
     */
    @Bean
     public PasswordEncoder passwordEncoder(){
         return new BCryptPasswordEncoder(10);
     }
}

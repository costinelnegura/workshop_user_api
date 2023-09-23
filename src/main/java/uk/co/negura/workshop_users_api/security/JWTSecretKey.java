package uk.co.negura.workshop_users_api.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.negura.workshop_users_api.security.JWTConfig;

import javax.crypto.SecretKey;

@Configuration
public class JWTSecretKey {

    private final JWTConfig jwtConfig;

    @Autowired
    public JWTSecretKey(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Bean
    public SecretKey getSecretKey (){
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }
}

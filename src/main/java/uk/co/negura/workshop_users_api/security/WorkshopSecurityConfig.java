package uk.co.negura.workshop_users_api.security;


/*
This class uses Basic Auth method.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.co.negura.workshop_users_api.JWT.JWTConfig;
import uk.co.negura.workshop_users_api.JWT.JWTTokenVerifier;
import uk.co.negura.workshop_users_api.JWT.JWTUsernameAndPasswordAuthFilter;
import uk.co.negura.workshop_users_api.auth.WorkshopUserService;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WorkshopSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final WorkshopUserService workshopUserService;
    private final SecretKey secretKey;
    private final JWTConfig jwtConfig;

    @Autowired
    public WorkshopSecurityConfig(PasswordEncoder passwordEncoder, WorkshopUserService workshopUserService, SecretKey secretKey, JWTConfig jwtConfig) {
        this.passwordEncoder = passwordEncoder;
        this.workshopUserService = workshopUserService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // This ensures that the token is not saved is a database
                .and()
                .addFilter(new JWTUsernameAndPasswordAuthFilter(authenticationManager(), jwtConfig, secretKey))
                .addFilterAfter(new JWTTokenVerifier(secretKey, jwtConfig), JWTUsernameAndPasswordAuthFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index.html")
                .permitAll()
                .antMatchers("/api/**")
                .hasRole(WorkshopUserRole.STUDENT.name())
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(workshopUserService);
        return provider;
    }

}
